package com.olmlo.thread.base.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;

public class ThrowsExceptionDemo {

    public static void main(String[] args) {

        OneSecondLongTask task = new OneSecondLongTask();
        Handler handler = new Handler();
        AlwaysThrowsExceptionWorkerThreadFactory factory = new AlwaysThrowsExceptionWorkerThreadFactory();
        ForkJoinPool pool = new ForkJoinPool(2, factory, handler, false);

        pool.execute(task);

        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("Main: The program has finished.\n");
    }
}

class AlwaysThrowsExceptionWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

    /**
     * This method creates a new Worker Thread.
     * @param pool The ForkJoinPool where the thread that is creater
     * is going to execute
     */
    @Override
    public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        return new AlwaysThrowsExceptionWorkerThread(pool);
    }
}

class AlwaysThrowsExceptionWorkerThread extends ForkJoinWorkerThread {

    /**
     * Constructor of the class. Call the constructor of the parent class
     * 
     * @param pool
     *            ForkJoinPool where the thread is going to execute
     */
    protected AlwaysThrowsExceptionWorkerThread(ForkJoinPool pool) {
        super(pool);
    }

    /**
     * Method that is going to execute where the Worker Thread begins its execution
     */
    @Override
    protected void onStart() {
        super.onStart();
        throw new RuntimeException("Exception from worker thread");
    }
}

class Handler implements UncaughtExceptionHandler {

    /**
     * This method process the uncaught exceptions thrown in a 
     * worker thread. 
     * @param t The thread that throws the exception
     * @param e The exception it was thrown
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("Handler: Thread %s has thrown an Exception.\n", t.getName());
        System.out.printf("%s\n", e);
        System.exit(-1);
    }

}

class OneSecondLongTask extends RecursiveAction {

    private static final long serialVersionUID = 1L;

    /**
     * Method that executes the action of the Task. It sleeps
     * the thread during one second
     */
    @Override
    protected void compute() {
        System.out.printf("Task: Starting.\n");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Task: Finish.\n");
    }
}
