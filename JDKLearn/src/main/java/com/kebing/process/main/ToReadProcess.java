package com.kebing.process.main;

import com.kebing.process.ToRead;
import org.reflections.Reflections;

import java.util.Set;

public class ToReadProcess {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.kebing.process.java");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(ToRead.class);
        for (Class<?> toReadClass : annotated) {
            ToRead toRead = toReadClass.getAnnotation(ToRead.class);
            System.out.println(toRead.date() + " | " + toRead.message() + " | " +
                    toReadClass.getName().replace("com.kebing.process.",""));
        }
    }
}
