package common.commands.concreteCommands.clientOnly;


import client.reading.readers.OfflineReader;
import client.reading.readers.Reader;
import common.commands.abstraction.Command;
import common.exceptions.inputExceptions.InputException;
import common.exceptions.inputExceptions.UnknownCommandException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Deque;

import static client.App.server;
import static server.launcher.CommandsLauncher.currentScripts;

/**
 * execute_script command
 */
public class ExecuteScript extends Command {
    private File script;
    private OfflineReader offlineReader;
    private boolean isOk = true;
    private static final Deque<Command> currentCms = new ArrayDeque<>();

    public ExecuteScript() {
        send = false;
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);
        script = new File(from.readString());
        if (currentScripts.contains(script)) {
            System.out.println("Script is already compiling. Command " + this + " was skipped");
            isOk = false;
        }

        try {
            offlineReader = new OfflineReader(new FileInputStream(script), from.getObjectTree());
        } catch (FileNotFoundException e) {
            System.out.println("File " + script + " does not exist, or can't be accessed");
            isOk = false;
        }
    }


    /**
     * @return true if script compiled successfully
     */
    @Override
    public Object execute() {

        if (!this.isOk) {
            return false;
        }
        currentScripts.add(script);
        while (offlineReader.hasNext()) {
            Command met = null;
            try {
                met = offlineReader.readCommand();
            } catch (UnknownCommandException e) {
                System.out.println("No such command: " + e.getMessage() + " command was skipped");
            }
            try {
                assert met != null;
                if (met.ifSend()) {
                    server.sendCommand(met);
                    System.out.println(server.getResponse());
                } else {
                    met.execute();
                }
            } catch (InputException e) {
                System.out.println(e.getMessage() + " Command " + met + " was skipped");
                offlineReader.skipTillNextCommand();
            }
        }
        offlineReader.closeStream();
        currentScripts.remove(script);
        return "Script " + script + " has compiled";
    }

    @Override
    public String toString() {
        String res = "execute_script";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
