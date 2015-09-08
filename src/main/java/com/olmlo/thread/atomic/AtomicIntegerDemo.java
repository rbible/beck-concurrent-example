package com.olmlo.thread.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {
    public static void main(String[] args) throws Exception {
        /* Create a ParkingCounter object */
        ParkingCounter counter = new ParkingCounter(5);

        /* Create and launch two sensors */
        Thread thread1 = new Thread(new Sensor1(counter));
        Thread thread2 = new Thread(new Sensor2(counter));

        thread1.start();
        thread2.start();

        /* Wait for the finalization of the threads */
        thread1.join();
        thread2.join();

        /* Write in the console the number of cars in the parking */
        System.out.printf("Main: Number of cars: %d\n", counter.get());

        /* Writ a message indicating the end of the program */
        System.out.printf("Main: End of the program.\n");
    }
}

class ParkingCounter extends AtomicInteger {

    private static final long serialVersionUID = 1L;

    /**
     * Max number accepted by this counter
     */
    private int maxNumber;

    /**
     * Constructor of the class
     * 
     * @param maxNumber
     *            Max number accepter by this counter
     */
    public ParkingCounter(int maxNumber) {
        set(0);
        this.maxNumber = maxNumber;
    }

    /**
     * Method that increments the internal counter if it has a value less than the maximum. Is implemented to be and
     * atomic operation
     * 
     * @return True if the car can enter in the parking, false if not.
     */
    public boolean carIn() {
        for (;;) {
            int value = get();
            if (value == maxNumber) {
                System.out.printf("ParkingCounter: The parking is full.\n");
                return false;
            }
            int newValue = value + 1;
            boolean changed = compareAndSet(value, newValue);
            if (changed) {
                System.out.printf("ParkingCounter: A car has entered.\n");
                return true;
            }
        }
    }

    /**
     * Method that decrements the internal counter if it has a value bigger than 0. Is implemented to be and atomic
     * operation
     * 
     * @return True if the car leave the parking, false if there are 0 cars in the parking
     */
    public boolean carOut() {
        for (;;) {
            int value = get();
            if (value == 0) {
                System.out.printf("ParkingCounter: The parking is empty.\n");
                return false;
            }
            int newValue = value - 1;
            boolean changed = compareAndSet(value, newValue);
            if (changed) {
                System.out.printf("ParkingCounter: A car has gone out.\n");
                return true;
            }
        }
    }

}

class Sensor1 implements Runnable {

    /**
     * Counter of cars in the parking
     */
    private ParkingCounter counter;

    /**
     * Constructor of the class. It initializes its attributes
     * 
     * @param counter
     *            Counter of cars in the parking
     */
    public Sensor1(ParkingCounter counter) {
        this.counter = counter;
    }

    /**
     * Main method of the sensor. Simulates the traffic in the door of the parking. total: 4+3 car in,3 car out
     */
    @Override
    public void run() {
        counter.carIn();
        counter.carIn();
        counter.carIn();
        counter.carIn();

        counter.carOut();
        counter.carOut();
        counter.carOut();

        counter.carIn();
        counter.carIn();
        counter.carIn();
    }
}

class Sensor2 implements Runnable {

    /**
     * Counter of cars in the parking
     */
    private ParkingCounter counter;

    /**
     * Constructor of the class. It initializes its attributes
     * 
     * @param counter
     *            Counter of cars in the parking
     */
    public Sensor2(ParkingCounter counter) {
        this.counter = counter;
    }

    /**
     * Main method of the sensor. Simulates the traffic in the door of the parking. total:1+6 car in,2 car out
     */
    @Override
    public void run() {
        counter.carIn();

        counter.carOut();
        counter.carOut();

        counter.carIn();
        counter.carIn();
        counter.carIn();
        counter.carIn();
        counter.carIn();
        counter.carIn();
    }
}
