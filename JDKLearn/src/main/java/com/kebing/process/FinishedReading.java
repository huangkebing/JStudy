package com.kebing.process;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 表示已读完的源码
 */
@Target(value={TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FinishedReading {
    String date() default "";
}
