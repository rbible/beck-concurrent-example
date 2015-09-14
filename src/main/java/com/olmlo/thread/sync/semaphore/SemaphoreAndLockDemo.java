package com.olmlo.thread.sync.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreAndLockDemo {

    /**
     * Main method of the class. Run ten jobs in parallel that
     * send documents to the print queue at the same time.
     */
    public static void main(String args[]) {
        SemaphoreAndLockPrintQueue printQueue = new SemaphoreAndLockPrintQueue();

        Thread thread[] = new Thread[12];
        for (int i = 0; i < 12; i++) {
            thread[i] = new Thread(new Job1(printQueue), "Thread " + i);
        }
        for (int i = 0; i < 12; i++) {
            thread[i].start();
        }

        System.out.println("Main Finish");
    }
}

class SemaphoreAndLockPrintQueue {
    private Semaphore semaphore;
    private boolean freePrinters[];
    private Lock lockPrinters;

    public SemaphoreAndLockPrintQueue() {
        semaphore = new Semaphore(3);
        freePrinters = new boolean[3];
        for (int i = 0; i < 3; i++) {
            freePrinters[i] = true;
        }
        lockPrinters = new ReentrantLock();
    }

    public void printJob(Object document) {
        try {
            // Get access to the semaphore. If there is one or more printers free,
            // it will get the access to one of the printers
            semaphore.acquire();
            int assignedPrinter = getPrinter();

            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: PrintQueue: Printing a Job in Printer" + " %d during %d seconds\n", Thread.currentThread()
                    .getName(), assignedPrinter, duration);
            TimeUnit.SECONDS.sleep(duration);

            freePrinters[assignedPrinter] = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    private int getPrinter() {
        int ret = -1;
        try {
            lockPrinters.lock();
            for (int i = 0; i < freePrinters.length; i++) {
                if (freePrinters[i]) {
                    ret = i;
                    freePrinters[i] = false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lockPrinters.unlock();
        }
        return ret;
    }

}

class Job1 implements Runnable {
    private SemaphoreAndLockPrintQueue printQueue;

    public Job1(SemaphoreAndLockPrintQueue printQueue) {
        this.printQueue = printQueue;
    }

    @Override
    public void run() {
        System.out.printf("%s: Going to print a job \n\n", Thread.currentThread().getName());
        printQueue.printJob(new Object());
        System.out.printf("%s: The document has been printed \n\n", Thread.currentThread().getName());
    }
}
