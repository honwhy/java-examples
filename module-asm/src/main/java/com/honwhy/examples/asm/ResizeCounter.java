package com.honwhy.examples.asm;

import java.util.concurrent.atomic.AtomicLong;

public class ResizeCounter {
    private static final AtomicLong counter = new AtomicLong();

    public static void inc() {
        counter.incrementAndGet();
    }

    public static long get() {
        return counter.get();
    }
}
