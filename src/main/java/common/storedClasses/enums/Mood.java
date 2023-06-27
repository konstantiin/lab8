package common.storedClasses.enums;

import java.io.Serializable;

/**
 * stored class
 */
public enum Mood implements Serializable {
    SORROW,
    LONGING,
    GLOOM,
    CALM,
    FRENZY;

    @SuppressWarnings("unused")
    public static Mood valueOf(int i) {
        return Mood.values()[i - 1];
    }
}