package com.hkb;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListTest {

    @Test
    public void toArrayTest(){
        /*
            [1, 2]
            class [Ljava.lang.String;
            [1, 2]
            class [Ljava.lang.Object;
         */
        String[] array = {"1", "2"};
        /*
            Arrays中的ArrayList，实际存的是泛型E类数组，toArray方法返回的也是
            因此当执行arrayArray[0] = new Object()语句时，出现向下转型而报错
            ArrayList(E[] array) {
                a = Objects.requireNonNull(array);
            }
            @Override
            public Object[] toArray() {
                return a.clone();
            }
        */
        List<String> arrayList = Arrays.asList(array);
        Object[] arrayArray = arrayList.toArray();
        // java.lang.ArrayStoreException: java.lang.Object
        // arrayArray[0] = new Object();
        System.out.println(Arrays.toString(arrayArray));
        System.out.println(arrayArray.getClass());

        List<String> newList = new ArrayList<>();
        newList.add("1");
        newList.add("2");
        Object[] newArray = newList.toArray();
        System.out.println(Arrays.toString(newArray));
        System.out.println(newArray.getClass());
    }
}
