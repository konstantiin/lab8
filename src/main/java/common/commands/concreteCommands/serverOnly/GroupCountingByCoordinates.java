package common.commands.concreteCommands.serverOnly;


import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * group_counting_by_coordinates
 */
public class GroupCountingByCoordinates extends Command {

    @Override
    public Object execute() {
        return collection.groupCountingByCoordinates();
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

    }

    @Override
    public String toString() {
        String res = "group_counting_by_coordinates";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
