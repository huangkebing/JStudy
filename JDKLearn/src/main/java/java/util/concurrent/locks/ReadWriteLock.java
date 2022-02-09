package java.util.concurrent.locks;

/**
 * 读写锁
 */
public interface ReadWriteLock {
    /**
     * 获得读锁
     */
    Lock readLock();

    /**
     * 获得写锁
     */
    Lock writeLock();
}
