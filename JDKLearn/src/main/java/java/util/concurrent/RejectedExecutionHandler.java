package java.util.concurrent;

/**
 * 当任务不能被{@link ThreadPoolExecutor}处理时的拒绝策略
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface RejectedExecutionHandler {

    /**
     * 线程池无法处理时，调用执行
     *
     * @param r 请求执行的可运行任务
     * @param executor 任务执行者(线程池)
     * @throws RejectedExecutionException 如果没有其他措施，抛出该异常
     */
    void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
}
