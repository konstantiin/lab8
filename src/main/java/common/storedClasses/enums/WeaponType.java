package common.storedClasses.enums;

import java.io.Serializable;

/**
 * stored class
 */
public enum WeaponType implements Serializable {
    PISTOL,
    SHOTGUN,
    RIFLE,
    MACHINE_GUN;

    @SuppressWarnings("unused")
    public static WeaponType valueOf(int i) {
        return WeaponType.values()[i - 1];
    }
}