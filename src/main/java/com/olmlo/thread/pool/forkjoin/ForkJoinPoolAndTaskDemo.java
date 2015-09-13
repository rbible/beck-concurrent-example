package com.olmlo.thread.pool.forkjoin;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * Main class of the example. It creates a ForkJoinPool and a 
 * Task and executes the task in the pool
 */
public class ForkJoinPoolAndTaskDemo {

    public static void main(String[] args) throws Exception {

        /* Create an array of 10000 elements */
        int array[] = new int[10000];

        /* ForkJoinPool to execute the task */
        ForkJoinPool pool = new ForkJoinPool();

        /* Task to increment the elements of the array */
        Task task = new Task("Task", array, 0, array.length);

        /* Send the task to the pool */
        pool.invoke(task);

        /* Shutdown the pool */
        pool.shutdown();

        /* Write a message in the console */
        System.out.printf("Main: End of the program.\n");

    }

}

abstract class MyWorkerTask extends ForkJoinTask<Void> {

    /**
     * Serial Version UID of the class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Name of the task 
     */
    private String name;

    /**
     * Constructor of the class. Initializes its attributes 
     * @param name Name of the task
     */
    public MyWorkerTask(String name) {
        this.name = name;
    }

    /**
     * Method that returns the result of the task. In this case, as 
     * the task doesn't return a result, it returns a null value
     */
    @Override
    public Void getRawResult() {
        return null;
    }

    /**
     * Method that establish the result of the task. In this case, as
     * the task doesn't return a result, this method is empty
     */
    @Override
    protected void setRawResult(Void value) {

    }

    /**
     * Main method of the task. Is called by the Fork/Join pool. It calls
     * the compute() method that is an abstract method that have to be
     * implemented by the tasks that extend this class, calculating its execution
     * time and writing it in the console
     */
    @Override
    protected boolean exec() {
        Date startDate = new Date();
        compute();
        Date finishDate = new Date();
        long diff = finishDate.getTime() - startDate.getTime();
        System.out.printf("MyWorkerTask: %s : %d Milliseconds to complete.\n", name, diff);
        return true;
    }

    /**
     * Method that returns the name of the console
     * @return The name of the task
     */
    public String getName() {
        return name;
    }

    /**
     * Main method of the child tasks. It has to be overridden in the child classes 
     * and implement on it its main logic
     */
    protected abstract void compute();
}

class Task extends MyWorkerTask {

    /**
     * Serival Version UID of the task
     */
    private static final long serialVersionUID = 1L;

    /**
     * Array of integers. This task will increment all the elements of the array
     */
    private int array[];
    /**
     * First element of the array that this task is going to increment
     */
    private int start;

    /**
     * Last element of the array that this task is going to increment
     */
    private int end;

    /**
     * Constructor of the class. It initializes its attributes
     * @param name Name of the task
     * @param array Array of elements that is going to be incremented
     * @param start First element of the array to be incremented by this task
     * @param end Last element of the array to be incremented by this task
     */
    public Task(String name, int array[], int start, int end) {
        super(name);
        this.array = array;
        this.start = start;
        this.end = end;
    }

    /**
     * Main method of the task. If the task has to increment less that 100
     * elements, it increments them directly. Else, it divides the
     * operation in two subtasks
     */
    @Override
    protected void compute() {
        if (end - start > 100) {
            int mid = (end + start) / 2;
            Task task1 = new Task(this.getName() + "1", array, start, mid);
            Task task2 = new Task(this.getName() + "2", array, mid, end);
            invokeAll(task1, task2);
        } else {
            for (int i = start; i < end; i++) {
                array[i]++;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
