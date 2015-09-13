package com.olmlo.thread.base;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the Example. Creates a FileClock runnable object
 * and a Thread to run it. Starts the Thread, waits five seconds
 * and interrupts it. 
 *
 */
public class InterruptsDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Creates a FileClock runnable object and a Thread
        // to run it
        FileClock clock = new FileClock();
        Thread thread = new Thread(clock);

        // Starts the Thread
        thread.start();
        try {
            // Waits five seconds
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrupts the Thread
        thread.interrupt();
    }
}

class FileClock implements Runnable {
    /**
     * Main method of the class
     */
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            try {
                // Sleep during one second
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.printf("The FileClock has been interrupted");
//                throw new RuntimeException(e);
            }
        }
    }
}
