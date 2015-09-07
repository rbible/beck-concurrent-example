package com.olmlo.thread.chapter6.recipe07.core;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Main class of the example. It creates three task and execute them
 *
 */
public class ThreadLocalRandomDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {

        /* Create an array to store the threads */
        Thread threads[] = new Thread[3];

        /* Launch three tasks */
        for (int i = 0; i < threads.length; i++) {
            TaskLocalRandom task = new TaskLocalRandom();
            threads[i] = new Thread(task);
            threads[i].start();
        }
    }
}

class TaskLocalRandom implements Runnable {

    /**
     * Constructor of the class. Initializes the randoom number generator
     * for this task
     */
    public TaskLocalRandom() {
        ThreadLocalRandom.current();
    }

    /**
     * Main method of the class. Generate 10 random numbers and write them
     * in the console
     */
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s: %d\n", name, ThreadLocalRandom.current().nextInt(10));
        }
    }
}
