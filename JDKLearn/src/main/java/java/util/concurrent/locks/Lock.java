package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

/**
 * Lock接口
 */
public interface Lock {

    /**
     * 获得锁
     */
    void lock();

    /**
     * 除非当前线程是interrupted，否则获取锁
     */
    void lockInterruptibly() throws InterruptedException;

    /**
     * 仅当调用时它是空闲的时才获取锁
     */
    boolean tryLock();

    /**
     * 带时间限制的tryLock()
     */
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    /**
     * 释放锁
     */
    void unlock();

    /**
     * 获得一个Condition实例
     */
    Condition newCondition();
}
