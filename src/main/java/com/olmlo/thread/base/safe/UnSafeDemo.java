package com.olmlo.thread.base.safe;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the UnsafeTask. Creates a Runnable task and
 * three Thread objects that run it.
 *
 */
public class UnSafeDemo {

    /**
     * Main method of the UnsafeTaks. Creates a Runnable task and
     * three Thread objects that run it.
     * @param args
     */
    public static void main(String[] args) {
        // Creates the unsafe task
        UnsafeTask task = new UnsafeTask();

        // Throw three Thread objects
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(task);
            thread.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class UnsafeTask implements Runnable {

    /**
     * Date shared by all threads
     */
    private Date startDate;

    /**
     * Main method of the class. Saves the start date and writes
     * it to the console when it starts and when it ends
     */
    @Override
    public void run() {
        startDate = new Date();
        System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate);
        try {
            TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate);
    }

}