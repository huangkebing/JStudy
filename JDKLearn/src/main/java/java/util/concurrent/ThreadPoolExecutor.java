package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An {@link ExecutorService} that executes each submitted task using
 * one of possibly several pooled threads, normally configured
 * using {@link Executors} factory methods.
 *
 * <p>Thread pools address two different problems: they usually
 * provide improved performance when executing large numbers of
 * asynchronous tasks, due to reduced per-task invocation overhead,
 * and they provide a means of bounding and managing the resources,
 * including threads, consumed when executing a collection of tasks.
 * Each {@code ThreadPoolExecutor} also maintains some basic
 * statistics, such as the number of completed tasks.
 *
 * <p>To be useful across a wide range of contexts, this class
 * provides many adjustable parameters and extensibility
 * hooks. However, programmers are urged to use the more convenient
 * {@link Executors} factory methods {@link
 * Executors#newCachedThreadPool} (unbounded thread pool, with
 * automatic thread reclamation), {@link Executors#newFixedThreadPool}
 * (fixed size thread pool) and {@link
 * Executors#newSingleThreadExecutor} (single background thread), that
 * preconfigure settings for the most common usage
 * scenarios. Otherwise, use the following guide when manually
 * configuring and tuning this class:
 *
 * <dl>
 *
 * <dt>Core and maximum pool sizes</dt>
 *
 * <dd>A {@code ThreadPoolExecutor} will automatically adjust the
 * pool size (see {@link #getPoolSize})
 * according to the bounds set by
 * corePoolSize (see {@link #getCorePoolSize}) and
 * maximumPoolSize (see {@link #getMaximumPoolSize}).
 *
 * When a new task is submitted in method {@link #execute(Runnable)},
 * and fewer than corePoolSize threads are running, a new thread is
 * created to handle the request, even if other worker threads are
 * idle.  If there are more than corePoolSize but less than
 * maximumPoolSize threads running, a new thread will be created only
 * if the queue is full.  By setting corePoolSize and maximumPoolSize
 * the same, you create a fixed-size thread pool. By setting
 * maximumPoolSize to an essentially unbounded value such as {@code
 * Integer.MAX_VALUE}, you allow the pool to accommodate an arbitrary
 * number of concurrent tasks. Most typically, core and maximum pool
 * sizes are set only upon construction, but they may also be changed
 * dynamically using {@link #setCorePoolSize} and {@link
 * #setMaximumPoolSize}. </dd>
 *
 * <dt>On-demand construction</dt>
 *
 * <dd>By default, even core threads are initially created and
 * started only when new tasks arrive, but this can be overridden
 * dynamically using method {@link #prestartCoreThread} or {@link
 * #prestartAllCoreThreads}.  You probably want to prestart threads if
 * you construct the pool with a non-empty queue. </dd>
 *
 * <dt>Creating new threads</dt>
 *
 * <dd>New threads are created using a {@link ThreadFactory}.  If not
 * otherwise specified, a {@link Executors#defaultThreadFactory} is
 * used, that creates threads to all be in the same {@link
 * ThreadGroup} and with the same {@code NORM_PRIORITY} priority and
 * non-daemon status. By supplying a different ThreadFactory, you can
 * alter the thread's name, thread group, priority, daemon status,
 * etc. If a {@code ThreadFactory} fails to create a thread when asked
 * by returning null from {@code newThread}, the executor will
 * continue, but might not be able to execute any tasks. Threads
 * should possess the "modifyThread" {@code RuntimePermission}. If
 * worker threads or other threads using the pool do not possess this
 * permission, service may be degraded: configuration changes may not
 * take effect in a timely manner, and a shutdown pool may remain in a
 * state in which termination is possible but not completed.</dd>
 *
 * <dt>Keep-alive times</dt>
 *
 * <dd>If the pool currently has more than corePoolSize threads,
 * excess threads will be terminated if they have been idle for more
 * than the keepAliveTime (see {@link #getKeepAliveTime(TimeUnit)}).
 * This provides a means of reducing resource consumption when the
 * pool is not being actively used. If the pool becomes more active
 * later, new threads will be constructed. This parameter can also be
 * changed dynamically using method {@link #setKeepAliveTime(long,
 * TimeUnit)}.  Using a value of {@code Long.MAX_VALUE} {@link
 * TimeUnit#NANOSECONDS} effectively disables idle threads from ever
 * terminating prior to shut down. By default, the keep-alive policy
 * applies only when there are more than corePoolSize threads. But
 * method {@link #allowCoreThreadTimeOut(boolean)} can be used to
 * apply this time-out policy to core threads as well, so long as the
 * keepAliveTime value is non-zero. </dd>
 *
 * <dt>Queuing</dt>
 *
 * <dd>Any {@link BlockingQueue} may be used to transfer and hold
 * submitted tasks.  The use of this queue interacts with pool sizing:
 *
 * <ul>
 *
 * <li> If fewer than corePoolSize threads are running, the Executor
 * always prefers adding a new thread
 * rather than queuing.</li>
 *
 * <li> If corePoolSize or more threads are running, the Executor
 * always prefers queuing a request rather than adding a new
 * thread.</li>
 *
 * <li> If a request cannot be queued, a new thread is created unless
 * this would exceed maximumPoolSize, in which case, the task will be
 * rejected.</li>
 *
 * </ul>
 *
 * There are three general strategies for queuing:
 * <ol>
 *
 * <li> <em> Direct handoffs.</em> A good default choice for a work
 * queue is a {@link SynchronousQueue} that hands off tasks to threads
 * without otherwise holding them. Here, an attempt to queue a task
 * will fail if no threads are immediately available to run it, so a
 * new thread will be constructed. This policy avoids lockups when
 * handling sets of requests that might have internal dependencies.
 * Direct handoffs generally require unbounded maximumPoolSizes to
 * avoid rejection of new submitted tasks. This in turn admits the
 * possibility of unbounded thread growth when commands continue to
 * arrive on average faster than they can be processed.  </li>
 *
 * <li><em> Unbounded queues.</em> Using an unbounded queue (for
 * example a {@link LinkedBlockingQueue} without a predefined
 * capacity) will cause new tasks to wait in the queue when all
 * corePoolSize threads are busy. Thus, no more than corePoolSize
 * threads will ever be created. (And the value of the maximumPoolSize
 * therefore doesn't have any effect.)  This may be appropriate when
 * each task is completely independent of others, so tasks cannot
 * affect each others execution; for example, in a web page server.
 * While this style of queuing can be useful in smoothing out
 * transient bursts of requests, it admits the possibility of
 * unbounded work queue growth when commands continue to arrive on
 * average faster than they can be processed.  </li>
 *
 * <li><em>Bounded queues.</em> A bounded queue (for example, an
 * {@link ArrayBlockingQueue}) helps prevent resource exhaustion when
 * used with finite maximumPoolSizes, but can be more difficult to
 * tune and control.  Queue sizes and maximum pool sizes may be traded
 * off for each other: Using large queues and small pools minimizes
 * CPU usage, OS resources, and context-switching overhead, but can
 * lead to artificially low throughput.  If tasks frequently block (for
 * example if they are I/O bound), a system may be able to schedule
 * time for more threads than you otherwise allow. Use of small queues
 * generally requires larger pool sizes, which keeps CPUs busier but
 * may encounter unacceptable scheduling overhead, which also
 * decreases throughput.  </li>
 *
 * </ol>
 *
 * </dd>
 *
 * <dt>Rejected tasks</dt>
 *
 * <dd>New tasks submitted in method {@link #execute(Runnable)} will be
 * <em>rejected</em> when the Executor has been shut down, and also when
 * the Executor uses finite bounds for both maximum threads and work queue
 * capacity, and is saturated.  In either case, the {@code execute} method
 * invokes the {@link
 * RejectedExecutionHandler#rejectedExecution(Runnable, ThreadPoolExecutor)}
 * method of its {@link RejectedExecutionHandler}.  Four predefined handler
 * policies are provided:
 *
 * <ol>
 *
 * <li> In the default {@link AbortPolicy}, the
 * handler throws a runtime {@link RejectedExecutionException} upon
 * rejection. </li>
 *
 * <li> In {@link CallerRunsPolicy}, the thread
 * that invokes {@code execute} itself runs the task. This provides a
 * simple feedback control mechanism that will slow down the rate that
 * new tasks are submitted. </li>
 *
 * <li> In {@link DiscardPolicy}, a task that
 * cannot be executed is simply dropped.  </li>
 *
 * <li>In {@link DiscardOldestPolicy}, if the
 * executor is not shut down, the task at the head of the work queue
 * is dropped, and then execution is retried (which can fail again,
 * causing this to be repeated.) </li>
 *
 * </ol>
 *
 * It is possible to define and use other kinds of {@link
 * RejectedExecutionHandler} classes. Doing so requires some care
 * especially when policies are designed to work only under particular
 * capacity or queuing policies. </dd>
 *
 * <dt>Hook methods</dt>
 *
 * <dd>This class provides {@code protected} overridable
 * {@link #beforeExecute(Thread, Runnable)} and
 * {@link #afterExecute(Runnable, Throwable)} methods that are called
 * before and after execution of each task.  These can be used to
 * manipulate the execution environment; for example, reinitializing
 * ThreadLocals, gathering statistics, or adding log entries.
 * Additionally, method {@link #terminated} can be overridden to perform
 * any special processing that needs to be done once the Executor has
 * fully terminated.
 *
 * <p>If hook or callback methods throw exceptions, internal worker
 * threads may in turn fail and abruptly terminate.</dd>
 *
 * <dt>Queue maintenance</dt>
 *
 * <dd>Method {@link #getQueue()} allows access to the work queue
 * for purposes of monitoring and debugging.  Use of this method for
 * any other purpose is strongly discouraged.  Two supplied methods,
 * {@link #remove(Runnable)} and {@link #purge} are available to
 * assist in storage reclamation when large numbers of queued tasks
 * become cancelled.</dd>
 *
 * <dt>Finalization</dt>
 *
 * <dd>A pool that is no longer referenced in a program <em>AND</em>
 * has no remaining threads will be {@code shutdown} automatically. If
 * you would like to ensure that unreferenced pools are reclaimed even
 * if users forget to call {@link #shutdown}, then you must arrange
 * that unused threads eventually die, by setting appropriate
 * keep-alive times, using a lower bound of zero core threads and/or
 * setting {@link #allowCoreThreadTimeOut(boolean)}.  </dd>
 *
 * </dl>
 *
 * <p><b>Extension example</b>. Most extensions of this class
 * override one or more of the protected hook methods. For example,
 * here is a subclass that adds a simple pause/resume feature:
 *
 *  <pre> {@code
 * class PausableThreadPoolExecutor extends ThreadPoolExecutor {
 *   private boolean isPaused;
 *   private ReentrantLock pauseLock = new ReentrantLock();
 *   private Condition unpaused = pauseLock.newCondition();
 *
 *   public PausableThreadPoolExecutor(...) { super(...); }
 *
 *   protected void beforeExecute(Thread t, Runnable r) {
 *     super.beforeExecute(t, r);
 *     pauseLock.lock();
 *     try {
 *       while (isPaused) unpaused.await();
 *     } catch (InterruptedException ie) {
 *       t.interrupt();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void pause() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = true;
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 *
 *   public void resume() {
 *     pauseLock.lock();
 *     try {
 *       isPaused = false;
 *       unpaused.signalAll();
 *     } finally {
 *       pauseLock.unlock();
 *     }
 *   }
 * }}</pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public class ThreadPoolExecutor extends AbstractExecutorService {
    /**
     * 主控制字段包含两个信息：
     *   工作线程数量, 表示有效线程数
     *   线程池运行状态, 指示线程池的运行状态
     *
     * The workerCount is the number of workers that have been
     * permitted to start and not permitted to stop.  The value may be
     * transiently different from the actual number of live threads,
     * for example when a ThreadFactory fails to create a thread when
     * asked, and when exiting threads are still performing
     * bookkeeping before terminating. The user-visible pool size is
     * reported as the current size of the workers set.
     *
     * runState 提供主要的线程池状态控制，取值：
     *
     *   RUNNING:  运行状态，接受新任务并处理排队的任务
     *   SHUTDOWN: 不接受新任务，但处理排队的任务
     *   STOP:     不接受新任务，不处理排队的任务，中断正在进行的任务
     *   TIDYING:  所有任务都已终止，workerCount 为零，转换到状态 TIDYING 的线程将运行 terminated() 钩子方法
     *   TERMINATED: terminated() 已执行完成
     *
     * 这5个值之间的数字，随着runState只会递增，而不会减，但不需要达到每个状态。变化时机是：
     *
     * RUNNING -> SHUTDOWN  调用了 shutdown(), 在finalize()方法中也会触发
     * (RUNNING or SHUTDOWN) -> STOP  调用了 shutdownNow()
     * SHUTDOWN -> TIDYING 当队列和池都为空时
     * STOP -> TIDYING 当池为空时
     * TIDYING -> TERMINATED 当 terminate() 钩子方法完成时
     *
     * Threads waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     *
     * Detecting the transition from SHUTDOWN to TIDYING is less
     * straightforward than you'd like because the queue may become
     * empty after non-empty and vice versa during SHUTDOWN state, but
     * we can only terminate if, after seeing that it is empty, we see
     * that workerCount is 0 (which sometimes entails a recheck -- see
     * below).
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

    /**
     * 29，{@code CAPACITY}的幂次
     */
    private static final int COUNT_BITS = Integer.SIZE - 3;
    /**
     * Integer共32位(其中1位符号位，31位存储)，在ThreadPoolExecutor中前3位存储运行状态，后29位存储任务数量
     * 0001 1111 1111 1111 1111 1111 1111 1111
     */
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
    /**
     * 运行状态
     * 1110 0000 0000 0000 0000 0000 0000 0000
     */
    private static final int RUNNING    = -1 << COUNT_BITS;
    /**
     * 0000 0000 0000 0000 0000 0000 0000 0000
     */
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    /**
     * 0010 0000 0000 0000 0000 0000 0000 0000
     */
    private static final int STOP       =  1 << COUNT_BITS;
    /**
     * 0100 0000 0000 0000 0000 0000 0000 0000
     * tryTerminate()方法会将状态修改为TIDYING，执行terminated()方法后，修改为TERMINATED状态
     */
    private static final int TIDYING    =  2 << COUNT_BITS;
    /**
     * 0110 0000 0000 0000 0000 0000 0000 0000
     */
    private static final int TERMINATED =  3 << COUNT_BITS;

    /*-------------ctl字段操作方法，取相应信息、生成ctl-----------------*/
    /**
     * 获得运行状态，~CAPACITY=RUNNING,即取高3位，后29位置0
     */
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    /**
     * 获得目前的任务数量,即取低29位
     */
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    /**
     * 将运行状态{@code rs} 和 工作线程数量{@code wc} 合到一个int中
     *
     * @param rs runState 运行状态
     * @param wc workerCount 工作线程数量
     * @return ctl的值
     */
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    /*-------------------------状态判断方法----------------------------*/
    /**
     * 比较两个状态值大小，若c小于s 返回true
     * @param c 状态值1
     * @param s 状态值2
     * @return c小于s 返回true
     */
    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    /**
     * 比较两个状态值大小，若c>=s 返回true
     * @param c 状态值1
     * @param s 状态值2
     * @return c>=s 返回true
     */
    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    /**
     * 比较两个状态值c和SHUTDOWN的大小，若c小于SHUTDOWN(即为RUNNING状态)，则返回true
     *
     * @param c 状态值
     * @return c小于SHUTDOWN 返回true
     */
    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    /**
     * CAS ctl+1
     */
    private boolean compareAndIncrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect + 1);
    }

    /**
     * 尝试对ctl的workerCount字段进行CAS操作，将值减1
     */
    private boolean compareAndDecrementWorkerCount(int expect) {
        return ctl.compareAndSet(expect, expect - 1);
    }

    /**
     * 对ctl的workerCount字段进行CAS操作，将值减1
     */
    private void decrementWorkerCount() {
        do {} while (! compareAndDecrementWorkerCount(ctl.get()));
    }

    /**
     * 等待执行的队列
     */
    private final BlockingQueue<Runnable> workQueue;

    /**
     * 当拥有锁时，才允许访问workers 和其他线程不安全的记录字段
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * 包含池中所有工作线程的集合, 仅在持有 mainLock 时访问
     */
    private final HashSet<Worker> workers = new HashSet<Worker>();

    /**
     * Wait condition to support awaitTermination
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * 记录线程池最大的线程数量，即workers.size()的峰值
     * 读写均只能在获得mainLock后，才允许访问
     */
    private int largestPoolSize;

    /**
     * 已完成任务数量，仅在工作线程终止时将w.completedTasks加到变量中
     * 读写均只能在获得mainLock后，才允许访问
     */
    private long completedTaskCount;

    /*-------------------以下为构造器的基本入参------------------*/
    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    /**
     * 线程空闲时保留的最大时长，以纳秒为单位保存
     */
    private volatile long keepAliveTime;
    /**
     * 如果为true，则适用于非核心线程的相同保活策略也适用于核心线程(即空闲时间超过{@code keepAliveTime}后，被终止)
     * 当为 false（默认值）时，核心线程永远不会由于空闲而终止
     */
    private volatile boolean allowCoreThreadTimeOut;
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    /**
     * 默认拒绝策略，为AbortPolicy
     */
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    /*-------------------线程认证使用，销毁时使用--------------------*/
    private static final RuntimePermission shutdownPerm =
        new RuntimePermission("modifyThread");

    /**
     * 执行终结器时要使用的上下文，可能null
     */
    private final AccessControlContext acc;

    /**
     * 工作线程类
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        private static final long serialVersionUID = 6138294804551838833L;
        /**
         * 执行此Worker任务的线程
         */
        final Thread thread;
        /**
         * 第一个执行的任务，可能为null
         */
        Runnable firstTask;
        /**
         * 已完成任务计数器
         */
        volatile long completedTasks;

        /**
         * 通过线程工厂创建线程，并初始化worker
         * @param firstTask 第一个执行的任务，可能为null
         */
        Worker(Runnable firstTask) {
            // 尝试中断时，会执行tryLock方法，即通过CAS将state从0改为1，但永远不会成功，因此不会被中断
            setState(-1);
            this.firstTask = firstTask;
            // 通过线程工厂，创建线程，其中的Runnable为当前对象
            this.thread = getThreadFactory().newThread(this);
        }

        /**
         * 线程的run方法，主逻辑由{@link #runWorker(Worker)}执行
         */
        public void run() {
            runWorker(this);
        }

        /*-----AQS相关方法------*/
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }

        /**
         * 中断已经启动的线程，即活跃线程
         */
        void interruptIfStarted() {
            Thread t;
            // state大于等于0，即已经启动的线程，且没有中断的，进行中断操作
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }

    /*---------------设置线程池状态-----------------*/
    /**
     * shutdown和shutdownNow方法调用，用于将状态修改为SHUTDOWN 或者 STOP
     * 将 runState 转换为给定的目标，或者至少已经是给定的目标
     *
     * @param targetState 新的状态, SHUTDOWN 或者 STOP
     */
    private void advanceRunState(int targetState) {
        for (;;) {
            int c = ctl.get();
            if (runStateAtLeast(c, targetState) ||
                ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))))
                break;
        }
    }

    /**
     * 如果（SHUTDOWN 并且池和队列为空）或（STOP 并且池为空），则转换到 TERMINATED 状态
     * 如果有其他条件终止但 workerCount 非零，则中断空闲的工作程序以确保关闭信号传播
     * 在关闭期间减少工作线程数量或从队列中删除任务，都会调用此方法
     */
    final void tryTerminate() {
        for (;;) {
            int c = ctl.get();
            // 如果线程池是运行状态，无需执行后面的逻辑
            // 或者是TIDYING、TERMINATED状态，说明线程池已经在终止了
            // 或者为SHUTDOWN状态但任务队列不为空，说明是shutdown方法，但仍有任务没执行完
            // 均直接返回
            if (isRunning(c) ||
                runStateAtLeast(c, TIDYING) ||
                (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
                return;
            // 如果工作线程数不为0，中断一个线程
            if (workerCountOf(c) != 0) {
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                    try {
                        terminated();
                    } finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    /*-----------控制工作线程中断的方法------------*/
    /**
     * 校验线程所属权限
     * 如果有安全管理器，请确保调用者通常有权关闭线程（请参阅shutdownPerm）
     * 如果通过，另外确保调用者被允许中断每个工作线程
     * 如果 SecurityManager 对某些线程进行特殊处理，即使第一次检查通过，这也可能不是真的。
     */
    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (Worker w : workers)
                    security.checkAccess(w.thread);
            } finally {
                mainLock.unlock();
            }
        }
    }

    /**
     * 中断所有线程，只在shutdownNow方法使用
     */
    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers)
                w.interruptIfStarted();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 中断可能正在等待任务的线程，以便它们可以终止
     *
     * @param onlyOne If true, 最多中断一个线程，仅从tryTerminate方法调用时为true
     * 在这种情况下，在所有线程当前都在等待的情况下，最多会中断一个等待的工作人员以传播关闭信号
     */
    private void interruptIdleWorkers(boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (Worker w : workers) {
                Thread t = w.thread;
                // 中断没有中断过的，空闲的(即tryLock成功)工作线程
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException ignore) {
                    } finally {
                        w.unlock();
                    }
                }
                if (onlyOne)
                    break;
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    /*---------杂项工具方法，大部分在ScheduledThreadPoolExecutor使用------------*/
    /**
     * 执行拒绝策略
     * 也在ScheduledThreadPoolExecutor中使用
     */
    final void reject(Runnable command) {
        handler.rejectedExecution(command, this);
    }

    /**
     * 在调用关闭时执行运行状态转换后的任何进一步清理
     */
    void onShutdown() {}

    /**
     * ScheduledThreadPoolExecutor使用
     *
     * @param shutdownOK true 如果SHUTDOWN状态需要返回true
     */
    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    /**
     * 将任务队列排到一个新列表中，通常使用 drainTo
     * 但是，如果是某些特殊的队列，drainTo移除失败，则使用循环移除
     */
    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            for (Runnable r : q.toArray(new Runnable[0])) {
                if (q.remove(r))
                    taskList.add(r);
            }
        }
        return taskList;
    }

    /*-------------------线程创建、运行和回收的方法-------------------*/
    /**
     * 检查是否可以根据当前池状态和给定上限（核心或最大值）添加新工作线程
     * 如果创建成功，调整工作线程数量(ctl)，将firstTask作为其第一个任务运行
     * 如果创建失败，返回false，若已对工作线程数量(ctl)或workers产生影响，则会执行回滚方法消除影响
     *
     * @param firstTask 新线程应该首先运行的任务（如果没有，则为 null）
     * @param core 如果为 true，则使用 corePoolSize 作为上限，否则为 maximumPoolSize
     * @return 如果成功返回true
     */
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            /*
             * 状态为RUNNING，判断结果为false，执行后续逻辑
             * 状态为STOP、TIDYING、TERMINATED，判断结果为true，直接返回false表示addWorker失败
             * 状态为SHUTDOWN时，只有firstTask为null，且workQueue不为空时，执行后续逻辑(创建工作线程来执行队列中的任务)
             */
            if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c);
                // 任务数量大于等于最大容量，或者大于等于核心线程/最大线程，视作失败
                if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                // 对ctl执行CAS+1，若成功说明线程池状态和工作数量没有发生变化，如果失败说明线程池状态或工作数量发送了变化
                if (compareAndIncrementWorkerCount(c))
                    // CAS成功，则跳出外层循环
                    break retry;
                c = ctl.get();
                // CAS失败，检查线程池状态是否发生了变化
                // 若线程池状态发生变化，跳转到外层循环执行，重新校验线程池状态
                // 若线程池状态没有变化，说明是工作线程数量变化导致CAS失败，执行内层循环，重新校验工作线程数量
                if (runStateOf(c) != rs)
                    continue retry;
            }
        }

        // 当workerAdded为true时，执行线程t的start方法，并将workerStarted置为true，表示新增的工作线程已启动(即新建线程成功)
        boolean workerStarted = false;
        // 将新增的工作线程添加到workers后，将workerAdded置为true
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    int rs = runStateOf(ctl.get());
                    // 再次校验线程池状态
                    // 线程池处于RUNNING态，或者线程池处于SHUTDOWN态且任务线程为null时允许创建新线程
                    if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive())
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        // 记录线程数量峰值
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            // 新线程没有启动，代表新建线程失败，执行回滚方法
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }

    /**
     * 回滚工作线程创建，只在{@link #addWorker}方法中调用
     * - 如果worker不为null，从workers中移除
     * - 减少ctl中工作线程数量
     * - 重新检查终止
     */
    private void addWorkerFailed(Worker w) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (w != null)
                workers.remove(w);
            decrementWorkerCount();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 清理和整理垂死的工作线程，仅从runWorker方法中调用
     * 除非completedAbruptly为true，否则认为workerCount已经调整过了，所以开头只当为true时，调整线程数量
     *
     * @param w 工作线程
     * @param completedAbruptly true代表工作线程因为异常而结束
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        // 异常退出，CAS将工作线程数量-1，若正常退出的线程，在getTask方法就已经完成了数量修改
        if (completedAbruptly)
            decrementWorkerCount();

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            completedTaskCount += w.completedTasks;
            workers.remove(w);
        } finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        // RUNNING或者SHUTDOWN态，才会进行以下判断，其他状态直接移除线程即可
        if (runStateLessThan(c, STOP)) {
            // 如果是非正常退出，无需进行以下判断，直接起一个新工作线程
            if (!completedAbruptly) {
                // 如果开启了allowCoreThreadTimeOut，则最小拥有线程为0
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if (min == 0 && ! workQueue.isEmpty())
                    min = 1;
                // 线程数量大于等于最小值，不需要替换直接return
                if (workerCountOf(c) >= min)
                    return;
            }
            addWorker(null, false);
        }
    }

    /**
     * 跟据配置从阻塞队列中拉取或定时拉取任务，符合以下条件时，返回null以回收工作线程
     * 1. 超过最大工作线程数 (由于调用了{@link #setMaximumPoolSize})
     * 2. 线程池状态至少是STOP
     * 3. 线程池状态是SHUTDOWN 且 workQueue为空
     * 4. 获取任务超时，且开始了时间限制{@code allowCoreThreadTimeOut || wc > corePoolSize}
     *    且如果workQueue是空的，或这个工作线程不是池中的最后一个线程
     *
     * @return 任务，可能为null，在这种情况下工作线程会被回收，workerCount递减
     */
    private Runnable getTask() {
        // 上次拉取任务队列是否超时(即拉取后获得的内容是否为null，若为null则置为true)
        boolean timedOut = false;

        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // 线程池为SHUTDOWN态且workQueue是空的，或者线程池状态为STOP、TIDYING、TERMINATED时，不获取任务，并将workCount-1
            if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            // allowCoreThreadTimeOut为true，或者当前工作线程大于核心线程数时，开启时间限制
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            // 若wc > maximumPoolSize成立，则通常wc > 1也成立，因此直接尝试减少工作线程
            // 若开启了时间限制，且获取任务超时，且wc > 1成立，尝试减少工作线程
            // 若开启了时间限制，且获取任务超时，且wc == 1,但workQueue为空，也尝试减少工作线程
            if ((wc > maximumPoolSize || (timed && timedOut))
                && (wc > 1 || workQueue.isEmpty())) {
                if (compareAndDecrementWorkerCount(c))
                    return null;
                continue;
            }

            try {
                Runnable r = timed ?
                    // 等待最大活跃时间，会阻塞直到获取到任务或者超时
                    workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                    // 直接获取，会阻塞知道获取到任务
                    workQueue.take();
                if (r != null)
                    return r;
                timedOut = true;
            } catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }

    /**
     * Main worker run loop.  Repeatedly gets tasks from queue and
     * executes them, while coping with a number of issues:
     *
     * 1. We may start out with an initial task, in which case we
     * don't need to get the first one. Otherwise, as long as pool is
     * running, we get tasks from getTask. If it returns null then the
     * worker exits due to changed pool state or configuration
     * parameters.  Other exits result from exception throws in
     * external code, in which case completedAbruptly holds, which
     * usually leads processWorkerExit to replace this thread.
     *
     * 2. Before running any task, the lock is acquired to prevent
     * other pool interrupts while the task is executing, and then we
     * ensure that unless pool is stopping, this thread does not have
     * its interrupt set.
     *
     * 3. Each task run is preceded by a call to beforeExecute, which
     * might throw an exception, in which case we cause thread to die
     * (breaking loop with completedAbruptly true) without processing
     * the task.
     *
     * 4. Assuming beforeExecute completes normally, we run the task,
     * gathering any of its thrown exceptions to send to afterExecute.
     * We separately handle RuntimeException, Error (both of which the
     * specs guarantee that we trap) and arbitrary Throwables.
     * Because we cannot rethrow Throwables within Runnable.run, we
     * wrap them within Errors on the way out (to the thread's
     * UncaughtExceptionHandler).  Any thrown exception also
     * conservatively causes thread to die.
     *
     * 5. After task.run completes, we call afterExecute, which may
     * also throw an exception, which will also cause thread to
     * die. According to JLS Sec 14.20, this exception is the one that
     * will be in effect even if task.run throws.
     *
     * The net effect of the exception mechanics is that afterExecute
     * and the thread's UncaughtExceptionHandler have as accurate
     * information as we can provide about any problems encountered by
     * user code.
     *
     * @param w the worker
     */
    final void runWorker(Worker w) {
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        /*
         * 这里不是解锁操作，这里是为了设置state = 0 以及 ExclusiveOwnerThread = null.因为起始状态state = -1
         * 不允许任何线程抢占锁，这里就是初始化操作
         */
        w.unlock();
        /*
         * 是否突然退出标志位
         * completedAbruptly = true，代表发生异常，突然退出
         * completedAbruptly = false, 正常退出
         */
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) || (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP)))
                        && !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        } finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    /*----------------构造器---------------*/
    /**
     * 线程池构造1，使用默认的线程工厂和默认拒绝策略
     *
     * @param corePoolSize 保留在池中的线程数，即使它们是空闲的，除非设置了{@code allowCoreThreadTimeOut}
     * @param maximumPoolSize 池中允许最大的线程池
     * @param keepAliveTime 当线程池中的线程数大于{@code corePoolSize}时，线程等待的最大空闲等待时间
     * @param unit {@code keepAliveTime}的单位
     * @param workQueue 队列用于存储未执行的任务. 队列仅保存由{@code execute}方法提交的Runnable任务
     * @throws IllegalArgumentException 以下任意一点成立:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException 如果{@code workQueue}为null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), defaultHandler);
    }

    /**
     * 线程池构造2，使用指定的线程工厂和默认拒绝策略
     *
     * @param corePoolSize 保留在池中的线程数，即使它们是空闲的，除非设置了{@code allowCoreThreadTimeOut}
     * @param maximumPoolSize 池中允许最大的线程池
     * @param keepAliveTime 当线程池中的线程数大于{@code corePoolSize}时，线程等待的最大空闲等待时间
     * @param unit {@code keepAliveTime}的单位
     * @param workQueue 队列用于存储未执行的任务. 队列仅保存由{@code execute}方法提交的Runnable任务
     * @param threadFactory 线程池创建线程时使用的工厂
     * @throws IllegalArgumentException 以下任意一点成立:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException 如果{@code workQueue}为null或{@code threadFactory}为null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             threadFactory, defaultHandler);
    }

    /**
     * 线程池构造3，使用默认的线程工厂和指定拒绝策略
     *
     * @param corePoolSize 保留在池中的线程数，即使它们是空闲的，除非设置了{@code allowCoreThreadTimeOut}
     * @param maximumPoolSize 池中允许最大的线程池
     * @param keepAliveTime 当线程池中的线程数大于{@code corePoolSize}时，线程等待的最大空闲等待时间
     * @param unit {@code keepAliveTime}的单位
     * @param workQueue 队列用于存储未执行的任务. 队列仅保存由{@code execute}方法提交的Runnable任务
     * @param handler 当线程和阻塞队列都满时，执行的拒绝策略
     * @throws IllegalArgumentException 以下任意一点成立:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException 如果{@code workQueue}为null或{@code handler}为null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             Executors.defaultThreadFactory(), handler);
    }

    /**
     * 线程池构造4，使用指定的线程工厂和指定拒绝策略(真正初始化逻辑)
     *
     * @param corePoolSize 保留在池中的线程数，即使它们是空闲的，除非设置了{@code allowCoreThreadTimeOut}
     * @param maximumPoolSize 池中允许最大的线程池
     * @param keepAliveTime 当线程池中的线程数大于{@code corePoolSize}时，线程等待的最大空闲等待时间
     * @param unit {@code keepAliveTime}的单位
     * @param workQueue 队列用于存储未执行的任务. 队列仅保存由{@code execute}方法提交的Runnable任务
     * @param threadFactory 线程池创建线程时使用的工厂
     * @param handler 当线程和阻塞队列都满时，执行的拒绝策略
     * @throws IllegalArgumentException 以下任意一点成立:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException 如果{@code workQueue}为null或{@code threadFactory}为null或{@code handler}为null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        // 入参合法校验，与注释说明的异常抛出逻辑一致
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.acc = System.getSecurityManager() == null ? null : AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     *
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@code RejectedExecutionHandler}.
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *         {@code RejectedExecutionHandler}, if the task
     *         cannot be accepted for execution
     * @throws NullPointerException if {@code command} is null
     */
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * Proceed in 3 steps:
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         */
        int c = ctl.get();
        // 当前任务数 小于 核心线程数，尝试以核心线程添加任务
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     *
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
     *
     * @throws SecurityException {@inheritDoc}
     */
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // 校验
            checkShutdownAccess();
            // 修改线程池状态为SHUTDOWN
            advanceRunState(SHUTDOWN);
            // 将空闲且未被打断的工作线程，执行interrupt
            interruptIdleWorkers();
            // 空方法
            onShutdown();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution. These tasks are drained (removed)
     * from the task queue upon return from this method.
     *
     * <p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
     *
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  This implementation
     * cancels tasks via {@link Thread#interrupt}, so any task that
     * fails to respond to interrupts may never terminate.
     *
     * @throws SecurityException {@inheritDoc}
     */
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(STOP);
            interruptWorkers();
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

    /*-----------------以下3个方法用于查询线程池状态----------------*/
    /**
     * 判断线程池是否shutdown
     * 即当前状态的值大于等于SHUTDOWN,(非RUNNING状态)
     *
     * @return {@code true} 当线程池为非RUNNING状态, 反之返回{@code false}
     */
    public boolean isShutdown() {
        return ! isRunning(ctl.get());
    }

    /**
     * 如果此执行程序在 {@link #shutdown} 或 {@link #shutdownNow} 之后正在终止但尚未完全终止，则返回 true
     * 即为SHUTDOWN、STOP、TIDYING状态时返回{@code true}
     *
     * @return {@code true} 如果终止但尚未终止
     */
    public boolean isTerminating() {
        int c = ctl.get();
        return ! isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    /**
     * 判断线程池是否为terminated状态
     * 即当前状态的值是否大于等于TERMINATED(根据定义，只有TERMINATED状态时，才会返回true)
     *
     * @return {@code true} 当线程池为TERMINATED状态, 反之返回{@code false}
     */
    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (runStateAtLeast(ctl.get(), TERMINATED))
                    return true;
                if (nanos <= 0)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 调用shutdown，在线程池被回收前
     */
    protected void finalize() {
        SecurityManager sm = System.getSecurityManager();
        if (sm == null || acc == null) {
            shutdown();
        } else {
            PrivilegedAction<Void> pa = () -> { shutdown(); return null; };
            AccessController.doPrivileged(pa, acc);
        }
    }

    /**
     * 为设置一个新的线程工厂
     *
     * @param threadFactory 新的线程工厂
     * @throws NullPointerException 如果新的线程工厂为null
     * @see #getThreadFactory
     */
    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null)
            throw new NullPointerException();
        this.threadFactory = threadFactory;
    }

    /**
     * 返回当前的线程工厂
     *
     * @return 当前的线程工厂
     * @see #setThreadFactory(ThreadFactory)
     */
    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    /**
     * 为不执行的任务设置一个新的拒绝策略
     *
     * @param handler 新的拒绝策略
     * @throws NullPointerException 如果新的拒绝策略为null
     * @see #getRejectedExecutionHandler
     */
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        this.handler = handler;
    }

    /**
     * 返回当前的不执行任务的拒绝策略
     *
     * @return 当前的拒绝策略
     * @see #setRejectedExecutionHandler(RejectedExecutionHandler)
     */
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return handler;
    }

    /**
     * 设置核心线程数
     * 如果新值小于当前值，多余的现有线程将在下次空闲时终止
     * 如果更大，则视任务队列情况，启动新线程以执行队列中的任务
     *
     * @param corePoolSize 新核心线程数量
     * @throws IllegalArgumentException 如果{@code corePoolSize < 0}
     * @see #getCorePoolSize
     */
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0)
            throw new IllegalArgumentException();
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        // 当前线程数大于新修改的线程
        if (workerCountOf(ctl.get()) > corePoolSize)
            interruptIdleWorkers();
        else if (delta > 0) {
            // 当前线程小于新修改的线程，创建尽可能少的核心线程
            // 1. 首先取差值和队列中任务数量的较小值k
            // 2. 先拟定创建k个核心线程，并逐一调用addWorker创建
            // 3. 每创建一个即检查任务队列是否为空，若为空说明线程已足够，停止创建；否则执行至循环结束
            int k = Math.min(delta, workQueue.size());
            while (k-- > 0 && addWorker(null, true)) {
                if (workQueue.isEmpty())
                    break;
            }
        }
    }

    /**
     * 返回核心线程数量
     *
     * @return 核心线程数量
     * @see #setCorePoolSize
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /*---------------------以下三个方法，可以实现预加载核心线程----------------------*/
    /**
     * 这会覆盖仅在执行新任务时启动核心线程的默认策略，这会覆盖仅在执行新任务时启动核心线程的默认策略
     * 如果核心线程已经到达上限，则会返回false
     *
     * @return {@code true} 为创建成功
     */
    public boolean prestartCoreThread() {
        // 如果线程数小于核心数，且创建核心线程成功，则返回true
        return workerCountOf(ctl.get()) < corePoolSize &&
            addWorker(null, true);
    }

    /**
     * 当线程数量小于核心线程数时，起一个核心线程，当线程数量为0时，起一个非核心线程
     */
    void ensurePrestart() {
        int wc = workerCountOf(ctl.get());
        if (wc < corePoolSize)
            addWorker(null, true);
        else if (wc == 0)
            addWorker(null, false);
    }

    /**
     * 启动所有核心线程，使它们空闲等待工作。这会覆盖仅在执行新任务时启动核心线程的默认策略
     *
     * @return 启动的核心线程数量
     */
    public int prestartAllCoreThreads() {
        int n = 0;
        while (addWorker(null, true))
            ++n;
        return n;
    }

    /**
     * 返回allowCoreThreadTimeOut
     *
     * @return allowCoreThreadTimeOut
     * @since 1.6
     */
    public boolean allowsCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    /**
     * 修改{@code allowCoreThreadTimeOut}的值，为避免持续的线程替换，设置 {@code true} 时的 keep-alive 时间必须大于零
     * 通常应该在主动使用池之前调用此方法
     *
     * @param value {@code true} or {@code false}
     * @throws IllegalArgumentException 如果value为{@code true}且当前的{@code keepAliveTime}不大于0
     * @since 1.6
     */
    public void allowCoreThreadTimeOut(boolean value) {
        if (value && keepAliveTime <= 0)
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        if (value != allowCoreThreadTimeOut) {
            allowCoreThreadTimeOut = value;
            if (value)
                interruptIdleWorkers();
        }
    }

    /**
     * 设置允许的最大线程数。这会覆盖构造函数中设置的任何值。如果新值小于当前值，多余的现有线程将在下次空闲时终止(不会立即生效)
     *
     * @param maximumPoolSize 新的最大线程数
     * @throws IllegalArgumentException 如果新的最大线程数小于0, 或者小于{@linkplain #getCorePoolSize 核心线程数}
     * @see #getMaximumPoolSize
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < corePoolSize)
            throw new IllegalArgumentException();
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(ctl.get()) > maximumPoolSize)
            interruptIdleWorkers();
    }

    /**
     * 返回允许的最大线程数
     *
     * @return 允许的最大线程数
     * @see #setMaximumPoolSize
     */
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * 设置线程在终止之前可以保持空闲的时间限制，如果当前池中的线程数超过核心数{@code corePoolSize}
     * 则在等待此时间{@code getKeepAliveTime}后没有处理任务，多余的线程将被终止
     *
     * @param time 保持空闲的时间(时间值为零将导致多余的线程在执行任务后立即终止)
     * @param unit {@code time}的时间单位
     * @throws IllegalArgumentException 如果{@code time}小于0 或者{@code time}为0且{@code allowsCoreThreadTimeOut}为true
     * @see #getKeepAliveTime(TimeUnit)
     */
    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0)
            throw new IllegalArgumentException();
        // allowCoreThreadTimeOut为true时，time不允许为0
        if (time == 0 && allowsCoreThreadTimeOut())
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        long keepAliveTime = unit.toNanos(time);
        long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0)
            interruptIdleWorkers();
    }

    /**
     * 返回线程保持活跃的最大空闲时间
     *
     * @param unit 返回结果的时间单位
     * @return 线程保持活跃的最大空闲时间
     * @see #setKeepAliveTime(long, TimeUnit)
     */
    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(keepAliveTime, TimeUnit.NANOSECONDS);
    }

    /*--------------用户级别的等待队列操作公用方法----------------*/
    /**
     * 返回此执行程序使用的任务队列。访问任务队列主要用于调试和监控，此队列可能正在使用中，检索任务队列不会阻止排队的任务执行
     *
     * @return 任务等待队列
     */
    public BlockingQueue<Runnable> getQueue() {
        return workQueue;
    }

    /**
     * 如果该任务存在，则从执行程序的内部队列中删除该任务
     * 如果是通过{@link #submit}方法提交的任务，则本方法无法移除，因为此时保存在workQueue中的是{@link FutureTask}而非Runnable
     * 但是，在这种情况下，可以使用方法 {@link #purge} 删除那些取消态的Future
     *
     * @param task 待移除的task
     * @return {@code true} 如果任务被移除
     */
    public boolean remove(Runnable task) {
        boolean removed = workQueue.remove(task);
        tryTerminate();
        return removed;
    }

    /**
     * 尝试删除所有取消态的 {@link Future}任务
     * 取消态的任务不会被执行，但是会一直存放在队列中，直到被工作线程主动移除，调用此方法替换为现在尝试移除他们
     * 此方法在其他线程的干扰(其他线程操作workQueue)下，可能失败
     */
    public void purge() {
        final BlockingQueue<Runnable> q = workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            // 通过迭代器遍历，如果是FutureTask，且已被取消，则移除
            while (it.hasNext()) {
                Runnable r = it.next();
                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
                    it.remove();
            }
        } catch (ConcurrentModificationException fallThrough) {
            // 如果遇到异常，则先转化为数组后，再遍历
            for (Object r : q.toArray())
                if (r instanceof Future<?> && ((Future<?>)r).isCancelled())
                    q.remove(r);
        }

        tryTerminate();
    }

    /*-------------Statistics，获取线程池的各项运行数据----------------*/
    /**
     * 返回池中的当前线程数
     *
     * @return 线程数量
     */
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // 如果状态为TIDYING或TERMINATED 则返回0，否则返回worker的数量
            return runStateAtLeast(ctl.get(), TIDYING) ? 0 : workers.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回正在执行任务的线程数量
     *
     * @return 正在执行任务的线程数量
     */
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (Worker w : workers)
                if (w.isLocked())
                    ++n;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回池中同时存在的最大线程数
     *
     * @return 池中同时存在的最大线程数
     */
    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return largestPoolSize;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回已安排执行的任务的大致总数。因为任务和线程的状态在计算过程中可能会动态变化，所以返回的值只是一个近似值
     *
     * @return 已安排执行的任务的大致总数
     */
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // 总任务数 = 计数器数量 + 各线程计数器数量 + 执行中线程数 + 等待队列数量
            long n = completedTaskCount;
            for (Worker w : workers) {
                n += w.completedTasks;
                if (w.isLocked())
                    ++n;
            }
            return n + workQueue.size();
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回已完成执行的任务总数(近似值)
     *
     * @return 已完成执行的大致任务总数
     */
    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            // 已完成数 = 计数器数量 + 各线程计数器数量
            long n = completedTaskCount;
            for (Worker w : workers)
                n += w.completedTasks;
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    /**
     * 返回线程池的描述，如：
     * java.util.concurrent.ThreadPoolExecutor@73a28541[Running, pool size = 1, active threads = 1, queued tasks = 1,
     * completed tasks = 0]
     *
     * @return 标识此池的字符串及其状态
     */
    public String toString() {
        // 完成任务数
        long ncompleted;
        // 线程池线程数，活跃线程数(即任务执行中的线程数)
        int nworkers, nactive;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            ncompleted = completedTaskCount;
            nactive = 0;
            nworkers = workers.size();
            for (Worker w : workers) {
                ncompleted += w.completedTasks;
                if (w.isLocked())
                    ++nactive;
            }
        } finally {
            mainLock.unlock();
        }
        // 获取线程池运行状态
        int c = ctl.get();
        String rs = (runStateLessThan(c, SHUTDOWN) ? "Running" : (runStateAtLeast(c, TERMINATED) ? "Terminated" : "Shutting down"));
        // 拼接结果
        return super.toString() +
            "[" + rs +
            ", pool size = " + nworkers +
            ", active threads = " + nactive +
            ", queued tasks = " + workQueue.size() +
            ", completed tasks = " + ncompleted +
            "]";
    }

    /*------------扩展钩，通过extends来扩展以下3个方法------------*/
    /**
     * 任务{@code r} 执行前调用，此方法由执行任务 {@code r} 的线程 {@code t} 调用(runWorker方法)
     * 可用于重新初始化 ThreadLocals，或执行日志记录等等
     *
     * @param t 执行任务r的线程 {@code r}
     * @param r 被执行的任务
     */
    protected void beforeExecute(Thread t, Runnable r) { }

    /**
     * 任务{@code r} 执行完成后调用。此方法由执行任务{@code r}的线程调用。
     * {@code t} 如果非 null，则 Throwable 是未捕获的 {@code RuntimeException} 或 {@code Error} 导致执行突然终止。
     *
     * @param r 被执行的任务
     * @param t 导致执行终止的异常, 如果正常执行则为null
     */
    protected void afterExecute(Runnable r, Throwable t) { }

    /**
     * Executor 终止时调用的方法
     */
    protected void terminated() { }

    /*--------------预定义的4个拒绝策略-----------------*/
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        public CallerRunsPolicy() { }

        /**
         * 在调用者的线程中执行任务r，如果线程池已经shutdown，则丢弃任务r
         *
         * @param r 需要执行的任务
         * @param e 执行任务r的执行者，线程池
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }

    public static class AbortPolicy implements RejectedExecutionHandler {
        public AbortPolicy() { }

        /**
         * 直接抛出异常 {@code RejectedExecutionException}.
         *
         * @param r 需要执行的任务
         * @param e 执行任务r的执行者，线程池
         * @throws RejectedExecutionException always
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }

    public static class DiscardPolicy implements RejectedExecutionHandler {
        public DiscardPolicy() { }

        /**
         * 什么都不做，即丢弃该任务
         *
         * @param r 需要执行的任务
         * @param e 执行任务r的执行者，线程池
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        public DiscardOldestPolicy() { }

        /**
         * 移除等待队列中最早的任务(即队首元素)，并执行任务r，前提是线程池e不为shutdown状态
         *
         * @param r 需要执行的任务
         * @param e 执行任务r的执行者，线程池
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
}
