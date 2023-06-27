package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * clear command
 */
public class Clear extends Command {

    @Override
    public Object execute() {
        return collection.clear();
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);
    }

    @Override
    public String toString() {
        String res = "clear";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
