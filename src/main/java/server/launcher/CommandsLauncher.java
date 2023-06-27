package server.launcher;


import common.storedClasses.Coordinates;
import common.storedClasses.HumanBeing;
import server.dataBase.DbConnection;

import java.io.File;
import java.util.*;


/**
 * realizes all the commands
 *
 * @param <T> - stored class
 */
public class CommandsLauncher<T extends Comparable<T>> {
    /**
     * scripts that are currently executing
     */
    public static ArrayList<File> currentScripts = new ArrayList<>();
    /**
     * managed collection
     */
    private final SortedSet<T> collection;
    private final DbConnection psqlBase;

    /**
     * @param col - managed collection
     */
    private CommandsLauncher(TreeSet<T> col, DbConnection Db) {

        this.collection = Collections.synchronizedSortedSet(col);
        this.psqlBase = Db;
    }

    public static CommandsLauncher<HumanBeing> getHumanBeingLauncher() {
        var psqlBase = new DbConnection();
        List<HumanBeing> col = psqlBase.readDb();
        return new CommandsLauncher<>(new TreeSet<>(col), psqlBase);
    }

    /**
     * adds element to collection
     *
     * @param element - element to add
     */
    @SuppressWarnings("unchecked")
    public Boolean add(String user, Object element) {
        if (collection.contains((T) element)) return true;
        var res = psqlBase.addElements(user, (HumanBeing) element);
        if (res) collection.add((T) element);
        return res;
    }

    /**
     * adds element if it is less than any element in collection
     *
     * @param element - element to add
     * @return true if element was added
     */
    @SuppressWarnings("unchecked")

    public Boolean addIfMin(String user, Object element) {
        T value = (T) element;
        boolean res = false;
        if (value.compareTo(collection.first()) < 0) {
            res = this.add(user, value);
        }
        return res;
    }

    /**
     * returns elements with given substring in their names
     *
     * @param pattern - substring to search in names
     * @return list of elements with give substring in names
     */
    public Object[] filterContainsName(String pattern) {

        return collection.stream().filter(h ->
                ((HumanBeing) h).getName().contains(pattern)).toArray();
    }

    /**
     * groups HumanBeings by coordinates
     *
     * @return HumanBeings grouped by coordinates
     */
    public HashMap<Coordinates, List<HumanBeing>> groupCountingByCoordinates() {
        HashMap<Coordinates, List<HumanBeing>> groups = new HashMap<>();

        collection.forEach(h -> {
            var human = (HumanBeing) h;
            if (groups.containsKey(human.getCoordinates())) {
                groups.get(human.getCoordinates()).add(human);
            } else {
                var value = new ArrayList<HumanBeing>();
                value.add(human);
                groups.put(human.getCoordinates(), value);
            }
        });
        return groups;
    }

    /**
     * prints information about collection
     */
    public String info() {
        return "TreeSet of size " + collection.size();
    }

    /**
     * Deletes element with given id
     *
     * @param id - element with this id will be removed
     */
    public Boolean removeById(String user, long id) {
        long c = collection.stream().filter(h -> ((HumanBeing) h).getId() == id).count();
        if (c == 0) return false;
        var res = psqlBase.removeElements(user, id);
        if (res) {
            var l = collection.stream().filter(h -> ((HumanBeing) h).getId() == id).toArray();
            Arrays.stream(l).forEach(collection::remove);
        }
        return res;
    }

    public Boolean register(String name, String password) {
        return psqlBase.register(name, password);
    }

    public Boolean signUp(String name, String password) {
        return psqlBase.signUp(name, password);
    }

    /**
     * removes all the elements that are less than given element
     *
     * @param element - element to compare
     */
    @SuppressWarnings("unchecked")

    public Integer removeLower(String user, Object element) {
        T value = (T) element;
        long[] ids = collection.stream().filter(h -> h.compareTo(value) < 0).mapToLong(h -> ((HumanBeing) h).getId()).toArray();
        int count = 0;
        for (var id : ids) {
            if (this.removeById(user, id)) count += 1;
        }
        return count;
    }

    /**
     * removes all the elements that are greater than given element
     *
     * @param element - element to compare
     */
    @SuppressWarnings("unchecked")

    public Integer removeGreater(String user, Object element) {
        T value = (T) element;
        long[] ids = collection.stream().filter(h -> h.compareTo(value) > 0).mapToLong(h -> ((HumanBeing) h).getId()).toArray();
        int count = 0;
        for (var id : ids) {
            if (this.removeById(user, id)) count += 1;
        }
        return count;
    }

    /**
     * shows elements of collection
     */
    public List<Object> show() {
        return new ArrayList<>(collection);
    }

    /**
     * @return sum of impactSpeed
     */
    public Double sumOfImpactSpeed() {
        Float sum = (float) 0;
        final ArrayList<Float> arr = new ArrayList<>();
        collection.forEach((e) -> arr.add(((HumanBeing) e).getImpactSpeed()));
        return (double) arr.stream()
                .reduce(sum, Float::sum);
    }

    /**
     * updates element with given id
     *
     * @param id      element id
     * @param element new element
     */
    public Boolean update(String user, long id, Object element) {
        long c = collection.stream().filter(h -> ((HumanBeing) h).getId() == id).count();
        if (c == 0) return false;
        var res = psqlBase.update(user, id, (HumanBeing) element);
        if (res)
            collection.stream().filter(h -> ((HumanBeing) h).getId() == id).forEach(h -> ((HumanBeing) h).update((HumanBeing) element));
        return res;
    }

    /**
     * clears collection
     */
    public Boolean clear() {
        HumanBeing.ids.clear();
        var res = psqlBase.clear();
        if (res) collection.clear();
        return res;
    }

    /**
     * saves collection
     */

    public Boolean runServerCommand(String command) {
        if (command.equals("interrupt")) {
            System.out.println("Program interrupted");
            return false;
        }

        System.out.println("Unknown command.");
        return true;
    }

    public void close() {
        psqlBase.closeConnection();
    }
}
