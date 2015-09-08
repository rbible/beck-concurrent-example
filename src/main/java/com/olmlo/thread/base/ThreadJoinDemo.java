package com.olmlo.thread.base;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the Example. Create and start two initialization tasks and wait for their finish
 */
public class ThreadJoinDemo {

    public static void main(String[] args) {

        DataSourcesLoader dsLoader = new DataSourcesLoader();
        Thread thread1 = new Thread(dsLoader, "DataSourceThread");
        thread1.start();

        NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
        Thread thread2 = new Thread(ncLoader, "NetworkConnectionLoader");
        thread2.start();

        // Wait for the finalization of the two threads
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: Configuration has been loaded: %s\n", new Date());
    }
}

class NetworkConnectionsLoader implements Runnable {

    /**
     * Main method of the class
     */
    @Override
    public void run() {
        System.out.printf("Begining network connections loading: %s\n", new Date());
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Network connections loading has finished: %s\n", new Date());
    }
}

class DataSourcesLoader implements Runnable {
    @Override
    public void run() {
        System.out.printf("Begining data sources loading: %s\n", new Date());
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Data sources loading has finished: %s\n", new Date());
    }
}
