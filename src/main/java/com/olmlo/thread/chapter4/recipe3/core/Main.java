package com.olmlo.thread.chapter4.recipe3.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Main class of the example. Creates and execute ten FactorialCalculator tasks
 * in an executor controlling when they finish to write the results calculated
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        // Create a ThreadPoolExecutor with fixed size. It has a maximun of two threads
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        // List to store the Future objects that control the execution of the task and
        // are used to obtain the results
        List<Future<Integer>> resultList = new ArrayList<>();

        // Create a random number generator
        Random random = new Random();
        // Create and send to the executor the ten tasks
        for (int i = 0; i < 10; i++) {
            Integer number = new Integer(random.nextInt(10));
            FactorialCalculator calculator = new FactorialCalculator(number);
            Future<Integer> result = executor.submit(calculator);
            resultList.add(result);
        }

        // Wait for the finalization of the ten tasks
        do {
            System.out.printf("Main: Number of Completed Tasks: %d\n", executor.getCompletedTaskCount());
            for (int i = 0; i < resultList.size(); i++) {
                Future<Integer> result = resultList.get(i);
                System.out.printf("Main: Task %d: %s\n", i, result.isDone());
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (executor.getCompletedTaskCount() < resultList.size());

        // Write the results
        System.out.printf("Main: Results\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Integer> result = resultList.get(i);
            Integer number = null;
            try {
                number = result.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.printf("Core: Task %d: %d\n", i, number);
        }

        // Shutdown the executor
        executor.shutdown();

    }

}

class FactorialCalculator implements Callable<Integer> {

    /**
     * Number to calculate the factorial
     */
    private Integer number;

    /**
     * Constructor of the class. Initializes the attributes
     * @param number Number to calculate the factorial
     */
    public FactorialCalculator(Integer number) {
        this.number = number;
    }

    /**
     * Method called by the executor to execute this task and calculate the factorial of a
     * number
     */
    @Override
    public Integer call() throws Exception {
        int num, result;

        num = number.intValue();
        result = 1;

        // If the number is 0 or 1, return the 1 value
        if ((num == 0) || (num == 1)) {
            result = 1;
        } else {
            // Else, calculate the factorial
            for (int i = 2; i <= number; i++) {
                result *= i;
                Thread.sleep(20);
            }
        }
        System.out.printf("%s: %d\n", Thread.currentThread().getName(), result);
        // Return the value
        return result;
    }
}
