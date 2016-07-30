package org.pine

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD])
public @interface Describe {
    String value() default ""
}
