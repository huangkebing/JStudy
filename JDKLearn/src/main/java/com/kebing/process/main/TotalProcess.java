package com.kebing.process.main;

import com.kebing.process.FinishedReading;
import com.kebing.process.Reading;
import com.kebing.process.ToRead;
import org.reflections.Reflections;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TotalProcess {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("com.kebing.process.java");
        BigDecimal toRead = BigDecimal.valueOf(reflections.getTypesAnnotatedWith(ToRead.class).size());
        BigDecimal reading = BigDecimal.valueOf(reflections.getTypesAnnotatedWith(Reading.class).size());
        BigDecimal finish = BigDecimal.valueOf(reflections.getTypesAnnotatedWith(FinishedReading.class).size());

        BigDecimal total = toRead.add(reading).add(finish);
        BigDecimal toReadRate = toRead.divide(total, 2, RoundingMode.HALF_UP);
        BigDecimal readingRate = reading.divide(total, 2, RoundingMode.HALF_UP);
        BigDecimal finishRate = finish.divide(total, 2, RoundingMode.HALF_UP);
        System.out.println("进度统计：");
        System.out.println("总数：" + total);
        System.out.println("待读数：" + toRead + " | 待读率：" + toReadRate);
        System.out.println("读中数：" + reading + " | 读中率：" + readingRate);
        System.out.println("已读数：" + finish + " | 已读率：" + finishRate);
    }
}
