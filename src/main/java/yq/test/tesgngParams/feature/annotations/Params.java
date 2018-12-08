package yq.test.tesgngParams.feature.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Params {

    /**
     * Parameter values defined as a String array. Each element in the array is
     * a full parameter set, comma-separated    .
     * The values must match the method parameters in order and type.
     * Whitespace characters are trimmed (use source class or method if You need to provide such parameters)
     *
     * Example: <code>@Parameters({
     *                    "1, joe, 26.4, true",
     *                    "2, angie, 37.2, false"})</code>
     */
    String[] value();
}
