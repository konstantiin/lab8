package client.connection;

import common.commands.abstraction.Command;
import common.connection.ObjectByteArrays;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Scanner;


public class ConnectToServer { // close scanner
    private final int timeOutSec = 10;
    private static boolean isOk = true;

    private final InetSocketAddress socket;
    private final DatagramChannel channel;

    private ConnectToServer(InetAddress host, int port) throws IOException {
        socket = new InetSocketAddress(host, port);
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
    }

    private static boolean ifReload() {
        if (!isOk) return false;
        System.out.println("Server error. Retry?(y/n)");
        var s = new Scanner(System.in);
        isOk = s.next().charAt(0) == 'y';
        return isOk;
    }

    public static ConnectToServer getServer(String hostName, int port) {
        boolean needConnect = true;
        ConnectToServer server = null;
        while (needConnect) {
            try {
                InetAddress host;
                if (Objects.equals(hostName, "localhost")) {
                    host = InetAddress.getLocalHost();
                } else {
                    host = InetAddress.getByName(hostName);
                }
                server = new ConnectToServer(host, port);
                needConnect = false;
            } catch (IOException e) {
                needConnect = ifReload();
            }
        }
        return server;
    }

    private byte[] receive() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(ObjectByteArrays.packageSize);
            SocketAddress res = null;
            var cur = LocalDateTime.now();

            while (res == null) {
                if (LocalDateTime.now().getSecond() - cur.getSecond() > timeOutSec) {
                    throw new IOException();
                }
                res = channel.receive(buf);
            }
            confirm();
            buf.flip();
            byte[] ans = new byte[buf.remaining()];
            System.arraycopy(buf.array(), 0, ans, 0, ans.length);
            return ans;
        } catch (IOException e) {
            if (ifReload()) {
                this.receive();
            } else {
                throw new RuntimeException(e);
            }
        }
        return new byte[0];
    }

    private ObjectByteArrays receiveArrays(ObjectByteArrays data) {
        boolean work = true;
        while (work) {
            work = data.addNext(this.receive());
        }

        return data;
    }

    public Object getResponse() {
        int size = ByteBuffer.wrap(this.receive()).getInt();
        var data = ObjectByteArrays.getEmpty(size);
        return this.receiveArrays(data).toObject();
    }

    private boolean isConfirmed() {
        byte[] arr = new byte[1];
        var cur = LocalDateTime.now();
        while (LocalDateTime.now().getSecond() - cur.getSecond() < timeOutSec) {
            try {
                var from = channel.receive(ByteBuffer.wrap(arr));
                if (!socket.equals(from)) throw new IOException();
                return arr[0] == 1;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

    private void confirm() {
        sendArr(new byte[]{1});
    }

    private void sendArr(byte[] d) {
        try {
            channel.send(ByteBuffer.wrap(d), socket);

        } catch (IOException e) {
            if (ifReload()) {
                sendArr(d);
            }
            throw new RuntimeException(e);
        }
    }

    private void sendArrays(ObjectByteArrays data) {
        byte[] next = data.getNext();
        while (next.length != 0) {
            sendArr(next);
            if (!isConfirmed()) {
                if (!ifReload()) throw new RuntimeException(new IOException());
            } else {
                next = data.getNext();
            }
        }
    }

    public void sendCommand(Command command) {
        this.sendArrays(ObjectByteArrays.getArrays(command));
    }
}
