package com.olmlo.thread.demo;

import java.util.concurrent.TimeUnit;

public class PrimeGeneratorDemo {

    public static void main(String[] args) {

        Thread task = new PrimeGenerator();
        task.start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task.interrupt();
    }

}

class PrimeGenerator extends Thread {

    @Override
    public void run() {
        long number = 1L;

        while (true) {
            if (isPrime(number)) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(" Interrupted ");

                }
                System.out.printf("Number %d is Prime\n", number);
            }

            if (isInterrupted()) {
                System.out.printf("The Prime Generator has been Interrupted\n");
                return;
            }
            number++;
        }
    }

    private boolean isPrime(long number) {
        if (number <= 2) {
            return true;
        }
        for (long i = 2; i < number; i++) {
            if ((number % i) == 0) {
                return false;
            }
        }
        return true;
    }
}
