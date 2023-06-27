package common.storedClasses;

import client.reading.generators.CarGenerator;
import client.reading.generators.Generator;
import common.storedClasses.annotations.NotNull;

import java.io.Serializable;

/**
 * stored class
 */
public class Car implements Checkable, Serializable {
    private final @NotNull String name;
    private final @NotNull Boolean cool;

    /**
     * @param name name of car
     * @param cool true if car is cool
     */
    public Car(String name, boolean cool) {
        this.name = name;
        this.cool = cool;
    }

    public Boolean getCool() {
        return cool;
    }

    public String getName() {
        return name;
    }

    /**
     * returns Car Generator
     *
     * @return Generator
     */
    public static Generator getGenerator() {
        return new CarGenerator();
    }

    @Override
    public String toString() {
        if (cool) {
            return "cool " + name;
        }
        return "not cool" + name;
    }
}