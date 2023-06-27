package common.commands.concreteCommands.clientOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static server.launcher.CommandsLauncher.currentScripts;

/**
 * help command
 */
public class Help extends Command {
    public Help() {
        send = false;
    }

    @Override
    public Object execute() {
        try (InputStream inputStream = getClass().getResourceAsStream("/help.txt")) {
            assert inputStream != null;
            Scanner help = new Scanner(inputStream);
            while (help.hasNextLine()) System.out.println(help.nextLine()); // return string
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);
    }

    @Override
    public String toString() {
        String res = "help";
        if (currentScripts.size() != 0) {
            res += "(in " + currentScripts.get(currentScripts.size() - 1) + " script)";
        }
        return res;
    }
}
