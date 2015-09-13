package com.olmlo.thread.pool.base;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RejectedExecutionHandlerDemo {

    public static void main(String[] args) {

        // Create the controller for the Rejected tasks
        RejectedTaskController rejectedTaskController = new RejectedTaskController();

        // Create the executor and establish the controller for the Rejected tasks
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        threadPoolExecutor.setRejectedExecutionHandler(rejectedTaskController);

        // Lauch three tasks
        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 3; i++) {
            RejectedDemoTask task = new RejectedDemoTask("Task" + i);
            threadPoolExecutor.submit(task);
        }

        // Shutdown the executor
        System.out.printf("Main: Shuting down the Executor.\n");
        threadPoolExecutor.shutdown();

        // Send another task
        System.out.printf("Main: Sending another Task.\n");
        RejectedDemoTask task = new RejectedDemoTask("RejectedTask");
        threadPoolExecutor.submit(task);

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
        System.out.printf("\nRejectedTaskController: The task %s has been rejected\n" + "RejectedTaskController: %s\n"
                + "RejectedTaskController: Terminating: %s\n" + "RejectedTaksController: Terminated: %s\n\n", r.toString(),
                executor.toString(), executor.isTerminating(), executor.isTerminated());
    }
}

class RejectedDemoTask implements Runnable {

    /**
     * Name of the task
     */
    private String name;

    /**
     * Constructor of the class. It initializes the attributes of the class
     * @param name The name of the task
     */
    public RejectedDemoTask(String name) {
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
        System.out.printf("Task %s: Ending\n\n", name);
    }

    /**
     * Returns the name of the task
     */
    public String toString() {
        return name;
    }

}
