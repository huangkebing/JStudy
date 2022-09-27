package com.kebing.process;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 表示未读的源码
 */
@Target(value={TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToRead {
    String date() default "";

    String message() default "";
}
