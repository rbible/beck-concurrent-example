package com.olmlo.thread.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main class of the example
 *
 */
public class AtomicIntegerDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /* Create a ParkingCounter object */
        ParkingCounter counter = new ParkingCounter(5);

        /* Create and launch two sensors */
        Sensor1 sensor1 = new Sensor1(counter);
        Sensor2 sensor2 = new Sensor2(counter);

        Thread thread1 = new Thread(sensor1);
        Thread thread2 = new Thread(sensor2);

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

    /**
     * Serial Version UID of the class 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Max number accepted by this counter
     */
    private int maxNumber;

    /**
     * Constructor of the class
     * @param maxNumber Max number accepter by this counter
     */
    public ParkingCounter(int maxNumber) {
        set(0);
        this.maxNumber = maxNumber;
    }

    /**
     * Method that increments the internal counter if it has
     * a value less than the maximum. Is implemented to be and
     * atomic operation
     * @return True if the car can enter in the parking, false if not.
     */
    public boolean carIn() {
        for (;;) {
            int value = get();
            if (value == maxNumber) {
                System.out.printf("ParkingCounter: The parking is full.\n");
                return false;
            } else {
                int newValue = value + 1;
                boolean changed = compareAndSet(value, newValue);
                if (changed) {
                    System.out.printf("ParkingCounter: A car has entered.\n");
                    return true;
                }
            }
        }
    }

    /**
     * Method that decrements the internal counter if it has
     * a value bigger than 0. Is implemented to be and
     * atomic operation
     * @return True if the car leave the parking, false if there are 0 cars 
     * in the parking
     */
    public boolean carOut() {
        for (;;) {
            int value = get();
            if (value == 0) {
                System.out.printf("ParkingCounter: The parking is empty.\n");
                return false;
            } else {
                int newValue = value - 1;
                boolean changed = compareAndSet(value, newValue);
                if (changed) {
                    System.out.printf("ParkingCounter: A car has gone out.\n");
                    return true;
                }
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
     * @param counter Counter of cars in the parking
     */
    public Sensor1(ParkingCounter counter) {
        this.counter = counter;
    }

    /**
     * Main method of the sensor. Simulates the traffic in the door of the parking
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
     * @param counter Counter of cars in the parking
     */
    public Sensor2(ParkingCounter counter) {
        this.counter = counter;
    }

    /**
     * Main method of the sensor. Simulates the traffic in the door of the parking
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
