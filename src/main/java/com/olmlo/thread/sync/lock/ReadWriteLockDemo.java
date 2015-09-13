package com.olmlo.thread.sync.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main class of the example
 *
 */
public class ReadWriteLockDemo {

    public static void main(String[] args) {

        // Creates an object to store the prices
        PricesInfo pricesInfo = new PricesInfo();

        Reader readers[] = new Reader[5];
        Thread threadsReader[] = new Thread[5];

        // Creates five readers and threads to run them
        for (int i = 0; i < 5; i++) {
            readers[i] = new Reader(pricesInfo);
            threadsReader[i] = new Thread(readers[i]);
        }

        // Creates a writer and a thread to run it
        Writer writer = new Writer(pricesInfo);
        Thread threadWriter = new Thread(writer);

        // Starts the threads
        for (int i = 0; i < 5; i++) {
            threadsReader[i].start();
        }
        threadWriter.start();
    }
}

class PricesInfo {

    /**
     * The two prices
     */
    private double price1;
    private double price2;

    /**
     * Lock to control the access to the prices
     */
    private ReadWriteLock lock;

    /**
     * Constructor of the class. Initializes the prices and the Lock
     */
    public PricesInfo() {
        price1 = 1.0;
        price2 = 2.0;
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Returns the first price
     * @return the first price
     */
    public double getPrice1() {
        lock.readLock().lock();
        double value = price1;
        lock.readLock().unlock();
        return value;
    }

    /**
     * Returns the second price
     * @return the second price
     */
    public double getPrice2() {
        lock.readLock().lock();
        double value = price2;
        lock.readLock().unlock();
        return value;
    }

    /**
     * Establish the prices
     * @param price1 The price of the first product
     * @param price2 The price of the second product
     */
    public void setPrices(double price1, double price2) {
        lock.writeLock().lock();
        this.price1 = price1;
        this.price2 = price2;
        lock.writeLock().unlock();
    }
}

class Reader implements Runnable {

    /**
     * Class that stores the prices
     */
    private PricesInfo pricesInfo;

    /**
     * Constructor of the class
     * @param pricesInfo object that stores the prices
     */
    public Reader(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    /**
     * Core method of the reader. Consults the two prices and prints them
     * to the console
     */
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s: Price 1: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice1());
            System.out.printf("%s: Price 2: %f\n", Thread.currentThread().getName(), pricesInfo.getPrice2());
        }
    }

}

class Writer implements Runnable {

    /**
     * Class that stores the prices
     */
    private PricesInfo pricesInfo;

    /**
     * Constructor of the class
     * @param pricesInfo object that stores the prices
     */
    public Writer(PricesInfo pricesInfo) {
        this.pricesInfo = pricesInfo;
    }

    /**
     * Core method of the writer. Establish the prices
     */
    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.printf("Writer: Attempt to modify the prices.\n");
            pricesInfo.setPrices(Math.random() * 10, Math.random() * 8);
            System.out.printf("Writer: Prices have been modified.\n");
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
