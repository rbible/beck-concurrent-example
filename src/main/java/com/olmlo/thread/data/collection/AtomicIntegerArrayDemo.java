package com.olmlo.thread.data.collection;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Main class of the example. Execute 100 incrementers and 100 decrementers
 * and checks that the results are the expected
 *
 */
public class AtomicIntegerArrayDemo {

    public static void main(String[] args) {

        final int THREADS = 100;
        /**
         * Atomic array whose elements will be incremented and decremented
         */
        AtomicIntegerArray vector = new AtomicIntegerArray(1000);
        /*
         * An incrementer task
         */
        Incrementer incrementer = new Incrementer(vector);
        /*
         * A decrementer task
         */
        Decrementer decrementer = new Decrementer(vector);

        /*
         * Create and execute 100 incrementer and 100 decrementer tasks
         */
        Thread threadIncrementer[] = new Thread[THREADS];
        Thread threadDecrementer[] = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threadIncrementer[i] = new Thread(incrementer);
            threadDecrementer[i] = new Thread(decrementer);

            threadIncrementer[i].start();
            threadDecrementer[i].start();
        }

        /*
         * Wait for the finalization of all the tasks
         */
        for (int i = 0; i < THREADS; i++) {
            try {
                threadIncrementer[i].join();
                threadDecrementer[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         * Write the elements different from 0
         */
        for (int i = 0; i < vector.length(); i++) {
            if (vector.get(i) != 0) {
                System.out.println("Vector[" + i + "] : " + vector.get(i));
            }
        }

        System.out.println("Main: End of the example");
    }

}

class Decrementer implements Runnable {

    /**
     * The array to decrement the elements
     */
    private AtomicIntegerArray vector;

    /**
     * Constructor of the class
     * @param vector The array to decrement is elements
     */
    public Decrementer(AtomicIntegerArray vector) {
        this.vector = vector;
    }

    /**
     * Main method of the class. It decrements all the elements of the 
     * array
     */
    @Override
    public void run() {
        for (int i = 0; i < vector.length(); i++) {
            vector.getAndDecrement(i);
        }
    }

}

class Incrementer implements Runnable {

    /**
     * Array that store the elements to increment
     */
    private AtomicIntegerArray vector;

    /**
     * Constructor of the class
     * @param vector Array to store the elements to increment
     */
    public Incrementer(AtomicIntegerArray vector) {
        this.vector = vector;
    }

    /**
     * Main method of the task. Increment all the elements of the
     * array
     */
    @Override
    public void run() {

        for (int i = 0; i < vector.length(); i++) {
            vector.getAndIncrement(i);
        }

    }

}
