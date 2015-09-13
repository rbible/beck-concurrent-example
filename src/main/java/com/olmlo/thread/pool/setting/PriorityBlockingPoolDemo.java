package com.olmlo.thread.pool.setting;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main method of the class. It creates an Executor with a PriorityQueue as working queue and then
 * sends various tasks with different priorities to check that they are executed in the correct order
 *
 */
public class PriorityBlockingPoolDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        /* Create an executor with a PriorityBlockingQueue as the structure to store the tasks */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());

        /* Send four task to the executor */
        for (int i = 0; i < 4; i++) {
            MyPriorityTask task = new MyPriorityTask("Task " + i, i);
            executor.execute(task);
        }

        /* sleep the thread during one second */
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Send four tasks to the executor */
        for (int i = 4; i < 8; i++) {
            MyPriorityTask task = new MyPriorityTask("Task " + i, i);
            executor.execute(task);
        }

        /* Shutdown the executor */
        executor.shutdown();

        /* Wait for the finalization of the executor */
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Write a message to the console indicating the end of the program */
        System.out.printf("Main: End of the program.\n");
    }

}

class MyPriorityTask implements Runnable, Comparable<MyPriorityTask> {

    /**
     * This attribute stores the priority of the task
     */
    private int priority;

    /**
     * This attribute stores the name of the task
     */
    private String name;

    /**
     * Constructor of the task. It initialize its attributes
     * @param name Name of the task
     * @param priority Priority of the task
     */
    public MyPriorityTask(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    /**
     * Method that returns the priority of the task
     * @return the priority of the task
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Method that compares the priorities of two tasks. The task with higher priority value will
     * be stored before in the list and it will be executed before
     */
    @Override
    public int compareTo(MyPriorityTask o) {
        if (this.getPriority() < o.getPriority()) {
            return 1;
        }

        if (this.getPriority() > o.getPriority()) {
            return -1;
        }

        return 0;
    }

    /**
     * Main method of the task. It only writes a message to the console. It will be overridden by the real tasks
     */
    @Override
    public void run() {
        System.out.printf("MyPriorityTask: %s Priority : %d\n", name, priority);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
