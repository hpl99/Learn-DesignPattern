package com.savebite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes participating in GoF Design Patterns.
 * Used by PatternRegistry and runtime scanner to keep code and documentation in sync.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(DesignPatterns.class)
public @interface DesignPattern {
    /**
     * The canonical name of the design pattern (e.g., "Singleton", "Observer").
     */
    String name();

    /**
     * The role of this participant according to the GoF specification
     * (e.g., "ConcreteSubject", "Memento", "Originator", "AbstractFactory").
     */
    String role() default "";

    /**
     * Brief explanation of what problem this participant solves in SaveBite.
     */
    String description() default "";
}
