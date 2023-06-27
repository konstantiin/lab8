package common.commands.concreteCommands.serverOnly;


import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * sum_of_impact_speed command
 */
public class SumOfImpactSpeed extends Command {
    @Override
    public Object execute() {
        return collection.sumOfImpactSpeed();
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

    }

    @Override
    public String toString() {
        String res = "sum_of_impact_speed";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
