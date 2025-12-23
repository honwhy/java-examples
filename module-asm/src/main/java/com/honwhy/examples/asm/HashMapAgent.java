package com.honwhy.examples.asm;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class HashMapAgent {
    public static void premain(String args, Instrumentation inst) {
        System.out.println("premain");
        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .type(named("java.util.HashMap"))
                .transform((builder, td, cl, module, pd) -> {
                    System.out.println("transform");
                            return builder.visit(
                                    Advice.to(ResizeAdvice.class)
                                            .on(named("resize").and(takesArguments(0)))
                            );
                        }

                )
                .installOn(inst);

    }
}
