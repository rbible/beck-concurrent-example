package com.olmlo.thread.chapter8.recipe02.core;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class of the example. Create five threads to execute the task and write info about the Lock shared by all the
 * threads
 */
public class Main {

    public static void main(String[] args) throws Exception {

        MyLock lock = new MyLock();

        Thread threads[] = new Thread[5];

        for (int i = 0; i < 5; i++) {
            Task task = new Task(lock);
            threads[i] = new Thread(task);
            threads[i].start();
        }

        /* Create a loop with 15 steps */
        for (int i = 0; i < 15; i++) {
            /* Write info about the lock */
            System.out.printf("Main: Logging the Lock\n");
            System.out.printf("************************\n");
            System.out.printf("Lock: Owner : %s\n", lock.getOwnerName());
            System.out.printf("Lock: Queued Threads: %s\n", lock.hasQueuedThreads());
            if (lock.hasQueuedThreads()) {
                System.out.printf("Lock: Queue Length: %d\n", lock.getQueueLength());
                System.out.printf("Lock: Queued Threads: ");
                Collection<Thread> lockedThreads = lock.getThreads();
                for (Thread lockedThread : lockedThreads) {
                    System.out.printf("%s ", lockedThread.getName());
                }
                System.out.printf("\n");
            }
            System.out.printf("Lock: Fairness: %s\n", lock.isFair());
            System.out.printf("Lock: Locked: %s\n", lock.isLocked());
            System.out.printf("************************\n");
            /* Sleep the thread for one second */
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

class MyLock extends ReentrantLock {

    /**
     * Declare the serial version uid of the class
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method returns the name of the thread that have the control of the Lock of the constant "None" if the Lock
     * is free
     * 
     * @return The name of the thread that has the control of the lock
     */
    public String getOwnerName() {
        if (this.getOwner() == null) {
            return "None";
        }
        return this.getOwner().getName();
    }

    /**
     * This method returns the list of the threads queued in the lock
     * 
     * @return The list of threads queued in the Lock
     */
    public Collection<Thread> getThreads() {
        return this.getQueuedThreads();
    }
}

class Task implements Runnable {

    /**
     * Lock shared by all the tasks
     */
    private Lock lock;

    /**
     * Constructor of the class
     * 
     * @param lock
     *            Lock shared by all the tasks
     */
    public Task(Lock lock) {
        this.lock = lock;
    }

    /**
     * Main method of the task. Takes the control of the Lock, sleeps for 500 milliseconds and free the lock. Repeats
     * this behavior five times
     */
    @Override
    public void run() {
        /* Loop with five steps */
        for (int i = 0; i < 5; i++) {
            /* Acquire the lock */
            lock.lock();
            System.out.printf("%s: Get the Lock.\n", Thread.currentThread().getName());
            /* Sleeps the thread 500 milliseconds */
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.printf("%s: Free the Lock.\n", Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                /* Free the lock */
                lock.unlock();
                
            }
        }
    }
}
