package com.hkb;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

        // BiConsumer.andThen()
        HashMap<String, String> map = new HashMap<>();
        map.put("1","aaa");
        map.put("2","bbb");
        map.put("3","ccc");
        map.forEach(((BiConsumer<String,String>)(k, v) -> System.out.println(k))
                .andThen((k,v) -> System.out.println(v)));
    }

    @Test
    public void predicateTest(){
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(5);
        integers.add(6);
        integers.add(7);
        integers.add(8);
        integers.add(9);
        integers.add(10);
        integers.add(11);
        integers.add(12);

        List<Integer> collect1 = integers.stream()
                // Predicate.and() 配合filter实现 与操作，过滤出3和4的公倍数
                .filter(((Predicate<Integer>) i -> i % 4 == 0).and(i-> i % 3 == 0))
                .collect(Collectors.toList());
        collect1.forEach(System.out::println);

        List<Integer> collect2 = integers.stream()
                .filter(((Predicate<Integer>) i -> i % 4 == 0).or(i-> i % 3 == 0))
                .collect(Collectors.toList());
        collect2.forEach(System.out::println);
    }
}
