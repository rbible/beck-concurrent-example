package com.olmlo.thread.app;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Main class of the example
 */
public class ProductConsumerDemo2 {

    /**
     * Main method of the example
     */
    public static void main(String[] args) {

        // Creates an event storage
        EventStorage storage = new EventStorage();

        // Creates a Producer and a Thread to run it
        Producer2 producer = new Producer2(storage);
        Thread thread1 = new Thread(producer);

        // Creates a Consumer and a Thread to run it
        Consumer2 consumer = new Consumer2(storage);
        Thread thread2 = new Thread(consumer);

        // Starts the thread
        thread2.start();
        thread1.start();
    }

}

class Consumer2 implements Runnable {

    /**
     * Store to work with
     */
    private EventStorage storage;

    /**
     * Constructor of the class. Initialize the storage
     * @param storage The store to work with
     */
    public Consumer2(EventStorage storage) {
        this.storage = storage;
    }

    /**
     * Core method for the consumer. Consume 100 events
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.get();
        }
    }

}

class EventStorage {

    /**
     * Maximum size of the storage
     */
    private int maxSize;
    /**
     * Storage of events
     */
    private List<Date> storage;

    /**
     * Constructor of the class. Initializes the attributes.
     */
    public EventStorage() {
        maxSize = 10;
        storage = new LinkedList<>();
    }

    /**
     * This method creates and storage an event.
     */
    public synchronized void set() {
        while (storage.size() == maxSize) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        storage.add(new Date());
        System.out.printf("Set: %d \n", storage.size());
        notify();
    }

    /**
     * This method delete the first event of the storage.
     */
    public synchronized void get() {
        while (storage.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.printf("Get: %d: %s \n", storage.size(), ((LinkedList<?>) storage).poll());
        notify();
    }

}

class Producer2 implements Runnable {

    /**
     * Store to work with
     */
    private EventStorage storage;

    /**
     * Constructor of the class. Initialize the storage.
     * @param storage The store to work with
     */
    public Producer2(EventStorage storage) {
        this.storage = storage;
    }

    /**
     * Core method of the producer. Generates 100 events.
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            storage.set();
        }
    }
}
