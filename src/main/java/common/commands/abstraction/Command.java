package common.commands.abstraction;

import client.reading.readers.Reader;
import server.launcher.CommandsLauncher;

import java.io.Serializable;

/**
 * Abstract command class
 */
public abstract class Command implements Serializable {
    protected CommandsLauncher<?> collection;
    protected boolean send = true;

    /**
     * default constructor, initialize fields as null
     */
    public boolean ifSend() {
        return send;
    }

    synchronized public void setCollection(CommandsLauncher<?> collect) {
        collection = collect;
    }

    protected String user;


    /**
     * executes command
     */
    public abstract Object execute();

    /**
     * sets arguments
     */
    public void setArgs(String user, Reader from) {
        this.user = user;
    }

    @Override
    public abstract String toString();
}
