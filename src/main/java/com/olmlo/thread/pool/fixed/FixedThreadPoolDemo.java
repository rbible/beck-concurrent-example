package com.olmlo.thread.pool.fixed;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Creates a server and 100 request of the Task class
 * that sends to the server
 *
 */
public class FixedThreadPoolDemo {

    public static void main(String[] args) {
        // Create the server
        Server server = new Server();

        // Send 100 request to the server and finish
        for (int i = 0; i < 30; i++) {
            Task task = new Task("Task " + i);
            server.executeTask(task);
        }
        server.endServer();
    }
}

class Server {

    /**
     * ThreadPoolExecutors to manage the execution of the request
     */
    private ThreadPoolExecutor executor;

    /**
     * Constructor of the class. Creates the executor object
     */
    public Server() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    /**
     * This method is called when a request to the server is made. The 
     * server uses the executor to execute the request that it receives
     * @param task The request made to the server
     */
    public void executeTask(Task task) {
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task);
        System.out.printf("Server: Pool Size: %d\n", executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n", executor.getActiveCount());
        System.out.printf("Server: Task Count: %d\n", executor.getTaskCount());
        System.out.printf("Server: Completed Tasks: %d\n\n", executor.getCompletedTaskCount());
    }

    /**
     * This method shuts down the executor
     */
    public void endServer() {
        executor.shutdown();
    }
}

class Task implements Runnable {

    /**
     * The start date of the task
     */
    private Date initDate;

    /**
     * The name of the task
     */
    private String name;

    /**
     * Constructor of the class. Initializes the name of the task
     * @param name Name assigned to the task
     */
    public Task(String name) {
        initDate = new Date();
        this.name = name;
    }

    /**
     * This method implements the execution of the task. Waits a random period of time and finish
     */
    @Override
    public void run() {
        System.out.printf("%s: %s: Created on: %s\n", Thread.currentThread().getName(), name, initDate);
        System.out.printf("%s: %s: Started on: %s\n", Thread.currentThread().getName(), name, new Date());

        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: %s: Doing a task during %d seconds\n", Thread.currentThread().getName(), name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("%s: %s: Finished on: %s\n\n", Thread.currentThread().getName(), new Date(), name);
    }
}
