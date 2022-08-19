import java.util.concurrent.*;

public class GCExecutionData {
    int numThreads;
    Executor executorService;
    BlockingQueue<Runnable> workQueue;

    GCExecutionData(int threadCount) {
        numThreads = threadCount;
        workQueue = new ArrayBlockingQueue<Runnable>(numThreads);
        executorService = new ThreadPoolExecutor(numThreads,
                numThreads,
                10,
                TimeUnit.SECONDS,
                workQueue);
    }
}