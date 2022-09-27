package com.kebing.process.main;

import com.kebing.process.FinishedReading;
import org.reflections.Reflections;

import java.util.Set;

public class FinishedReadingProcess {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.kebing.process.java");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(FinishedReading.class);
        for (Class<?> finishedClass : annotated) {
            FinishedReading finishedReading = finishedClass.getAnnotation(FinishedReading.class);
            System.out.println(finishedReading.date() + " | " +
                    finishedClass.getName().replace("com.kebing.process.",""));
        }
    }
}
