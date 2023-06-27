package server.dataBase;

import common.storedClasses.Car;
import common.storedClasses.Coordinates;
import common.storedClasses.HumanBeing;
import common.storedClasses.enums.Mood;
import common.storedClasses.enums.WeaponType;
import common.storedClasses.forms.HumanBeingForm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class DbConnection {
    private final Connection connection;

    public DbConnection() {
        try(var reader = new BufferedReader(new FileReader("/home/studs/s367363/.pgpass"));) {

            var line = reader.readLine();
            var arr = line.split(":");
            var user = arr[3];
            var pas = arr[4];

            connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", user, pas);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
        try {
            this.createTable();
        } catch (Exception ignored) {
        }
    }

    synchronized private void createTable() {
        try {
            var create = connection.prepareStatement("CREATE TABLE IF NOT EXISTS humans(" +
                    "id bigserial primary key," +
                    "creation_Date text not null, " +
                    "name text not null," +
                    "coordinates_x real not null," +
                    "coordinates_y bigint not null," +
                    "real_Hero boolean not null," +
                    "has_Toothpick boolean," +
                    "impact_Speed real," +
                    "weapon_Type text not null," +
                    "mood text," +
                    "car_name text," +
                    "car_cool boolean," +
                    "user_name text not null);");
            create.execute();
            create.close();
            create = connection.prepareStatement("CREATE TABLE if NOT EXISTS passwords(" +
                    "id serial primary key," +
                    "user_name text not null unique," +
                    "hashed_pass text not null); ");
            create.execute();
            create.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    synchronized public boolean addElements(String user, HumanBeing... items) {
        for (HumanBeing item : items) {
            try {

                var insert = connection.prepareStatement("INSERT INTO humans(creation_date, name, coordinates_x, coordinates_y, real_hero," +
                        " has_toothpick, impact_speed, weapon_type, mood, car_name, car_cool, user_name )" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?, ?) RETURNING id;");
                setParams(item, insert);
                insert.setString(12, user);
                var id = insert.executeQuery();
                long newId = -1;
                if (id.next()) newId = id.getLong("id");
                else System.out.println(id);
                item.updId(newId);
                id.close();
                insert.close();
            } catch (SQLException e) {
                return false; // cringe remove added elements
            }
        }
        return true;
    }

    synchronized public List<HumanBeing> readDb() {
        try {
            var ans = new ArrayList<HumanBeing>();
            var req = connection.prepareStatement("SELECT * from humans;");
            var result = req.executeQuery();
            while (result.next()) {
                var id = result.getLong("id");
                var creationDate = LocalDateTime.parse(result.getString("creation_date"));
                var name = result.getString("name");
                var x = result.getFloat("coordinates_x");
                var y = result.getLong("coordinates_y");
                var realHero = result.getBoolean("real_hero");
                Boolean hasToothpick = result.getBoolean("has_toothpick");
                if (result.wasNull()) hasToothpick = null;
                Float impactSpeed = result.getFloat("impact_speed");
                if (result.wasNull()) impactSpeed = null;
                var weaponType = WeaponType.valueOf(result.getString("weapon_type"));
                var mood = Mood.valueOf(result.getString("mood"));
                var carName = result.getString("car_name");
                if (result.wasNull()) carName = null;
                Boolean carCool = result.getBoolean("car_cool");
                if (result.wasNull()) carCool = null;

                Car car = null;
                if (carName != null && carCool != null) car = new Car(carName, carCool);
                Coordinates coordinates = new Coordinates(x, y);

                HumanBeingForm form = new HumanBeingForm(name, coordinates, realHero, hasToothpick, impactSpeed, weaponType, mood, car);
                HumanBeing h = new HumanBeing(form);
                h.updId(id);
                h.updateDate(creationDate);
                ans.add(h);
            }
            result.close();
            req.close();
            return ans;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized public boolean removeElements(String user, long id) {
        System.out.println(user);
        try {
            var delete = connection.prepareStatement("DELETE FROM humans where id = " + id + " and user_name = ?" +
                    "RETURNING id;");
            delete.setString(1, user);
            var result = delete.executeQuery();
            boolean res = false;
            if (result.next()) {
                res = result.getLong(1) == id;
            }
            delete.close();
            return res;
        } catch (SQLException e) {
            return false;
        }
    }

    synchronized public boolean update(String user, long old, HumanBeing n) {
        try {
            var update = connection.prepareStatement(
                    "UPDATE humans SET creation_date = ?," +
                            "name = ?," +
                            "coordinates_x = ?," +
                            "coordinates_y = ?," +
                            "real_hero = ?," +
                            "has_toothpick = ?," +
                            "impact_speed = ?," +
                            "weapon_type = ?," +
                            "mood = ?," +
                            "car_name = ?," +
                            "car_cool = ? WHERE id = " + old + " and " +
                            "user_name = ? returning id;");

            setParams(n, update);
            update.setString(12, user);
            var result = update.executeQuery();
            boolean res = result.next();
            update.close();
            return res;
        } catch (SQLException e) {
            return true;
        }
    }

    synchronized public String hash(String str) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-384");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        var n = new BigInteger(1, md.digest(str.getBytes()));
        return n.toString();

    }

    synchronized public boolean register(String name, String pas) {
        var password = hash(pas);
        try {
            var add = connection.prepareStatement("INSERT INTO passwords(user_name, hashed_pass)" +
                    "values (?,?);");
            add.setString(1, name);
            add.setString(2, password);
            add.execute();
            add.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    synchronized public boolean signUp(String name, String pas) {
        var password = hash(pas);
        boolean ans = false;
        try {
            var check = connection.prepareStatement("SELECT hashed_pass from passwords " +
                    "where user_name = ?");
            check.setString(1, name);
            var result = check.executeQuery();
            if (result.next()) {
                String correct = result.getString("hashed_pass");
                ans = Objects.equals(correct, password);
            }
            check.close();
            return ans;
        } catch (SQLException e) {
            return false;
        }

    }

    synchronized private void setParams(HumanBeing n, PreparedStatement req) throws SQLException {
        req.setString(1, n.getStrTime());
        req.setString(2, n.getName());
        req.setFloat(3, n.getCoordinates().getX());
        req.setLong(4, n.getCoordinates().getY());
        req.setBoolean(5, n.getRealHero());
        req.setObject(6, n.getHasToothpick());
        req.setObject(7, n.getImpactSpeed());
        req.setString(8, n.getWeaponType().toString());
        req.setString(9, n.getMood().toString());
        req.setString(10, n.getCarName());
        req.setObject(11, n.getCarCool());
    }

    synchronized public boolean clear() {
        try {
            var drop = connection.prepareStatement("DROP TABLE humans;");
            drop.execute();
            createTable();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    synchronized public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
