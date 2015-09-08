package com.olmlo.thread.base;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Execute a task trough an executor, waits
 * 2 seconds and then cancel the task.
 */
public class FutureCancelDemo {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        System.out.printf("Main: Executing the Task\n\n");
        // Send the task to the executor
        Future<String> result = executor.submit(new Task());

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cancel the task, finishing its execution
        System.out.printf("\nMain: Cancelling the Task\n");
        result.cancel(true);
        // Verify that the task has been cancelled
        System.out.printf("Main: Cancelled: %s\n" + "Main: Done: %s\n", result.isCancelled(), result.isDone());

        executor.shutdown();
        System.out.printf("Main: The executor has finished\n");
    }

}

class Task implements Callable<String> {

    /**
     * Main method of the task. It has an infinite loop that writes a message to
     * the console every 100 milliseconds
     */
    @Override
    public String call() throws Exception {
        while (true) {
            System.out.printf("Task: Test\n");
            Thread.sleep(100);
        }
    }
}
