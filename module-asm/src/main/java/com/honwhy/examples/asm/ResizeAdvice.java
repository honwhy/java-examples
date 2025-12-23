package com.honwhy.examples.asm;

import net.bytebuddy.asm.Advice;

public class ResizeAdvice {

    @Advice.OnMethodEnter
    static void enter() {
        System.out.println("Resize called");
        ResizeCounter.inc();
    }
}