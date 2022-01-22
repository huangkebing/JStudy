package com.hkb;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FunctionTest {
    @Test
    public void consumerTest(){
        // consumer.andThen()
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(3);
        integers.add(4);
        integers.add(2);
        integers.forEach(((Consumer<Integer>) System.out::println).andThen(x -> {
            int a = x + 4;
            System.out.println(a);
        }));
    }

    @Test
    public void biConsumerTest(){
        // BiConsumer.andThen()
        HashMap<String, String> map = new HashMap<>();
        map.put("1","aaa");
        map.put("2","bbb");
        map.put("3","ccc");
        map.forEach(((BiConsumer<String,String>)(k, v) -> System.out.println(k))
                .andThen((k,v) -> System.out.println(v)));
    }
}
