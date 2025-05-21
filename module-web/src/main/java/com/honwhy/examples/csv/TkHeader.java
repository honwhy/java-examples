package com.honwhy.examples.csv;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TkHeader {
    String name();
    String pattern() default "";
}
