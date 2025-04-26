package com.honwhy.examples.version;

import com.honwhy.examples.common.annotation.AtVersion;

@AtVersion("001")
@AtVersion("002")
@AtVersion("003")
public interface AtVersionGenerator {

    default void printVersions() {
        System.out.println(AtVersion001.class.getSimpleName());
        System.out.println(AtVersion002.class.getSimpleName());
        System.out.println(AtVersion003.class.getSimpleName());
    }
}
