package server.connection;


import common.connection.ObjectByteArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.main.Main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectToClient {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final DatagramChannel channel;
    private final Selector selector;


    private final Map<SocketAddress, ObjectByteArrays> currentInput = new HashMap<>();
    private final Map<SocketAddress, ObjectByteArrays> answerToClient = new ConcurrentHashMap<>();

    private final Map<SocketAddress, Boolean> needConfirm = new HashMap<>();
    private final Map<SocketAddress, SelectionKey> keysAndClients = new ConcurrentHashMap<>();

    synchronized public void setAnswer(SocketAddress client, ObjectByteArrays data) {
        answerToClient.put(client, data);
    }

    private final List<SocketAddress> complete = new ArrayList<>();

    public ConnectToClient(int port) {
        SocketAddress addr = new InetSocketAddress(port);
        try {
            channel = DatagramChannel.open();
            channel.bind(addr);
            selector = Selector.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<SocketAddress, ObjectByteArrays> getInputObjects() {
        Map<SocketAddress, ObjectByteArrays> res = new HashMap<>();
        for (var i : complete) {
            var p = currentInput.get(i);
            res.put(i, p);
            currentInput.remove(i);
        }
        assert res.size() == complete.size();
        complete.clear();
        return res;
    }

    public Set<SelectionKey> getKeys() {
        try {
            selector.selectNow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return selector.selectedKeys();
    }

    private void confirm(SocketAddress client) {

        try {
            channel.send(ByteBuffer.wrap(new byte[]{1}), client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void isConfirmed(byte[] in, SocketAddress client, SelectableChannel datagramChannel) {
        if (in.length != 1 || in[0] != 1) {
            answerToClient.get(client).iterBack();
        }
        if (!answerToClient.get(client).hasNext()) {
            needConfirm.put(client, false);
            try {
                datagramChannel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
            return;

        }
        regWrite(client);
    }

    public void read(SelectionKey key) {
        var datagramChannel = (DatagramChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(ObjectByteArrays.packageSize);
        SocketAddress client;
        try {
            client = datagramChannel.receive(buf);
        } catch (IOException e) {
            return;
        }
        buf.flip();
        byte[] in = new byte[buf.remaining()];
        buf.get(in);
        keysAndClients.put(client, key);
        if (needConfirm.containsKey(client) && needConfirm.get(client)) {
            isConfirmed(in, client, datagramChannel);
            key.attach(client);
            return;
        }
        if (currentInput.containsKey(client)) {
            boolean ifReady = !currentInput.get(client).addNext(in);
            if (ifReady) {
                complete.add(client);
                key.interestOps(0);
            }
        } else {
            currentInput.put(client, ObjectByteArrays.getEmpty(ByteBuffer.wrap(in).getInt()));
        }
        confirm(client);
        key.attach(client);
    }

    synchronized public void regWrite(SocketAddress addr) {
        try {
            keysAndClients.get(addr).channel().register(selector, SelectionKey.OP_WRITE);
            keysAndClients.get(addr).attach(addr);
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readAll(ConnectToClient server) {
        var keys = server.getKeys();
        if (keys.size() > 2) System.out.println(keys.size());
        for (var iter = keys.iterator(); iter.hasNext(); ) {
            var key = iter.next();
            iter.remove();

            if (key.isValid() && key.isReadable()) {
                try {
                    server.read(key);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("While reading from client:" + e.getMessage());

                }
            }
        }
    }

    public void write(SelectionKey key) {

        var client = (SocketAddress) key.attachment();
        var datagramChannel = (DatagramChannel) key.channel();
        var data = answerToClient.get(client);
        var pack = data.getNext();

        try {
            datagramChannel.send(ByteBuffer.wrap(pack), client);
        } catch (IOException e) {
            try {
                datagramChannel.register(selector, SelectionKey.OP_WRITE);
            } catch (ClosedChannelException ex) {
                throw new RuntimeException(ex);
            }
            data.iterBack();
        }

        needConfirm.put(client, true);
        try {
            datagramChannel.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        }

    }

    public static void writeAll(ConnectToClient server) {
        var keys = server.getKeys();
        for (var iter = keys.iterator(); iter.hasNext(); ) {
            var key = iter.next();
            iter.remove();
            if (key.isValid() && key.isWritable()) {
                try {
                    server.write(key);
                } catch (Exception e) {
                    //e.printStackTrace();
                    logger.error("While writing to client:" + e.getMessage());
                }
            }
        }
    }

    public void close() {
        try {
            channel.close();
            selector.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
