package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;
import common.storedClasses.HumanBeing;
import common.storedClasses.forms.HumanBeingForm;

import static server.launcher.CommandsLauncher.currentScripts;


/**
 * add command
 */
public class Add extends Command {

    private Object arg;

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);
        arg = from.readObject();
    }

    @Override
    public Object execute() {
        if (collection.add(user, new HumanBeing((HumanBeingForm) arg))) {
            return "Element added";
        } else {
            return "Element not added";
        }
    }

    @Override
    public String toString() {
        String res = "add";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }


}
