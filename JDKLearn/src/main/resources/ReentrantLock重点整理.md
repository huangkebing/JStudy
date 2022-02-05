# ReentrantLock重点整理
## 一、ReentrantLock是什么
ReentrantLock是JUC包下的一个类。实现了一个同步锁，基于AQS实现，是一个独占锁。
## 二、和synchronized的区别
### 2.1 底层实现
1. 底层实现上来说，synchronized 是JVM层面的锁，是Java关键字，通过monitor对象来完成（monitorenter与monitorexit），对象只有在同步块或同步方法中才能调用wait/notify方法，ReentrantLock 是从jdk1.5以来（java.util.concurrent.locks.Lock）提供的API层面的锁。
2. synchronized 的实现涉及到锁的升级，具体为无锁、偏向锁、自旋锁、向OS申请重量级锁，ReentrantLock实现则是通过利用CAS自旋机制保证线程操作的原子性和volatile保证数据可见性以实现锁的功能。
### 2.2 操作区别
synchronized 不需要用户去手动释放锁，synchronized 代码执行完后系统会自动让线程释放对锁的占用； ReentrantLock则需要用户去手动释放锁，如果没有手动释放锁，就可能导致死锁现象。一般通过lock()和unlock()方法配合try/finally语句块来完成，使用释放更加灵活。
### 2.3 是否可中断
synchronized是不可中断类型的锁，除非加锁的代码中出现异常或正常执行完成； ReentrantLock则可以中断，可通过trylock(long timeout,TimeUnit unit)设置超时方法或者将lockInterruptibly()放到代码块中，调用interrupt方法进行中断。
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
    // 可中断方法
    private void doAcquireInterruptibly(int arg) throws InterruptedException {
        // 调用addWaiter生成节点
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            // 逻辑和acquireQueued一致，唯一区别是，如果线程有中断标记则抛出异常
            for (; ; ) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed) cancelAcquire(node);
        }
    }
}
```
### 2.4 公平锁
synchronized为非公平锁 ReentrantLock则即可以选公平锁也可以选非公平锁，通过构造方法new ReentrantLock时传入boolean值进行选择，为空默认false非公平锁，true为公平锁。
```java
public class ReentrantLock implements Lock, java.io.Serializable {
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
}
```
### 2.5 Condition
synchronized不能绑定； ReentrantLock通过绑定Condition结合await()/singal()方法实现线程的精确唤醒，而不是像synchronized通过Object类的wait()/notify()/notifyAll()方法要么随机唤醒一个线程要么唤醒全部线程。
### 2.6 锁的对象
synchronzied锁的是对象，锁是保存在对象头里面的，根据对象头数据来标识是否有线程获得锁/争抢锁；ReentrantLock根据进入的线程和int类型的state标识锁的获得/争抢。
### 2.7 ReentrantLock独有功能
即公平锁、Condition精准唤醒线程、可中断、可以查看锁的一些信息。一般只有要用到以上的特性时，使用ReentrantLock。
## 三、ReentrantLock上锁解锁过程
分析总结一下，各种情况下的获得锁，以最基本的lock方法为例
### 3.1 lock方法
ReentrantLock.lock()方法就是调用了内部类的lock，如下：
```java
static final class NonfairSync extends Sync {
    final void lock() {
        // 非公平锁模式，首先CAS state字段，尝试获取锁，成功则代表获取到锁了
        if (compareAndSetState(0, 1)) setExclusiveOwnerThread(Thread.currentThread());
        // 失败，走AQS的逻辑
        else acquire(1);
    }
}

