package com.olmlo.thread.factory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;

/**
 * Main class of the example. It creates an array of 100000 elements, initializes all
 * the elements to the 1 value, creates a new ForkJoinPool with the new 
 *
 */
public class MyWorkerThreadFactoryDemo {

    public static void main(String[] args) throws Exception {

        MyWorkerThreadFactory factory = new MyWorkerThreadFactory();
        ForkJoinPool pool = new ForkJoinPool(4, factory, null, false);

        int array[] = new int[100000];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1;
        }

        /* Create a new Task to sum the elements of the array */
        MyRecursiveTask task = new MyRecursiveTask(array, 0, array.length);
        pool.execute(task);
        task.join();

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        System.out.printf("Main: Result: %d\n", task.get());
        System.out.printf("Main: End of the program\n");
    }

}

class MyRecursiveTask extends RecursiveTask<Integer> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Array to be summed
     */
    private int array[];

    /**
     * Start and end positions of the part of the array to be summed by this task
     */
    private int start, end;

    /**
     * Constructor of the class. It initializes the  attributes of the task
     * @param array Array to be summed
     * @param start Start position of the block of the array to be summed by this task
     * @param end End position of the block of the array to be summed by this task
     */
    public MyRecursiveTask(int array[], int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    /**
     * Main method of the task. If the task has less than 100 elements to sum, it calculates
     * the sum of these elements directly. Else, it creates two subtask to process the two
     * halves of the block.
     * 
     * It also calls the addTask() method of the thread that is executing the task to
     * updates its internal counter of tasks
     */
    @Override
    protected Integer compute() {
        Integer ret;
        MyWorkerThread thread = (MyWorkerThread) Thread.currentThread();
        thread.addTask();
        if (end - start > 100) {
            int mid = (start + end) / 2;
            MyRecursiveTask task1 = new MyRecursiveTask(array, start, mid);
            MyRecursiveTask task2 = new MyRecursiveTask(array, mid, end);
            invokeAll(task1, task2);
            ret = addResults(task1, task2);
        } else {
            int add = 0;
            for (int i = start; i < end; i++) {
                add += array[i];
            }
            ret = new Integer(add);
        }
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Method that adds the results of the two subtasks create by this task
     * @param task1 First task
     * @param task2 Second task
     * @return The sum of the results of the two tasks
     */
    private Integer addResults(MyRecursiveTask task1, MyRecursiveTask task2) {
        int value;
        try {
            value = task1.get().intValue() + task2.get().intValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
            value = 0;
        } catch (ExecutionException e) {
            e.printStackTrace();
            value = 0;
        }
        return new Integer(value);
    }

}

class MyWorkerThread extends ForkJoinWorkerThread {

    /**
     * ThreadLocal attribute to store the number of tasks executed by each thread
     */
    private static ThreadLocal<Integer> taskCounter = new ThreadLocal<>();

    /**
     * Constructor of the class. It calls the constructor of its parent class using the
     * super keyword
     * @param pool ForkJoinPool where the thread will be executed
     */
    protected MyWorkerThread(ForkJoinPool pool) {
        super(pool);
    }

    /**
     * This method is called when a worker thread of the Fork/Join framework begins its execution.
     * It initializes its task counter
     */
    @Override
    protected void onStart() {
        super.onStart();
        System.out.printf("MyWorkerThread %d: Initializing task counter.\n", getId());
        taskCounter.set(0);
    }

    /**
     * This method is called when a worker thread of the Fork/Join framework ends its execution.
     * It writes in the console the value of the taskCounter attribute.
     */
    @Override
    protected void onTermination(Throwable exception) {
        System.out.printf("MyWorkerThread %d: %d\n", getId(), taskCounter.get());
        super.onTermination(exception);
    }

    /**
     * This method is called for each task to increment the task counter of the worker thread
     */
    public void addTask() {
        int counter = taskCounter.get().intValue();
        counter++;
        taskCounter.set(counter);
    }

}

class MyWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

    /**
     * Method that creates a worker thread for the Fork/Join framework
     * @param pool ForkJoinPool where the thread will be executed
     * @return a MyWorkerThread thread
     */
    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new MyWorkerThread(pool);
    }

}
