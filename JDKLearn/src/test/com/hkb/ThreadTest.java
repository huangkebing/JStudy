package com.hkb;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 多线程测试类
 */
public class ThreadTest {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        new Thread(() -> {
            lock.lock();
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            System.out.println(Thread.currentThread().isInterrupted());
            lock.lock();
            System.out.println(Thread.currentThread().isInterrupted());
            lock.unlock();
        }).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            System.out.println(Thread.currentThread().isInterrupted());
            Thread.currentThread().interrupt();
            lock.lock();
            System.out.println(Thread.currentThread().isInterrupted());
            lock.unlock();
        }).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            System.out.println(Thread.currentThread().isInterrupted());
            lock.lock();
            System.out.println(Thread.currentThread().isInterrupted());
            lock.unlock();
        }).start();
    }

    @Test
    public void readWriteLock(){
        Class<Unsafe> unsafeClass = Unsafe.class;
        try {
            Constructor<Unsafe> constructor = unsafeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Unsafe unsafe = constructor.newInstance();
            TestAAA o = (TestAAA)unsafe.allocateInstance(TestAAA.class);
            o.setA(1);
            System.out.println(o.getA());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



}

class TestAAA{
    int a;
    public int getA(){
        return a;
    }
    public void setA(int a){
        this.a = a;
    }
}
