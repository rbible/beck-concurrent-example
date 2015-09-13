package com.olmlo.thread.data.collection;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Main method of the example. Execute five tasks and then
 * take the events of the delayed queue when they are activated
 *
 */
public class DelayQueueDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        /* Delayed queue to store the events */
        DelayQueue<Event> queue = new DelayQueue<>();

        /* An array to store the Thread objects that execute the tasks */
        Thread threads[] = new Thread[5];

        /* Create the five tasks */
        for (int i = 0; i < threads.length; i++) {
            Task task = new Task(i + 1, queue);
            threads[i] = new Thread(task);
        }

        /* Execute the five tasks */
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        /* Wait for the finalization of the five tasks */
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        /* Write the results to the console */
        do {
            int counter = 0;
            Event event;
            do {
                event = queue.poll();
                if (event != null)
                    counter++;
            } while (event != null);
            System.out.printf("At %s you have read %d events\n", new Date(), counter);
            TimeUnit.MILLISECONDS.sleep(500);
        } while (queue.size() > 0);
    }
}

class Task implements Runnable {

    /**
     * Id of the task
     */
    private int id;

    /**
     * Delayed queue to store the events
     */
    private DelayQueue<Event> queue;

    /**
     * Constructor of the class. It initializes its attributes
     * @param id Id of the task
     * @param queue Delayed queue to store the events
     */
    public Task(int id, DelayQueue<Event> queue) {
        this.id = id;
        this.queue = queue;
    }

    /**
     * Main method of the task. It generates 100 events with the
     * same activation time. The activation time will be the execution
     * time of the thread plus the id of the thread seconds
     */
    @Override
    public void run() {

        Date now = new Date();
        Date delay = new Date();
        delay.setTime(now.getTime() + (id * 1000));

        System.out.printf("Thread %s: %s\n", id, delay);

        for (int i = 0; i < 100; i++) {
            Event event = new Event(delay);
            queue.add(event);
        }
    }

}

class Event implements Delayed {

    /**
     * Date when we want to activate the event
     */
    private Date startDate;

    /**
     * Constructor of the class
     * @param startDate Date when we want to activate the event
     */
    public Event(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Method to compare two events
     */
    @Override
    public int compareTo(Delayed o) {
        long result = this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        if (result < 0) {
            return -1;
        } else if (result > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Method that returns the remaining time to the activation of the event
     */
    @Override
    public long getDelay(TimeUnit unit) {
        Date now = new Date();
        long diff = startDate.getTime() - now.getTime();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

}
