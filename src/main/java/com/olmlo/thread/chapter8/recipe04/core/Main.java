package com.olmlo.thread.chapter8.recipe04.core;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * class of the example. Creates a Phaser with three participants and Three task objects. Write information about the
 * evolution of the Phaser
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /*
         * Create a new Phaser for three participants
         */
        Phaser phaser = new Phaser(3);

        for (int i = 0; i < 3; i++) {
            Task task = new Task(i + 1, phaser);
            Thread thread = new Thread(task);
            thread.start();
        }

        /*
         * Write information about the Phaser
         */
        for (int i = 0; i < 10; i++) {
            System.out.printf("********************\n");
            System.out.printf("Main: Phaser Log\n");
            System.out.printf("Main: Phaser: Phase: %d\n", phaser.getPhase());
            System.out.printf("Main: Phaser: Registered Parties: %d\n", phaser.getRegisteredParties());
            System.out.printf("Main: Phaser: Arrived Parties: %d\n", phaser.getArrivedParties());
            System.out.printf("Main: Phaser: Unarrived Parties: %d\n", phaser.getUnarrivedParties());
            System.out.printf("********************\n");

            TimeUnit.SECONDS.sleep(1);
        }
    }
}

class Task implements Runnable {

    /**
     * Number of seconds this task is going to sleep the thread in each phase
     */
    private int time;

    /**
     * Phaser to synchronize the execution of phases
     */
    private Phaser phaser;

    /**
     * Constructor of the class. Initialize its attributes
     * 
     * @param time
     *            Number of seconds this task is going to sleep the thread in each phase
     * @param phaser
     *            Phaser to synchronize the execution of tasks
     */
    public Task(int time, Phaser phaser) {
        this.time = time;
        this.phaser = phaser;
    }

    /**
     * Main method of the task. Executes three phases. In each phase, sleeps the thread the number of seconds specified
     * by the time attribute.
     */
    @Override
    public void run() {
        /*
         * Arrive to the phaser
         */
        phaser.arrive();
        /*
         * Phase 1
         */
        System.out.printf("%s: Entering phase 1.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 1.\n", Thread.currentThread().getName());
        /*
         * End of Phase 1
         */
        phaser.arriveAndAwaitAdvance();
        /*
         * Phase 2
         */
        System.out.printf("%s: Entering phase 2.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 2.\n", Thread.currentThread().getName());
        /*
         * End of Phase 2
         */
        phaser.arriveAndAwaitAdvance();
        /*
         * Phase 3
         */
        System.out.printf("%s: Entering phase 3.\n", Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("%s: Finishing phase 3.\n", Thread.currentThread().getName());
        /*
         * End of Phase 3
         */
        phaser.arriveAndDeregister();
    }
}