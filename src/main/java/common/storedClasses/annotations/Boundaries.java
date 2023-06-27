package common.storedClasses.annotations;

import java.lang.annotation.*;

/**
 * stores field boundaries
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Boundaries {
    String lowerBound();

    String upperBound();
}

