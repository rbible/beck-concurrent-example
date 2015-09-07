package com.olmlo.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class of the example.
 *
 */
public class ReentrantLockDemo2 {

    /**
     * Main method of the class. Run ten jobs in parallel that
     * send documents to the print queue at the same time.
     */
    public static void main(String args[]) {

        // Creates the print queue
        PrintQueue printQueue = new PrintQueue();

        // Creates ten Threads
        Thread thread[] = new Thread[10];
        for (int i = 0; i < 10; i++) {
            thread[i] = new Thread(new Job(printQueue), "Thread " + i);
        }

        // Starts the Threads
        for (int i = 0; i < 10; i++) {
            thread[i].start();
        }
    }

}

class Job implements Runnable {

    /**
     * Queue to print the documents
     */
    private PrintQueue printQueue;

    /**
     * Constructor of the class. Initializes the queue
     * @param printQueue
     */
    public Job(PrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    /**
     * Core method of the Job. Sends the document to the print queue and waits
     *  for its finalization
     */
    @Override
    public void run() {
        System.out.printf("%s: Going to print a document\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: The document has been printed\n", Thread.currentThread().getName());
    }
}

class PrintQueue {

    /**
     * Lock to control the access to the queue.
     */
    private final Lock queueLock = new ReentrantLock();

    /**
     * Method that prints a document
     * @param document document to print
     */
    public void printJob(Object document) {
        queueLock.lock();

        try {
            Long duration = (long) (Math.random() * 10000);
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
