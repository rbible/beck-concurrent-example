package com.olmlo.thread.sync.cyclicbarrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo2 {

    public static void main(String[] args) {

        /*
         * Initializes the bi-dimensional array of data 10000 rows 1000 numbers in each row Looking for number 5
         */
        final int ROWS = 10000;
        final int NUMBERS = 1000;
        final int SEARCH = 5;
        final int PARTICIPANTS = 5;
        final int LINES_PARTICIPANT = 2000;

        MatrixMock matrixMock = new MatrixMock(ROWS, NUMBERS, SEARCH);

        // Initializes the object for the results
        Results results = new Results(ROWS);

        // Creates an Grouper object
        Grouper grouper = new Grouper(results);

        // Creates the CyclicBarrier object. It has 5 participants and, when
        // they finish, the CyclicBarrier will execute the grouper object
        CyclicBarrier cyclicBarrier = new CyclicBarrier(PARTICIPANTS, grouper);

        // Creates, initializes and starts 5 Searcher objects
        Searcher searchers[] = new Searcher[PARTICIPANTS];

        for (int i = 0; i < PARTICIPANTS; i++) {
            searchers[i] = new Searcher(i * LINES_PARTICIPANT, (i * LINES_PARTICIPANT) + LINES_PARTICIPANT, matrixMock, results, 5,
                    cyclicBarrier);
            Thread thread = new Thread(searchers[i]);
            thread.start();
        }
        System.out.printf("Main: The main thread has finished.\n");
    }

}

class Grouper implements Runnable {

    /**
     * Results object with the occurrences of the number in each row
     */
    private Results results;

    /**
     * Constructor of the class. Initializes its attributes
     * @param results Results object with the ocurrences of the number in each row
     */
    public Grouper(Results results) {
        this.results = results;
    }

    /**
     * Main method of the Grouper. Sum the values stored in the Results object 
     */
    @Override
    public void run() {
        int finalResult = 0;
        System.out.printf("Grouper: Processing results...\n");
        int data[] = results.getData();
        for (int number : data) {
            finalResult += number;
        }
        System.out.printf("Grouper: Total result: %d.\n", finalResult);
    }
}

class MatrixMock {

    /**
     * Bi-dimensional array with the random numbers
     */
    private int data[][];

    /**
     * Constructor of the class. Generates the bi-dimensional array of numbers.
     * While generates the array, it counts the times that appears the number we are going
     * to look for so we can check that the CiclycBarrier class does a good job
     * @param size Number of rows of the array
     * @param length Number of columns of the array
     * @param number Number we are going to look for
     */
    public MatrixMock(int size, int length, int number) {

        int counter = 0;
        data = new int[size][length];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < length; j++) {
                data[i][j] = random.nextInt(10);
                if (data[i][j] == number) {
                    counter++;
                }
            }
        }
        System.out.printf("Mock: There are %d ocurrences of number in generated data.\n", counter, number);
    }

    /**
     * This methods returns a row of the bi-dimensional array
     * @param row the number of the row to return
     * @return the selected row
     */
    public int[] getRow(int row) {
        if ((row >= 0) && (row < data.length)) {
            return data[row];
        }
        return null;
    }

}

class Searcher implements Runnable {

    /**
     * First row where look for
     */
    private int firstRow;

    /**
     * Last row where look for
     */
    private int lastRow;

    /**
     * Bi-dimensional array with the numbers
     */
    private MatrixMock mock;

    /**
     * Array to store the results
     */
    private Results results;

    /**
     * Number to look for
     */
    private int number;

    /**
     * CyclicBarrier to control the execution
     */
    private final CyclicBarrier barrier;

    /**
     * Constructor of the class. Initializes its attributes
     * @param firstRow First row where look for
     * @param lastRow Last row where fook for
     * @param mock Object with the array of numbers
     * @param results Array to store the results
     * @param number Number to look for
     * @param barrier CyclicBarrier to control the execution
     */
    public Searcher(int firstRow, int lastRow, MatrixMock mock, Results results, int number, CyclicBarrier barrier) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.mock = mock;
        this.results = results;
        this.number = number;
        this.barrier = barrier;
    }

    /**
     * Main method of the searcher. Look for the number in a subset of rows. For each row, saves the
     * number of occurrences of the number in the array of results
     */
    @Override
    public void run() {
        int counter;
        System.out.printf("%s: Processing lines from %d to %d.\n", Thread.currentThread().getName(), firstRow, lastRow);
        for (int i = firstRow; i < lastRow; i++) {
            int row[] = mock.getRow(i);
            counter = 0;
            for (int j = 0; j < row.length; j++) {
                if (row[j] == number) {
                    counter++;
                }
            }
            results.setData(i, counter);
        }
        System.out.printf("%s: Lines processed.\n", Thread.currentThread().getName());

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class Results {

    /**
     * Array to store the number of occurrences of the number in each row of the array
     */
    private int data[];

    /**
     * Constructor of the class. Initializes its attributes
     * @param size Size of the array to store the results
     */
    public Results(int size) {
        data = new int[size];
    }

    /**
     * Sets the value of one position in the array of results
     * @param position Position in the array
     * @param value Value to set in that position
     */
    public void setData(int position, int value) {
        data[position] = value;
    }

    /**
     * Returns the array of results
     * @return the array of results
     */
    public int[] getData() {
        return data;
    }
}
