package com.honwhy.examples.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Repeatable(AtVersions.class)
public @interface AtVersion {
    String value();
}
