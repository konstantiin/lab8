package common.commands.concreteCommands.serverOnly;

import client.reading.readers.Reader;
import common.commands.abstraction.Command;

public class Register extends Command {
    private final String name, password;

    public Register(String n, String pass) {
        this.name = n;
        this.password = pass;
    }

    @Override
    public Object execute() {
        return collection.register(name, password);
    }

    @Override
    public void setArgs(String user, Reader from) {
        super.setArgs(user, from);

        // do nothing
    }

    @Override
    public String toString() {
        return "Register " + name + " " + password;
    }
}
