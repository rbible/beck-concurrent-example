package com.olmlo.thread.pool.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. It creates all the elements for the execution and writes information about the Fork/Join
 * pool that executes the task
 *
 */
public class ForkJoinPoolAndRecursiveDemo2 {

    public static void main(String[] args) throws Exception {

        /* Create the Fork/Join pool */
        ForkJoinPool pool = new ForkJoinPool();

        int array[] = new int[1000];
        RecursiveTask task1 = new RecursiveTask(array, 0, array.length);
        pool.execute(task1);

        /* Wait for the finalization of the task writing information about the pool every second */
        while (!task1.isDone()) {
            showLog(pool);
            TimeUnit.SECONDS.sleep(1);
        }

        /* Shutdown the pool */
        pool.shutdown();

        /* Wait for the finalization of the pool */
        pool.awaitTermination(1, TimeUnit.DAYS);
        showLog(pool);
        System.out.printf("Main: End of the program.\n");
    }

    /* This method writes information about a Fork/Join pool */
    private static void showLog(ForkJoinPool pool) {
        System.out.printf("**********************\n");
        System.out.printf("Main: Fork/Join Pool log\n");
        System.out.printf("Main: Fork/Join Pool: Parallelism: %d\n", pool.getParallelism());
        System.out.printf("Main: Fork/Join Pool: Pool Size: %d\n", pool.getPoolSize());
        System.out.printf("Main: Fork/Join Pool: Active Thread Count: %d\n", pool.getActiveThreadCount());
        System.out.printf("Main: Fork/Join Pool: Running Thread Count: %d\n", pool.getRunningThreadCount());
        System.out.printf("Main: Fork/Join Pool: Queued Submission: %d\n", pool.getQueuedSubmissionCount());
        System.out.printf("Main: Fork/Join Pool: Queued Tasks: %d\n", pool.getQueuedTaskCount());
        System.out.printf("Main: Fork/Join Pool: Queued Submissions: %s\n", pool.hasQueuedSubmissions());
        System.out.printf("Main: Fork/Join Pool: Steal Count: %d\n", pool.getStealCount());
        System.out.printf("Main: Fork/Join Pool: Terminated : %s\n", pool.isTerminated());
        System.out.printf("**********************\n");
    }

}

class RecursiveTask extends RecursiveAction {

    private static final long serialVersionUID = 1L;

    private int array[], start, end;

    public RecursiveTask(int array[], int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    /**
     * Main method of the task. If it has to process more that 100 elements, it divides that set it two sub-sets and
     * creates two task to process them. Otherwise, it increments directly the elements it has to process.
     */
    @Override
    protected void compute() {
        if (end - start > 100) {
            int mid = (start + end) / 2;
            RecursiveTask task1 = new RecursiveTask(array, start, mid);
            RecursiveTask task2 = new RecursiveTask(array, mid, end);

            task1.fork();
            task2.fork();

            task1.join();
            task2.join();
        } else {
            for (int i = start; i < end; i++) {
                array[i]++;
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
