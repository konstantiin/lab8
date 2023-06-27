package common.storedClasses.annotations;

import java.lang.annotation.*;

/**
 * indicates that field is not allowed to be null
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
}
