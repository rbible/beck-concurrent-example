package com.olmlo.thread.safe;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example.
 *
 */
public class SafeDemo {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {
        // Creates a task
        SafeTask task = new SafeTask();

        // Creates and start three Thread objects for that Task
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(task);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread.start();
        }
    }
}

class SafeTask implements Runnable {

    /**
     * ThreadLocal shared between the Thread objects
     */
    private static ThreadLocal<Date> startDate = new ThreadLocal<Date>() {
        protected Date initialValue() {
            return new Date();
        }
    };

    /**
     * Main method of the class
     */
    @Override
    public void run() {
        // Writes the start date
        System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate.get());
        try {
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Writes the start date
        System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate.get());
    }

}
