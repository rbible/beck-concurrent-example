package com.olmlo.thread.pool.scheduled;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Creates a MyScheduledThreadPoolExecutor and
 * executes a delayed task and a periodic task in it.
 */
public class MyScheduledThreadPoolExecutorDemo {

    public static void main(String[] args) throws Exception {

        MyScheduledThreadPoolExecutor executor = new MyScheduledThreadPoolExecutor(2);

        Task task = new Task();

        System.out.printf("Main: %s\n", new Date());

        /* Send to the executor a delayed task. It will be executed after 1 second of delay */
        executor.schedule(task, 1, TimeUnit.SECONDS);

        /* Sleeps the thread three seconds */
        TimeUnit.SECONDS.sleep(3);

        task = new Task();

        System.out.printf("Main: %s\n", new Date());

        /*
         * Send to the executor a delayed task. It will begin its execution after 1 second of dealy and then it will be
         * executed every three seconds
         */
        executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);

        /* Sleep the thread during ten seconds */
        TimeUnit.SECONDS.sleep(10);

        /* Shutdown the executor */
        executor.shutdown();

        /* Wait for the finalization of the executor */
        executor.awaitTermination(1, TimeUnit.DAYS);

        /* Write a message indicating the end of the program */
        System.out.printf("Main: End of the program.\n");
    }
}

class MyScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    /**
     * Constructor of the class. Calls the constructor of its parent class using the super keyword
     * @param corePoolSize Number of threads to keep in the pool
     */
    public MyScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    /**
     * Method that converts a RunnableScheduledFuture task in a MyScheduledTask task
     */
    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        MyScheduledTask<V> myTask = new MyScheduledTask<V>(runnable, null, task, this);
        return myTask;
    }

    /**
     * Method that schedule in the executor a periodic tasks. It calls the method of its parent class using
     * the super keyword and stores the period of the task.
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture<?> task = super.scheduleAtFixedRate(command, initialDelay, period, unit);
        MyScheduledTask<?> myTask = (MyScheduledTask<?>) task;
        myTask.setPeriod(TimeUnit.MILLISECONDS.convert(period, unit));
        return task;
    }

}

class MyScheduledTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {

    /**
     * Attribute to store the task that will be used to create a MyScheduledTask
     */
    private RunnableScheduledFuture<V> task;

    /**
     * ScheduledThreadPoolExecutor that is going to execute the task
     */
    private ScheduledThreadPoolExecutor executor;

    /**
     * Period of time between two executions of the task
     */
    private long period;

    /**
     * Date when will begin the next execution of the task
     */
    private long startDate;

    /**
     * Constructor of the class. It initializes the attributes of the class
     * @param runnable Runnable submitted to be executed by the task
     * @param result Result that will be returned by the task
     * @param task Task that will execute the Runnable object
     * @param executor Executor that is going to execute the task
     */
    public MyScheduledTask(Runnable runnable, V result, RunnableScheduledFuture<V> task, ScheduledThreadPoolExecutor executor) {
        super(runnable, result);
        this.task = task;
        this.executor = executor;
    }

    /**
     * Method that returns the reminder for the next execution of the task. If is 
     * a delayed task, returns the delay of the original task. Else, return the difference
     * between the startDate attribute and the actual date.
     * @param unit TimeUnit to return the result
     */
    @Override
    public long getDelay(TimeUnit unit) {
        if (!isPeriodic()) {
            return task.getDelay(unit);
        }
        if (startDate == 0) {
            return task.getDelay(unit);
        }
        Date now = new Date();
        long delay = startDate - now.getTime();
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Method to compare two tasks. It calls the compareTo() method of the original task
     */
    @Override
    public int compareTo(Delayed o) {
        return task.compareTo(o);
    }

    /**
     * Method that returns if the task is periodic or not. It calls the isPeriodic() method
     * of the original task
     */
    @Override
    public boolean isPeriodic() {
        return task.isPeriodic();
    }

    /**
     * Method that executes the task. If it's a periodic task, it updates the 
     * start date of the task and store in the queue of the executor the task to
     * be executed again
     */
    @Override
    public void run() {
        if (isPeriodic() && (!executor.isShutdown())) {
            Date now = new Date();
            startDate = now.getTime() + period;
            executor.getQueue().add(this);
        }
        System.out.printf("Pre-MyScheduledTask: %s\n", new Date());
        System.out.printf("MyScheduledTask: Is Periodic: %s\n", isPeriodic());
        super.runAndReset();
        System.out.printf("Post-MyScheduledTask: %s\n", new Date());
    }

    /**
     * Method that establish the period of the task for periodic tasks
     * @param period
     */
    public void setPeriod(long period) {
        this.period = period;
    }
}

class Task implements Runnable {

    /**
     * Main method of the task. Writes a message, sleeps the current thread for two seconds and
     * writes another message
     */
    @Override
    public void run() {
        System.out.printf("Task: Begin.\n");
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.printf("Task: Runing.\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Task: End.\n");
    }
}
