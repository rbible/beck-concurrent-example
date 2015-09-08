package com.olmlo.thread.chapter4.recipe6.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Launch three tasks using the invokeAll() method
 * and then prints their results to the console
 *
 */
public class Main {

    public static void main(String[] args) {

        // Create an executor
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();

        // Create three tasks and stores them in a List
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Task task = new Task("Task-" + i);
            taskList.add(task);
        }

        // Call the invokeAll() method
        List<Future<Result>> resultList = null;
        try {
            resultList = executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Finish the executor
        executor.shutdown();

        // Writes the results to the console
        System.out.printf("Core: Printing the results\n");
        for (int i = 0; i < resultList.size(); i++) {
            Future<Result> future = resultList.get(i);
            try {
                Result result = future.get();
                System.out.printf("%s: %s\n", result.getName(), result.getValue());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

}

class Result {
    /**
     * The name of the task that generates the result
     */
    private String name;
    /**
     * The value of the task that generates the result 
     */
    private int value;

    /**
     * Returns the name of the task
     * @return Name of the task that generates the result
     */
    public String getName() {
        return name;
    }

    /**
     * Establish the name of the task
     * @param name The name of the task that generates the result
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the value of the result
     * @return The value of the result
     */
    public int getValue() {
        return value;
    }

    /**
     * Establish the value of the result
     * @param value The value of the result
     */
    public void setValue(int value) {
        this.value = value;
    }

}

class Task implements Callable<Result> {

    /**
     * The name of the Task
     */
    private String name;

    /**
     * Constructor of the class
     * @param name Initializes the name of the task
     */
    public Task(String name) {
        this.name = name;
    }

    /**
     * Main method of the task. Waits during a random period of time and then
     * calculates the sum of five random numbers
     */
    @Override
    public Result call() throws Exception {
        // Writes a message to the console
        System.out.printf("%s: Staring\n", this.name);

        // Waits during a random period of time
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Calculates the sum of five random numbers
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value += (int) (Math.random() * 100);

        }

        // Creates the object with the results
        Result result = new Result();
        result.setName(this.name);
        result.setValue(value);
        System.out.printf("%s: Ends\n", this.name);

        // Returns the result object
        return result;
    }
}
