package com.olmlo.thread.chapter4.recipe10.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Creates five tasks that wait a random period of time.
 * Waits 5 seconds and cancel all the tasks. Then, write the results of that tasks
 * that haven't been cancelled.
 *
 */
public class Main {

    /**
     * Main method of the class.
     * @param args
     */
    public static void main(String[] args) {
        // Create an executor
        ExecutorService executor = (ExecutorService) Executors.newCachedThreadPool();

        // Create five tasks
        ResultTask resultTasks[] = new ResultTask[5];
        for (int i = 0; i < 5; i++) {
            ExecutableTask executableTask = new ExecutableTask("Task " + i);
            resultTasks[i] = new ResultTask(executableTask);
            executor.submit(resultTasks[i]);
        }

        // Sleep the thread five seconds
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        // Cancel all the tasks. In the tasks that have finished before this moment, this
        // cancellation has no effects
        for (int i = 0; i < resultTasks.length; i++) {
            resultTasks[i].cancel(true);
        }

        // Write the results of those tasks that haven't been cancelled
        for (int i = 0; i < resultTasks.length; i++) {
            try {
                if (!resultTasks[i].isCancelled()) {
                    System.out.printf("%s\n", resultTasks[i].get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // Finish the executor.
        executor.shutdown();

    }

}

class ExecutableTask implements Callable<String> {

    /**
     * The name of the class
     */
    private String name;

    /**
     * Constructor of the class
     * @param name The name of the class
     */
    public ExecutableTask(String name) {
        this.name = name;
    }

    /**
     * Main method of the task. It waits a random period of time and returns a message
     */
    @Override
    public String call() throws Exception {
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
        }
        return "Hello, world. I'm " + name;
    }

    /**
     * This method returns the name of the task
     * @return The name of the task
     */
    public String getName() {
        return name;
    }
}

class ResultTask extends FutureTask<String> {

    /**
     * Name of the ResultTask. It's initialized with the name of the
     * ExecutableTask that manages
     */
    private String name;

    /**
     * Constructor of the Class. Override one of the constructor of its parent class 
     * @param callable The task this object manages
     */
    public ResultTask(Callable<String> callable) {
        super(callable);
        this.name = ((ExecutableTask) callable).getName();
    }

    /**
     * Method that is called when the task finish.
     */
    @Override
    protected void done() {
        if (isCancelled()) {
            System.out.printf("%s: Has been cancelled\n", name);
        } else {
            System.out.printf("%s: Has finished\n", name);
        }
    }

}
