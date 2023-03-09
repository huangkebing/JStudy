package com.hkb;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapTest {
    @Test
    public void tableSizeForTest(){
        HashMap<String, Object> map = new HashMap<>(17);
    }

    @Test
    public void nodeTest(){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("8",1);map.put("4",2);map.put("7",3);map.put("5",4);
        System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toList()));
        System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList()));
        System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByKey((k1, k2) -> -k1.compareTo(k2))).collect(Collectors.toList()));
        System.out.println(map.entrySet().stream().sorted(Map.Entry.comparingByValue((v1, v2) -> -v1.compareTo(v2))).collect(Collectors.toList()));
    }
}
