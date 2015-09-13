package com.olmlo.thread.sync.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Create ten threads to execute ten task objects and write information about the semaphore
 *
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws Exception {

        Semaphore semaphore = new Semaphore(3);

        Thread threads[] = new Thread[10];

        /* Create and launch a thread every 200 milliseconds. After each creation, show information
         * about the semaphore */
        for (int i = 0; i < threads.length; i++) {
            SemaphoreTask task = new SemaphoreTask(semaphore);
            threads[i] = new Thread(task);
            threads[i].start();

            TimeUnit.MILLISECONDS.sleep(200);

            showLog(semaphore);
        }

        /* Write information about the semaphore five times */
        for (int i = 0; i < 5; i++) {
            showLog(semaphore);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    /**
     * Method that writes information about a semaphore
     * 
     * @param semaphore
     *            Semaphore to write its log information
     */
    private static void showLog(Semaphore semaphore) {
        System.out.printf("********************\n");
        System.out.printf("Main: Semaphore Log\n");
        System.out.printf("Main: Semaphore: Avalaible Permits: %d\n", semaphore.availablePermits());
        System.out.printf("Main: Semaphore: Queued Threads: %s\n", semaphore.hasQueuedThreads());
        System.out.printf("Main: Semaphore: Queue Length: %d\n", semaphore.getQueueLength());
        System.out.printf("Main: Semaphore: Fairness: %s\n", semaphore.isFair());
        System.out.printf("********************\n");
    }

}

class SemaphoreTask implements Runnable {

    private Semaphore semaphore;

    public SemaphoreTask(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.printf("%s: Get the semaphore.\n", Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(2);
            System.out.println(Thread.currentThread().getName() + ": Release the semaphore.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            semaphore.release();
        }
    }
}
