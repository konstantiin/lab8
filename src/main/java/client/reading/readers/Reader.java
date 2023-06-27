package client.reading.readers;

import client.App;
import client.reading.objectTree.Node;
import common.commands.abstraction.Command;
import common.commands.concreteCommands.clientOnly.ExecuteScript;
import common.commands.concreteCommands.clientOnly.Exit;
import common.commands.concreteCommands.clientOnly.Help;
import common.commands.concreteCommands.serverOnly.*;
import common.exceptions.inputExceptions.*;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public abstract class Reader {
    /**
     * List of classes representing numbers
     */
    public static List<Class<?>> numbers = new ArrayList<>(Arrays.asList(Long.class, long.class, Integer.class, int.class,
            Float.class, float.class, Double.class, double.class,
            Short.class, short.class));

    /**
     * Hash map with common.commands
     */
    protected final HashMap<String, Command> commands = new HashMap<>();
    /**
     * Node, which is pointing to root of Object tree
     */
    protected final Node objectTree;
    /**
     * Scanner from which data will be taken
     */
    protected Scanner scan;
    /**
     * variable in which stores current depth.
     */
    protected int tabs = 0;

    /**
     * @param source - input stream
     * @param tree   - tree to read
     */
    public Reader(InputStream source, Node tree) {
        this.scan = new Scanner(source);
        objectTree = tree;
        this.initCommands();
    }

    /**
     * @return next string item
     */
    protected abstract String getNext();

    /**
     * reads string
     *
     * @return next string
     */
    public String readString() {
        String input = getNext().trim();
        if (input.equals("")) throw new EmptyStringException();
        return input;
    }

    /**
     * reads integer
     *
     * @return integer value between lowerBound and upperBound
     * @throws OutOfBoundsException - if input is out of bounds
     */
    public BigInteger readInt(BigInteger lowerBound, BigInteger upperBound) throws OutOfBoundsException {
        BigInteger value;
        try {
            value = new BigInteger(getNext().trim());
        } catch (NumberFormatException e) {
            throw new WrongInputException("value should be an integer number!");
        }
        if (value.compareTo(lowerBound) < 0 || value.compareTo(upperBound) > 0) {
            throw new OutOfBoundsException();
        }
        return value;
    }

    /**
     * reads decimal
     *
     * @return decimal value between lowerBound and upperBound
     * @throws OutOfBoundsException - if input is out of bounds
     */
    public BigDecimal readDec(BigDecimal lowerBound, BigDecimal upperBound) throws OutOfBoundsException {
        BigDecimal value;
        try {
            value = new BigDecimal(getNext().trim());
        } catch (NumberFormatException e) {
            throw new WrongInputException("value should be a decimal number!");
        }
        if (value.compareTo(lowerBound) < 0 || value.compareTo(upperBound) > 0) {
            throw new OutOfBoundsException();
        }
        return value;
    }

    /**
     * reads boolean
     *
     * @return next boolean value
     * @throws WrongInputException - if there is no boolean value to read
     */
    public Boolean readBool() {
        String value = getNext().trim().toLowerCase();
        if (value.equals("0") || value.equals("false")) return false;
        else if (value.equals("1") || value.equals("true")) return true;
        throw new WrongInputException("value should be true or false!");
    }

    /**
     * reads next object, represented by object tree
     *
     * @return next object
     */
    public abstract Object readObject();

    /**
     * reads Enum value. Enum value could be either string, or integer.
     *
     * @param type - Enum to read
     * @return next value of Enum type
     */
    public Object readEnum(Class<?> type) {
        String name = getNext();
        try {
            if (StringUtils.isNumeric(name)) {
                return ((Object[]) type.getMethod("values").invoke(null))[Integer.parseInt(name) - 1];
            }
        } catch (Exception ignored) {
        }
        try {
            return type.getMethod("valueOf", String.class).invoke(null, name);//,tp ji
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(type.getName() + " is not Enum");
        } catch (InvocationTargetException e) {
            throw new EnumInputException(e);
        }
    }


    /**
     * initialize common.commands
     */
    private void initCommands() {
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("add", new Add());
        commands.put("update", new Update());
        commands.put("remove_by_id", new RemoveById());
        commands.put("clear", new Clear());
        commands.put("execute_script", new ExecuteScript());
        commands.put("exit", new Exit());
        commands.put("add_if_min", new AddIfMin());
        commands.put("remove_greater", new RemoveGreater());
        commands.put("remove_Lower", new RemoveLower());
        commands.put("sum_of_impact_speed", new SumOfImpactSpeed());
        commands.put("group_counting_by_coordinates", new GroupCountingByCoordinates());
        commands.put("filter_contains_name", new FilterContainsName());
    }

    /**
     * returns object tree
     *
     * @return tree root
     */
    public Node getObjectTree() {
        return this.objectTree;
    }

    /**
     * @return checks if stream has next value
     */
    public abstract boolean hasNext();


    /**
     * @param v - current node of object tree
     * @return true, if next item is null.
     */
    protected abstract boolean readNull(Node v);

    /**
     * @return true, if next item is not null
     */
    protected abstract boolean checkNotNullObject();

    /**
     * @param v -- current node of object tree
     * @return read object
     */
    protected Object readTree(Node v) {
        try {
            tabs++;
            if (v.ifNullable()) {
                if (readNull(v)) {
                    this.readLine();
                    tabs--;
                    return null;
                }
            }
            Class<?> cls = v.getType();
            Object result;
            if (cls == Float.class || cls == float.class) {
                result = this.readDec(v.getLowerBound(), v.getUpperBound()).floatValue();
            } else if (cls == Long.class || cls == long.class) {
                result = this.readInt(v.getLowerBound().toBigInteger(), v.getUpperBound().toBigInteger()).longValue();
            } else if (cls == Integer.class || cls == int.class) {
                result = this.readInt(v.getLowerBound().toBigInteger(), v.getUpperBound().toBigInteger()).intValue();
            } else if (cls == Boolean.class) {
                result = this.readBool();
            } else if (cls == String.class) {
                result = this.readString();
            } else if (cls.isEnum()) {
                result = this.readEnum(cls);
            } else {

                if (checkNotNullObject()) {
                    throw new NullObjectException("Field \"" + v.getName() + "\" can't be null!");
                }
                this.readLine();
                HashMap<String, Object> fields = new HashMap<>();
                for (Node i : v.getFields()) {
                    fields.put(i.getName(), readTree(i));
                }
                result = v.getObjectGenerator().generate(fields);

            }
            tabs--;
            return result;
        } catch (EmptyStringException e) {
            throw new InputException("Field \"" + v.getName() + "\" should not be empty!");
        } catch (EnumInputException e) {
            throw new InputException("Field \"" + v.getName() + "\" should be one of " + v.getType() + " values!");
        } catch (OutOfBoundsException e) {
            throw new InputException("Field \"" + v.getName() + "\" should be between " + v.getLowerBound() + " and " + v.getUpperBound() + "!");
        } catch (WrongInputException e) {
            throw new InputException("Field \"" + v.getName() + "\" " + e.getMessage());
        }
    }

    /**
     * reads next line
     */
    protected void readLine() {                                // мб стоит с этим что-то сделать, но пока что пусть будет так
    }

    /**
     * reads command
     *
     * @return next command
     */
    public Command readCommand() {
        String metName = getNext().trim();
        Command command = commands.get(metName);
        if (command == null) {
            throw new UnknownCommandException(metName);
        }
        command.setArgs(App.user, this);
        return command;
    }

    /**
     * closes scanner's stream
     */
    public void closeStream() {
        scan.close();
    }
}


