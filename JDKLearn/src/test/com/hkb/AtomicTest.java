package com.hkb;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicTest {
    @Test
    public void atomicArrayTest(){
        int[] array = new int[2];
        array[0] = 1;
        array[1] = 2;
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(array);
        System.out.println(atomicIntegerArray.get(0));
    }
}
