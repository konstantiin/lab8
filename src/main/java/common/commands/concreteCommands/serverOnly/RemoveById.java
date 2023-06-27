package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import java.math.BigInteger;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * remove_by_id command
 */
public class RemoveById extends Command {
    long id;

    @Override
    public Object execute() {
        if (collection.removeById(user, id)) {
            return "Element removed";
        } else {
            return "Element was not removed";
        }
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

        id = from.readInt(BigInteger.ZERO, BigInteger.valueOf(Long.MAX_VALUE)).longValue();
    }

    @Override
    public String toString() {
        String res = "remove_by_id";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
