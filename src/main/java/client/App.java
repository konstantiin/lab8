package client;


import client.connection.ConnectToServer;
import client.reading.objectTree.Node;
import client.reading.readers.OnlineReader;
import common.commands.abstraction.Command;
import common.commands.concreteCommands.serverOnly.Register;
import common.commands.concreteCommands.serverOnly.SignUp;
import common.exceptions.inputExceptions.InputException;
import common.exceptions.inputExceptions.UnknownCommandException;
import common.storedClasses.forms.HumanBeingForm;

import java.util.Objects;
import java.util.Scanner;


public class App {
    public static String user;
    public static ConnectToServer server;
    public static OnlineReader console;

    public static void register(ConnectToServer server) {

        var scanner = new Scanner(System.in);
        boolean need = true;
        while (need) {
            System.out.println("Register or sign up?");
            String i = scanner.nextLine().trim().toLowerCase();
            Command c;
            String name;
            System.out.print("Type user name: ");
            name = scanner.nextLine().trim();
            if (i.equals("register")) {
                boolean notConf = true;
                String pas = "";
                while (notConf) {
                    System.out.print("Type password: ");
                    pas = scanner.nextLine().trim();
                    System.out.println();
                    System.out.print("Confirm password: ");
                    var conf = scanner.nextLine().trim();
                    System.out.println();
                    if (!Objects.equals(pas, conf)) {
                        System.out.println("Passwords does not match. Try again");
                    } else {
                        notConf = false;
                    }
                }

                c = new Register(name, pas);

            } else {

                System.out.print("Type password: ");
                String pas = scanner.nextLine();
                System.out.println();
                c = new SignUp(name, pas);

            }
            System.out.println(c);
            server.sendCommand(c);
            Object e = server.getResponse();
            if ((Boolean) e) {
                System.out.println("Success!");
                user = name;
                need = false;
            } else {
                System.out.println("Something went wrong!");
            }
        }
    }

    /**
     * main method
     * creates managed collection, parses xml file and execute commands from System.in
     */
    public static void main(String[] args) {

        int port = Integer.parseInt(args[1]);
        server = ConnectToServer.getServer(args[0], port);
        if (server == null) {
            System.out.println("Connection failed.");
            return;
        }

        console = new OnlineReader(System.in, Node.generateTree(HumanBeingForm.class, "HumanBeing"));
        register(server);
        while (console.hasNext()) {
            Command met = null;
            try {
                met = console.readCommand();
            } catch (UnknownCommandException e) {
                System.out.println("Command not found, type \"help\" for more info");
            } catch (InputException e) {
                console.renewScan(System.in);
            }
            if (met != null) {
                if (met.ifSend()) {
                    try {
                        server.sendCommand(met);
                        Object e = server.getResponse();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Execution ended");
                        console.closeStream();
                    }

                } else {
                    met.execute();
                }
            }
        }

    }
}