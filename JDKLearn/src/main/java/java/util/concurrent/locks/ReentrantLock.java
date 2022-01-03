package java.util.concurrent.locks;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;

    // 锁实例
    private final Sync sync;

    /**
     * ReentrantLock类的核心
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        abstract void lock();

        // 非公平方式获取
        final boolean nonfairTryAcquire(int acquires) {
            // 当前线程
            final Thread current = Thread.currentThread();
            // 获取状态
            int c = getState();
            if (c == 0) { // 表示没有线程正在竞争该锁
                if (compareAndSetState(0, acquires)) { // 比较并设置状态成功，状态0表示锁没有被占用
                    // 设置当前线程独占
                    setExclusiveOwnerThread(current);
                    return true; // 成功
                }
            }
            else if (current == getExclusiveOwnerThread()) { // 当前线程拥有该锁
                int nextc = c + acquires; // 增加重入次数
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                // 设置状态
                setState(nextc);
                // 成功
                return true;
            }
            // 失败
            return false;
        }

        // 尝试获得锁
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            // 如果不是占有线程，抛出异常
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            // 如果state为0，释放锁
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        // 检查当前线程是否是锁的拥有线程
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        // 获得condition对象
        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        // 获得锁的拥有线程，如果state==0，返回null
        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        // 如果是锁拥有线程返回state，否则返回0
        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        /**
         * 根据AQS的state判断是否已经触发锁
         * state!=0，返回true 表示已上锁
         * state==0，返回false 表示未上锁
         */
        final boolean isLocked() {
            return getState() != 0;
        }

        /**
         * 反序列化逻辑
         */
        private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    /**
     * 非公平锁模式
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * 调用AQS的CAS方法，如果成功直接获得锁。即不保证先后顺序的获取锁
         * 如果失败调用AQS的acquire方法
         */
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        /**
         * AQS的acquire方法会首先调用此方法
         */
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * 公平锁模式
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        /**
         * 调用AQS的acquire方法
         */
        final void lock() {
            acquire(1);
        }

        /**
         * AQS的acquire方法会首先调用此方法
         */
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                // 当前线程前面没有排队线程，且CAS成功，则获得锁
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    /**
     * 空构造器，ReentrantLock默认是非公平锁
     */
    public ReentrantLock() {
        sync = new NonfairSync();
    }

    /**
     * 传入true，则创建公平锁。ReentrantLock相比synchronized的优势之一：可以创建公平锁
     */
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    // ReentrantLock 的核心是Sync类，方法均是调用Sync的方法
    public void lock() {
        sync.lock();
    }

    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    public void unlock() {
        sync.release(1);
    }

    public Condition newCondition() {
        return sync.newCondition();
    }

    public int getHoldCount() {
        return sync.getHoldCount();
    }

    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }

    public boolean isLocked() {
        return sync.isLocked();
    }

    // 判断是否为公平锁
    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    protected Thread getOwner() {
        return sync.getOwner();
    }

    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    public boolean hasWaiters(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    public int getWaitQueueLength(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    protected Collection<Thread> getWaitingThreads(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    // toString
    public String toString() {
        Thread o = sync.getOwner();
        return super.toString() + ((o == null) ?
                                   "[Unlocked]" :
                                   "[Locked by thread " + o.getName() + "]");
    }
}
