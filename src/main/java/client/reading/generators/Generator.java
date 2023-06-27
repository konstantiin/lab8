package client.reading.generators;

import java.util.HashMap;

/**
 * generator interface
 */
public interface Generator {
    Object generate(HashMap<String, Object> fields);
}
