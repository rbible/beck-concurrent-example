package com.olmlo.thread.demo;

import java.util.Date;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Creates a factory, a MyThread object to execute a Task object
 * and executes the Thread
 * 	
 */
public class MyThreadFactoryDemo2 {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /*
         * Create a Factory
         */
        MyThreadFactory2 myFactory = new MyThreadFactory2("MyThreadFactory");

        /*
         * Crate a Task
         */
        MyTask2 task = new MyTask2();

        /*
         * Create a Thread using the Factory to execute the Task
         */
        Thread thread = myFactory.newThread(task);

        /*
         * Start the Thread
         */
        thread.start();

        /*
         * Wait for the finalization of the Thread
         */
        thread.join();

        /*
         * Write the thread info to the console
         */
        System.out.printf("Main: Thread information.\n");
        System.out.printf("%s\n", thread);
        System.out.printf("Main: End of the example.\n");

    }

}

class MyTask2 implements Runnable {

    /**
     * Main method of the Thread. Sleeps the thread during two seconds
     */
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class MyThread2 extends Thread {

    /**
     * Creation date of the Thread
     */
    private Date creationDate;

    /**
     * Start date of the Thread
     */
    private Date startDate;

    /**
     * Finish date of the Thread
     */
    private Date finishDate;

    /**
     * Constructor of the class. Use the constructor of the Thread class and storeas the creation date of the Thread
     * @param target Task to execute
     * @param name Name of the thread
     */
    public MyThread2(Runnable target, String name) {
        super(target, name);
        setCreationDate();
    }

    /**
     * Main method of the thread. Stores the start date and the finish date and calls the run() method of the parent class
     */
    @Override
    public void run() {
        setStartDate();
        super.run();
        setFinishDate();
    }

    /**
     * Method that establish the value of the creation date
     */
    public void setCreationDate() {
        creationDate = new Date();
    }

    /**
     * Method that establish the value of the start date
     */
    public void setStartDate() {
        startDate = new Date();
    }

    /**
     * Method that establish the value of the finish date
     */
    public void setFinishDate() {
        finishDate = new Date();
    }

    /**
     * Method that calculates the execution time of the thread
     * @return The execution time of the thread
     */
    public long getExecutionTime() {
        return finishDate.getTime() - startDate.getTime();
    }

    /**
     * Method that writes information about the thread
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getName());
        buffer.append(": ");
        buffer.append(" Creation Date: ");
        buffer.append(creationDate);
        buffer.append(" : Running time: ");
        buffer.append(getExecutionTime());
        buffer.append(" Milliseconds.");
        return buffer.toString();
    }
}

class MyThreadFactory2 implements ThreadFactory {

    /**
     * Attribute to store the number of threads created in this factory
     */
    private int counter;

    /**
     * String to create the name of the threads created with this factory
     */
    private String prefix;

    /**
     * Constructor of the class. Initialize its parameters
     * @param prefix First part of the name of the threads created with this factory
     */
    public MyThreadFactory2(String prefix) {
        this.prefix = prefix;
        counter = 1;
    }

    /**
     * Method that creates a new MyThread thread
     */
    @Override
    public Thread newThread(Runnable r) {
        MyThread2 myThread = new MyThread2(r, prefix + "-" + counter);
        counter++;
        return myThread;
    }

}