static final class FairSync extends Sync {
    // 公平锁模式，直接走AQS逻辑
    final void lock() {
        acquire(1);
    }
}
```
可以看到两者都用到了acquire()，这个是在AQS中定义的方法，而两者的区别在于，非公平锁会先尝试获取一次锁！
### 3.2 tryAcquire方法
在看acquire方法前，先看一下tryAcquire方法，该方法是获得锁的核心！
```java
static final class NonfairSync extends Sync {
    protected final boolean tryAcquire(int acquires) {
        // 使用了父类的nonfairTryAcquire方法
        return nonfairTryAcquire(acquires);
    }
}
abstract static class Sync extends AbstractQueuedSynchronizer {
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) { // 如果当前锁是空闲的，直接尝试CAS获取锁
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) { //是当前锁的拥有线程
            int nextc = c + acquires;
            if (nextc < 0) throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
}
static final class FairSync extends Sync {
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {// 当前锁空闲，且队列中没有等待线程或者当前线程为第一个，且CAS修改状态成功，则获得锁
            if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) { // 如果是拥有者，修改state字段为c+acquires
            int nextc = c + acquires;
            if (nextc < 0) throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        // 否则，获取锁失败
        return false;
    }
}
```
可以看到公平版本多了一个hasQueuedPredecessors()方法，保证了线程能够按照先后顺序获取锁。
### 3.3 acquire方法
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
    public final void acquire(int arg) {
        // 1.先tryAcquire尝试获得锁
        // 2.若没有获得锁，执行addWaiter方法，创建一个Node，插入阻塞队列尾部
        // 3.acquireQueued()返回false，表示线程没有被阻塞就获得了锁
        // 4.acquireQueued()返回true，执行selfInterrupt方法。为线程重新打上标记
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
}
```
acquire方法会先执行一次tryAcquire方法，如果成功获得到锁，则直接执行完了。如果失败，有如下逻辑：
1. 执行addWaiter方法，addWaiter()方法的作用是封装一个独占式的Node节点，并插入到队列的尾部！
2. 然后执行acquireQueued()方法
3. 若acquireQueued()的结果为false，表示线程没有中断标记
4. 如果为true，表示线程有过中断标记，需要执行selfInterrupt()重新打上标记
### 3.4 acquireQueued方法
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                // 如果前置结点为head，且尝试获取锁成功，将当前节点置为Head节点
                // 不论是公平锁还是非公平锁，对于队列中的线程只能按照先后顺序获取锁
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                // shouldParkAfterFailedAcquire返回true，执行parkAndCheckInterrupt方法
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    // 标记线程是否被打断过，park后唤醒会重置打断标记
                    interrupted = true;
            }
        } finally {
            // 如果没有获取到锁，报错了，取消获取
            if (failed) cancelAcquire(node);
        }
    }
}
```
执行到这一步，线程已经以Node的形式保存在双端队列中了，会按照先后顺序来获得锁。只有head节点的后置结点，才有资格tryAcquire。如果没有获得到锁，会执行shouldParkAfterFailedAcquire()判断是否需要阻塞该线程，执行parkAndCheckInterrupt()阻塞线程
### 3.5 线程阻塞
线程的阻塞判断逻辑在shouldParkAfterFailedAcquire方法中，而由parkAndCheckInterrupt方法实现阻塞
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
    // 判断是否需要阻塞
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus; // 取前置结点的等待状态
        if (ws == Node.SIGNAL) // 如果为SIGNAL态，表示前置结点尚在执行或在等待锁，本节点可以阻塞，返回true
            return true;
        if (ws > 0) { // 如果ws > 0,表示前置结点的等待状态为CANCELLED，移除这些状态的节点
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            // 其他情况即等待状态=0，则将前置结点的等待状态置为SIGNAL，并返回false，即再去获取一次锁，若依旧失败就会被阻塞
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    // 阻塞线程
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
}
```
### 3.6 锁的释放与线程唤醒
当释放锁时，会执行unlock方法，其中调用了release方法。
```java
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            //若释放成功，即state==0时，执行unparkSuccessor方法唤醒阻塞的线程。但此时state已经为0，如果是非公平锁，则可能被其他线程获得锁
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
    
    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0) compareAndSetWaitStatus(node, ws, 0); //将等待状态置为0

        // 唤醒下一个不为取消态的节点
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0) s = t;
        }
        if (s != null)
            LockSupport.unpark(s.thread);
    }
}
abstract static class Sync extends AbstractQueuedSynchronizer {
    protected final boolean tryRelease(int releases) {
        int c = getState() - releases;
        // 如果不是拥有线程，抛出异常
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        // 若state-releases==0，则返回true，否则返回false
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }
}
```