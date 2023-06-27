package client.reading.generators;

import common.storedClasses.Coordinates;

import java.util.HashMap;

/**
 * class to generate Coordinates object
 */
public class CoordinatesGenerator implements Generator {
    /**
     * @param fields - fields of new object
     * @return new Coordinates object
     */
    @Override
    public Object generate(HashMap<String, Object> fields) {
        return new Coordinates((float) fields.get("x"), (long) fields.get("y"));
    }
}
