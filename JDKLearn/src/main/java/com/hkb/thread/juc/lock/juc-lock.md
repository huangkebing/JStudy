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
// AQS Node节点
static final class Node {
    // 模式，共享锁、独占锁
    static final Node SHARED = new Node();
    static final Node EXCLUSIVE = null;
    Node nextWaiter;
    
    // 等待状态
    static final int CANCELLED =  1;
    static final int SIGNAL    = -1;
    static final int CONDITION = -2;
    static final int PROPAGATE = -3;
    volatile int waitStatus;
    
    // 前一个结点 和 后一个结点
    volatile Node prev;
    volatile Node next;
    
    //结点内存放的线程
    volatile Thread thread;
    
    final boolean isShared() {
        return nextWaiter == SHARED;
    }
    
    // 获取前一个结点
    final Node predecessor() throws NullPointerException {
        Node p = prev;
        if (p == null)
            throw new NullPointerException();
        else
            return p;
    }

    Node() {}

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