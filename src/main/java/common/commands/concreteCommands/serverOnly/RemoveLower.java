package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;
import common.storedClasses.HumanBeing;
import common.storedClasses.forms.HumanBeingForm;

import java.io.Serializable;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * remove_lower command
 */
public class RemoveLower extends Command implements Serializable {

    private Object arg;

    @Override
    public Object execute() {
        return collection.removeLower(user, new HumanBeing((HumanBeingForm) arg));
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

        arg = from.readObject();
    }

    @Override
    public String toString() {
        String res = "remove_lower";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
