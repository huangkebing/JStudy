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
        /*pool.execute(()->{
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });*/
        System.out.println(pool.isTerminated());
    }
}
