package client.reading.generators;

import common.storedClasses.Car;
import common.storedClasses.Coordinates;
import common.storedClasses.enums.Mood;
import common.storedClasses.enums.WeaponType;
import common.storedClasses.forms.HumanBeingForm;

import java.util.HashMap;

/**
 * class to generate HumanBeing
 */
public class HumanBeingFormGenerator implements Generator {
    /**
     * @param fields - object fields
     * @return new HumanBeing object
     */
    @Override
    public Object generate(HashMap<String, Object> fields) {
        return new HumanBeingForm((String) fields.get("name"), (Coordinates) fields.get("coordinates"), (Boolean) fields.get("realHero"),
                (Boolean) fields.get("hasToothpick"), (Float) fields.get("impactSpeed"), (WeaponType) fields.get("weaponType"),
                (Mood) fields.get("mood"), (Car) fields.get("car"));

    }
}
