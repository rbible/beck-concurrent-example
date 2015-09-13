package com.olmlo.thread.sync.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class of the example
 *
 */
public class LockRepeatDemo {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String args[]) {
        // Creates the print queue
        LockRepeatPrintQueue printQueue = new LockRepeatPrintQueue();

        // Creates ten jobs and the Threads to run them
        Thread thread[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new LockRepeatJob(printQueue), "Thread " + i);
        }

        // Launch a thread ever 0.1 seconds
        for (int i = 0; i < 10; i++) {
            thread[i].start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

class LockRepeatPrintQueue {

    /**
     * Creates a lock to control the access to the queue.
     * With the boolean attribute, we control the fairness of
     * the Lock
     */
    private final Lock queueLock = new ReentrantLock(false);

    /**
     * Method that prints the Job. The printing is divided in two phase two
     * show how the fairness attribute affects the election of the thread who
     * has the control of the lock
     * @param document The document to print
     */
    public void printJob(Object document) {
        queueLock.lock();

        try {
            Long duration = (long) (Math.random() * 3000);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(),
                    (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }

        queueLock.lock();
        try {
            Long duration = (long) (Math.random() * 3000);
            System.out.printf("%s: PrintQueue: Printing a Job during %d seconds\n", Thread.currentThread().getName(),
                    (duration / 1000));
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }
}

class LockRepeatJob implements Runnable {

    /**
     * The queue to send the documents
     */
    private LockRepeatPrintQueue printQueue;

    /**
     * Constructor of the class. Initializes the print queue
     * @param printQueue the print queue to send the documents
     */
    public LockRepeatJob(LockRepeatPrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    /**
     * Core method of the Job. Sends the document to the queue
     */
    @Override
    public void run() {
        System.out.printf("%s: Going to print a job\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
    }

}
