package com.savebite.patterns.structural.decorator;

import com.savebite.annotation.DesignPattern;
import com.savebite.teaching.PatternTracer;

import java.util.function.Supplier;

// Pattern: Decorator
// Category: Structural
// Problem it solves here: Wraps service invocations to measure latency and record execution metrics without modifying business logic in controllers.
// GoF participant role: ConcreteDecorator
// Used by: Service layer, Teaching tracers
@DesignPattern(name = "Decorator", role = "ConcreteDecorator", description = "Execution timer and logging decorator for services")
public class LoggingServiceDecorator {

    public static <T> T executeWithLogging(String operationName, Supplier<T> operation) {
        long start = System.nanoTime();
        try {
            T result = operation.get();
            long elapsedMicros = (System.nanoTime() - start) / 1000;
            PatternTracer.getInstance().record(
                "Decorator",
                "LoggingServiceDecorator",
                "executeWithLogging",
                "Decorated call to '" + operationName + "' completed in " + elapsedMicros + " µs"
            );
            return result;
        } catch (RuntimeException e) {
            long elapsedMicros = (System.nanoTime() - start) / 1000;
            PatternTracer.getInstance().record(
                "Decorator",
                "LoggingServiceDecorator",
                "executeWithLogging",
                "Decorated call to '" + operationName + "' failed after " + elapsedMicros + " µs: " + e.getMessage()
            );
            throw e;
        }
    }
}

