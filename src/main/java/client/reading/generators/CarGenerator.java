package client.reading.generators;

import common.storedClasses.Car;

import java.util.HashMap;

/**
 * class to generate Car objects
 */
public class CarGenerator implements Generator {
    /**
     * @param fields - fields of new object
     * @return new Car object
     */
    @Override
    public Object generate(HashMap<String, Object> fields) {
        return new Car((String) fields.get("name"), (Boolean) fields.get("cool"));
    }
}
