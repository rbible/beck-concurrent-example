package com.olmlo.thread.data.collection;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Main class of the example. First, execute 100 AddTask objects to
 * add 1000000 elements to the list and the execute 100 PollTask objects
 * to delete all those elements.
 *
 */
public class ConcurrentLinkedDequeDemo{

    /**
     * Main method of the class
     * @param args
     */
    public static void main(String[] args) throws Exception {

        // Create a ConcurrentLinkedDeque to work with it in the example
        ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
        // Create an Array of 100 threads
        Thread threads[] = new Thread[100];

        // Create 100 AddTask objects and execute them as threads
        for (int i = 0; i < threads.length; i++) {
            AddTask task = new AddTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }
        System.out.printf("Main: %d AddTask threads have been launched\n", threads.length);

        // Wait for the finalization of the threads
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        // Write to the console the size of the list
        System.out.printf("Main: Size of the List: %d\n", list.size());

        // Create 100 PollTask objects and execute them as threads
        for (int i = 0; i < threads.length; i++) {
            PollTask task = new PollTask(list);
            threads[i] = new Thread(task);
            threads[i].start();
        }
        System.out.printf("Main: %d PollTask threads have been launched\n", threads.length);

        // Wait for the finalization of the threads
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        // Write to the console the size of the list
        System.out.printf("Main: Size of the List: %d\n", list.size());
    }
}

class AddTask implements Runnable {

    /**
     * List to add the elements
     */
    private ConcurrentLinkedDeque<String> list;

    /**
     * Constructor of the class
     * @param list List to add the elements
     */
    public AddTask(ConcurrentLinkedDeque<String> list) {
        this.list = list;
    }

    /**
     * Main method of the class. Add 10000 elements to the list
     * using the add() method that adds the element at the end of
     * the list
     */
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        for (int i = 0; i < 10000; i++) {
            list.add(name + ": Element " + i);
        }
    }

}

class PollTask implements Runnable {

    /**
     * List to delete the elements
     */
    private ConcurrentLinkedDeque<String> list;

    /**
     * Constructor of the class
     * @param list List to delete the elements
     */
    public PollTask(ConcurrentLinkedDeque<String> list) {
        this.list = list;
    }

    /**
     * Main method of the task. Deletes 10000 elements from the
     * list using the pollFirst() that deletes the first element of the
     * list and pollLast() that deletes the last element of the list
     */
    @Override
    public void run() {
        for (int i = 0; i < 5000; i++) {
            list.pollFirst();
            list.pollLast();
        }
    }

}
