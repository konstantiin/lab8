package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * filter_contains_name
 */
public class FilterContainsName extends Command {

    private String name;

    @Override
    public Object execute() {
        var list = collection.filterContainsName(name);
        if (list.length == 0) return "Nothing matches pattern";
        return list;
    }

    @Override
    public void setArgs(String user, Reader from) {

        super.setArgs(user, from);
        name = from.readString();
    }

    @Override
    public String toString() {
        String res = "filter_contains_name";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
