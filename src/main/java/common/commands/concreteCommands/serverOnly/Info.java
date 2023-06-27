package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import java.io.Serializable;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * info command
 */
public class Info extends Command implements Serializable {

    @Override
    public Object execute() {
        return collection.info();
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

    }

    @Override
    public String toString() {
        String res = "info";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
