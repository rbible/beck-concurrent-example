package com.olmlo.thread.pool.recursive;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Creates a ForkJoinPool, an array of 100
 * elements and a Task object. Executes the Task object in the pool
 * and process the exception thrown by the Task
 *
 */
public class RecursiveTaskExceptionDemo {

    /**
     * Main method of the class
     */
    public static void main(String[] args) {
        // Array of 100 integers
        int array[] = new int[100];
        // Task to process the array
        Task task = new Task(array, 0, 100);
        // ForkJoinPool to execute the Task
        ForkJoinPool pool = new ForkJoinPool();

        // Execute the task
        pool.execute(task);

        // Shutdown the ForkJoinPool
        pool.shutdown();

        // Wait for the finalization of the task
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the task has thrown an Exception. If it's the case, write it
        // to the console

        if (task.isCompletedAbnormally()) {
            System.out.printf("Main: An exception has ocurred\n");
            System.out.printf("Main: %s\n", task.getException());
        }

        System.out.printf("Main: Result: %d", task.join());
    }
}

class Task extends RecursiveTask<Integer> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Array to process
     */
    private int array[];

    /**
     * Start and end positions of the block of elements this task
     * has to process
     */
    private int start, end;

    /**
     * Constructor of the class
     * @param array Array to process
     * @param start Start position of the block of elements this task has to process
     * @param end End position of the block of elements this task has to process
     */
    public Task(int array[], int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    /**
     * Main method of the task. If the block of elements it has to process has 10
     *  or more elements, it divides the block in two and executes two subtasks 
     *  to process those blocks. Else, sleeps the task one second. Additionally,
     *  If the block of elements it has to process has the third position, it 
     *  throws an exception.
     */
    @Override
    protected Integer compute() {
        System.out.printf("Task: Start from %d to %d\n", start, end);
        if (end - start < 10) {
            if ((3 > start) && (3 < end)) {
                throw new RuntimeException("This task throws an Exception: Task from  " + start + " to " + end);
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            int mid = (end + start) / 2;
            Task task1 = new Task(array, start, mid);
            Task task2 = new Task(array, mid, end);
            invokeAll(task1, task2);
            System.out.printf("Task: Result form %d to %d: %d\n", start, mid, task1.join());
            System.out.printf("Task: Result form %d to %d: %d\n", mid, end, task2.join());
        }
        System.out.printf("Task: End form %d to %d\n", start, end);
        return new Integer(0);
    }

}
