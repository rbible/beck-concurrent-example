package com.olmlo.thread.chapter4.recipe12.core;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example
 *
 */
public class Main {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {

        // Create the controller for the Rejected tasks
        RejectedTaskController controller = new RejectedTaskController();
        // Create the executor and establish the controller for the Rejected tasks
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.setRejectedExecutionHandler(controller);

        // Lauch three tasks
        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 3; i++) {
            Task task = new Task("Task" + i);
            executor.submit(task);
        }

        // Shutdown the executor
        System.out.printf("Main: Shuting down the Executor.\n");
        executor.shutdown();

        // Send another task
        System.out.printf("Main: Sending another Task.\n");
        Task task = new Task("RejectedTask");
        executor.submit(task);

        // The program ends
        System.out.printf("Main: End.\n");
    }
}

class RejectedTaskController implements RejectedExecutionHandler {

    /**
     * Method that will be executed for each rejected task
     * @param r Task that has been rejected
     * @param executor Executor that has rejected the task
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.printf("RejectedTaskController: The task %s has been rejected\n", r.toString());
        System.out.printf("RejectedTaskController: %s\n", executor.toString());
        System.out.printf("RejectedTaskController: Terminating: %s\n", executor.isTerminating());
        System.out.printf("RejectedTaksController: Terminated: %s\n", executor.isTerminated());
    }

}

class Task implements Runnable {

    /**
     * Name of the task
     */
    private String name;

    /**
     * Constructor of the class. It initializes the attributes of the class
     * @param name The name of the task
     */
    public Task(String name) {
        this.name = name;
    }

    /**
     * Main method of the task. Waits a random period of time
     */
    @Override
    public void run() {
        System.out.printf("Task %s: Starting\n", name);
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("Task %s: ReportGenerator: Generating a report during %d seconds\n", name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Task %s: Ending\n", name);
    }

    /**
     * Returns the name of the task
     */
    public String toString() {
        return name;
    }

}
