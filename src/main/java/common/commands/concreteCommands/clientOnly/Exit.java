package common.commands.concreteCommands.clientOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * exit
 */
public class Exit extends Command {
    private Reader input;

    public Exit() {
        send = false;
    }

    @Override
    public Object execute() {
        input.closeStream();
        StringBuilder res = new StringBuilder();
        res.append("Exit ");
        if (currentScripts.size() != 0)
            res.append("from ").append(currentScripts.get(currentScripts.size() - 1)).append(" script");
        res.append("complete");
        return res;
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);
        input = from;
    }

    @Override
    public String toString() {
        String res = "exit";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
