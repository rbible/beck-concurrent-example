package com.olmlo.thread.chapter8.recipe05.core;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Create an Executor and submits ten Task objects for its execution. It writes information
 * about the executor to see its evolution.
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Task task = new Task(random.nextInt(10000));
            executor.submit(task);
        }

        for (int i = 0; i < 5; i++) {
            showLog(executor);
            TimeUnit.SECONDS.sleep(1);
        }

        executor.shutdown();

        /* Write information about the executor */
        for (int i = 0; i < 5; i++) {
            showLog(executor);
            TimeUnit.SECONDS.sleep(1);
        }

        /* Wait for the finalization of the executor */
        executor.awaitTermination(1, TimeUnit.DAYS);

        /* Write a message to indicate the end of the program */
        System.out.printf("Main: End of the program.\n");

    }

    /**
     * Method that writes in the console information about an executor
     * 
     * @param executor
     *            Executor this method is going to process
     */
    private static void showLog(ThreadPoolExecutor executor) {
        System.out.printf("*********************\n");
        System.out.printf("Main: Executor Log");
        System.out.printf("Main: Executor: Core Pool Size: %d\n", executor.getCorePoolSize());
        System.out.printf("Main: Executor: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Main: Executor: Active Count: %d\n", executor.getActiveCount());
        System.out.printf("Main: Executor: Task Count: %d\n", executor.getTaskCount());
        System.out.printf("Main: Executor: Completed Task Count: %d\n", executor.getCompletedTaskCount());
        System.out.printf("Main: Executor: Shutdown: %s\n", executor.isShutdown());
        System.out.printf("Main: Executor: Terminating: %s\n", executor.isTerminating());
        System.out.printf("Main: Executor: Terminated: %s\n", executor.isTerminated());
        System.out.printf("*********************\n");
    }

}

class Task implements Runnable {

    /**
     * Number of milliseconds this task is going to sleep the thread
     */
    private long milliseconds;

    /**
     * Constructor of the task. Initializes its attributes
     * 
     * @param milliseconds
     *            Number of milliseconds this task is going to sleep the thread
     */
    public Task(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Main method of the task. Sleep the thread the number of millisecons specified by the milliseconds attribute.
     */
    @Override
    public void run() {
        System.out.printf("%s: Begin\n", Thread.currentThread().getName());
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: End\n", Thread.currentThread().getName());

    }

}
