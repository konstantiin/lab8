package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;
import common.storedClasses.HumanBeing;
import common.storedClasses.forms.HumanBeingForm;

import java.math.BigInteger;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * update command
 */
public class Update extends Command {
    private long id;
    private Object arg;

    @Override
    public Object execute() {
        if (collection.update(user, id, new HumanBeing((HumanBeingForm) arg))) {
            return "Element updated";
        } else {
            return "Element was not updated";
        }
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

        id = from.readInt(BigInteger.ZERO, BigInteger.valueOf(Integer.MAX_VALUE)).longValue();
        arg = from.readObject();
    }

    @Override
    public String toString() {
        String res = "update";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
