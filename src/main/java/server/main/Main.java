package server.main;


import common.commands.abstraction.Command;
import common.connection.ObjectByteArrays;
import common.storedClasses.HumanBeing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.connection.ConnectToClient;
import server.launcher.CommandsLauncher;

import java.io.Serializable;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final AtomicBoolean work = new AtomicBoolean(true);

    public static void runConsole(CommandsLauncher<?> collection) {
        var r = new Runnable() {
            @Override
            public void run() {
                Scanner scan = new Scanner(System.in);
                while (work.get()) {
                    String cmd = scan.next();
                    work.set(collection.runServerCommand(cmd));
                }
                scan.close();
            }
        };
        new Thread(r).start();
    }

    public static void test(CommandsLauncher<?> c) {
        System.out.println(c.register("Admin", "123"));
        System.out.println(c.clear());
        System.out.println(c.show());
        System.out.println(c.add("Admin", new HumanBeing()));
        System.out.println(c.show());
        System.out.println(c.removeById("Admin", 1));
        System.out.println(c.show());
        System.out.println(c.add("Admin", new HumanBeing()));
        System.out.println(c.update("Admin", 2, new HumanBeing()));

    }

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        var server = new ConnectToClient(port);
        System.out.println("Server is running. Port " + port);
        logger.info("Server is running. Port " + port);
        CommandsLauncher<HumanBeing> collection = CommandsLauncher.getHumanBeingLauncher();
        runConsole(collection);
        //test(collection);
        var ex = Executors.newCachedThreadPool();
        while (work.get()) {
            try {
                ConnectToClient.readAll(server);
                var input = server.getInputObjects();
                input.forEach((key, value) -> {
                    Runnable r = () -> {
                        var cmd = (Command) value.toObject();
                        try {
                            cmd.setCollection(collection);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        server.setAnswer(key,
                                ObjectByteArrays.getArrays((Serializable) cmd.execute()));
                        server.regWrite(key);

                    };
                    ex.submit(r);
                });

                ConnectToClient.writeAll(server);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                logger.error("Unknown error" + e.getMessage());
                throw new RuntimeException(e);
            }

        }
        collection.close();
        server.close();
    }
}
