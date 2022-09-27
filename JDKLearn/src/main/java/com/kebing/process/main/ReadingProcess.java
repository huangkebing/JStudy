package com.kebing.process.main;

import com.kebing.process.Reading;
import org.reflections.Reflections;

import java.util.Set;

public class ReadingProcess {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.kebing.process.java");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Reading.class);
        for (Class<?> readingClass : annotated) {
            Reading reading = readingClass.getAnnotation(Reading.class);
            System.out.println(reading.date() + " | " + reading.message() + " | " +
                    readingClass.getName().replace("com.kebing.process.",""));
        }
    }
}
