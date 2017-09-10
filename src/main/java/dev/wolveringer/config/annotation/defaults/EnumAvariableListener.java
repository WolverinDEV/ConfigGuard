package dev.wolveringer.config.annotation.defaults;

import dev.wolveringer.config.annotation.CommentGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wolverindev on 10.09.17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CommentGenerator(generatorClass = DefaultImplementations.class, methode = "buildEnumAvailable")
public @interface EnumAvariableListener { }
