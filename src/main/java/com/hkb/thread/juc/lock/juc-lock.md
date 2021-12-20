# 1. 概述
JUC包下的lock包，源代码阅读记录
# 2. AbstractOwnableSynchronizer
lock的最底层类 ，包含一个空构造器，如下的成员变量，以及其get和set方法
```java
public abstract class AbstractOwnableSynchronizer implements java.io.Serializable {
    private static final long serialVersionUID = 3737899427754241961L;
    protected AbstractOwnableSynchronizer() { }
    //当前锁拥有的线程
    private transient Thread exclusiveOwnerThread;
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
```

# 3. Lock接口
Lock接口定义了如下6个方法：
```java
public interface Lock {
    // 获得锁
    void lock();
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    // 释放锁
    void unlock();
    // 新建Condition对象
    Condition newCondition();
}
```

# 4. AbstractQueuedSynchronizer
继承了AbstractOwnableSynchronizer类，是lock的第二层类。
## 4.1 Node
```java
static final class Node {
        static final Node SHARED = new Node();
        static final Node EXCLUSIVE = null;
        
        static final int CANCELLED =  1;
        static final int SIGNAL    = -1;
        static final int CONDITION = -2;
        static final int PROPAGATE = -3;
        
        volatile int waitStatus;

        
        volatile Node prev;
        volatile Node next;
        
        volatile Thread thread;

        /**
         * Link to next node waiting on condition, or the special
         * value SHARED.  Because condition queues are accessed only
         * when holding in exclusive mode, we just need a simple
         * linked queue to hold nodes while they are waiting on
         * conditions. They are then transferred to the queue to
         * re-acquire. And because conditions can only be exclusive,
         * we save a field by using special value to indicate shared
         * mode.
         */
        Node nextWaiter;

        /**
         * Returns true if node is waiting in shared mode.
         */
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * Returns previous node, or throws NullPointerException if null.
         * Use when predecessor cannot be null.  The null check could
         * be elided, but is present to help the VM.
         *
         * @return the predecessor of this node
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {    // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) {     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }
```

# 5. ReentrantLock
ReentrantLock是可以被开发者使用的类，实现了Lock接口。该类的构造器：
```java
// 默认是非公平锁
public ReentrantLock() {
    sync = new NonfairSync();
}
// 使用 new ReentrantLock(true); 语句可以创建公平锁
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```
ReentrantLock的实现逻辑都在Sync、NonfairSync、FairSync这三个内部类中，其他的方法均是调用了内部类方法来实现的。
# 5.2 NonfairSync
NonfairSync继承了Sync，重写了两个方法：
```java
static final class NonfairSync extends Sync {
    private static final long serialVersionUID = 7316153563782823691L;
    // 非公平锁获得锁方法
    final void lock() {
        // 尝试CAS，如果成功，则直接获得锁，并将当前线程设为锁的拥有线程
        if (compareAndSetState(0, 1))
            setExclusiveOwnerThread(Thread.currentThread());
        else
            // 失败，调用AQS的acquire方法
            acquire(1);
    }
    protected final boolean tryAcquire(int acquires) {
        // 直接调用 Sync中的nonfairTryAcquire方法
        return nonfairTryAcquire(acquires);
    }
}
```
## 5.3 FairSync
FairSync同样继承了Sync，重写了两个方法：
```java
static final class FairSync extends Sync {
    private static final long serialVersionUID = -3000897897090466540L;
    // 公平锁获得lock直接调用AQS的acquire方法
    final void lock() {
        acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
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
```

# 6. Condition接口
```java
public interface Condition {
    void await() throws InterruptedException;
    void awaitUninterruptibly();
    long awaitNanos(long nanosTimeout) throws InterruptedException;
    boolean await(long time, TimeUnit unit) throws InterruptedException;
    boolean awaitUntil(Date deadline) throws InterruptedException;
    void signal();
    void signalAll();
}
```