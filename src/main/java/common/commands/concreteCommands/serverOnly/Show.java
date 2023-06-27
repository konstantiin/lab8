package common.commands.concreteCommands.serverOnly;


import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * show command
 */
public class Show extends Command {
    @Override
    public Object execute() {
        return collection.show();
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

    }

    @Override
    public String toString() {
        String res = "show";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
