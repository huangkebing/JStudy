package com.hkb;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorTest {
    @Test
    public void executeTest(){
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 2, 5,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(500));
        pool.execute(()->{
            try {
                TimeUnit.MINUTES.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        pool.execute(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
        });
    }
}
