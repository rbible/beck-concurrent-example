package com.olmlo.thread.pool.forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the program. 
 */
public class ForkJoinPoolCancelDemo {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {

        // Generate an array of 1000 integers
        ArrayGenerator generator = new ArrayGenerator();
        int array[] = generator.generateArray(1000);

        // Create a TaskManager object
        TaskManager manager = new TaskManager();

        // Create a ForkJoinPool with the default constructor
        ForkJoinPool pool = new ForkJoinPool();

        // Create a Task to process the array
        SearchNumberTask task = new SearchNumberTask(array, 0, 1000, 5, manager);

        // Execute the task
        pool.execute(task);

        // Shutdown the pool
        pool.shutdown();

        // Wait for the finalization of the task
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write a message to indicate the end of the program
        System.out.printf("Main: The program has finished\n");
    }

}

class ArrayGenerator {

    /**
     * Method that generates an array of integer numbers between 0 and 10
     * with the specified size
     * @param size The size of the array
     * @return An array of random integer numbers between 0 and 10
     */
    public int[] generateArray(int size) {
        int array[] = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10);
        }
        return array;
    }

}

class SearchNumberTask extends RecursiveTask<Integer> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Valued returned when the number has not been found by the task
     */
    private final static int NOT_FOUND = -1;

    /**
     * Array of numbers
     */
    private int numbers[];

    /**
     * Start and end positions of the block of numbers
     * this task has to process
     */
    private int start, end;

    /**
     * Number this task is going to look for
     */
    private int number;

    /**
     * Object that allows the cancellation of all the tasks
     */
    private TaskManager manager;

    /**
     * Constructor of the class
     * @param array Array of numbers
     * @param start Start position of the block of numbers this task has to process 
     * @param end End position of the block of numbers this task has to process
     * @param number Number this task is going to look for
     * @param manager 
     */
    public SearchNumberTask(int numbers[], int start, int end, int number, TaskManager manager) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
        this.number = number;
        this.manager = manager;
    }

    /**
     * If the block of number this task has to process has more than
     * ten elements, divide that block in two parts and create two
     * new Tasks using the launchTasks() method.
     * Else, looks for the number in the block assigned to it using
     * the lookForNumber() method
     */
    @Override
    protected Integer compute() {
        System.out.println("Task: " + start + ":" + end);
        int ret;
        if (end - start > 10) {
            ret = launchTasks();
        } else {
            ret = lookForNumber();
        }
        return new Integer(ret);
    }

    /**
     * Looks for the number in the block of numbers assigned to this task
     * @return The position where it found the number or -1 if it doesn't find it
     */
    private int lookForNumber() {
        for (int i = start; i < end; i++) {
            if (numbers[i] == number) {
                System.out.printf("Task: Number %d found in position %d\n", number, i);
                manager.cancelTasks(this);
                return i;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return NOT_FOUND;
    }

    /**
     * Divide the block of numbers assigned to this task in two and 
     * execute to new Task objects to process that blocks 
     * @return The position where the number has been found of -1
     * if the number haven't been found in the subtasks
     */
    private int launchTasks() {
        int mid = (start + end) / 2;

        SearchNumberTask task1 = new SearchNumberTask(numbers, start, mid, number, manager);
        SearchNumberTask task2 = new SearchNumberTask(numbers, mid, end, number, manager);

        manager.addTask(task1);
        manager.addTask(task2);

        task1.fork();
        task2.fork();
        int returnValue;

        returnValue = task1.join();
        if (returnValue != -1) {
            return returnValue;
        }

        returnValue = task2.join();
        return returnValue;
    }

    public void writeCancelMessage() {
        System.out.printf("Task: Cancelled task from %d to %d\n", start, end);
    }

}

class TaskManager {

    /**
     * List of tasks
     */
    private List<ForkJoinTask<Integer>> tasks;

    /**
     * Constructor of the class. Initializes the list of tasks
     */
    public TaskManager() {
        tasks = new ArrayList<>();
    }

    /**
     * Method to add a new Task in the list
     * @param task The new task
     */
    public void addTask(ForkJoinTask<Integer> task) {
        tasks.add(task);
    }

    /**
     * Method that cancel all the tasks in the list
     * @param cancelTask 
     */
    public void cancelTasks(ForkJoinTask<Integer> cancelTask) {
        for (ForkJoinTask<Integer> task : tasks) {
            if (task != cancelTask) {
                task.cancel(true);
                ((SearchNumberTask) task).writeCancelMessage();
            }
        }
    }
}
