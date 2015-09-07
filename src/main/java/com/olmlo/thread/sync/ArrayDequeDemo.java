package com.olmlo.thread.sync;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. Creates three WriterTaks and a CleanerTask 
 *
 */
public class ArrayDequeDemo {

    /**
     * Main method of the example. Creates three WriterTasks and a CleanerTask
     * @param args
     */
    public static void main(String[] args) {

        // Creates the Event data structure
        Deque<Event> deque = new ArrayDeque<Event>();

        // Creates the three WriterTask and starts them
        WriterTask writer = new WriterTask(deque);
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(writer);
            thread.start();
        }

        // Creates a cleaner task and starts them
        CleanerTask cleaner = new CleanerTask(deque);
        cleaner.start();
    }
}

class CleanerTask extends Thread {

    /**
     * Data structure that stores events
     */
    private Deque<Event> deque;

    /**
     * Constructor of the class
     * @param deque data structure that stores events
     */
    public CleanerTask(Deque<Event> deque) {
        this.deque = deque;
        // Establish that this is a Daemon Thread
        setDaemon(true);
    }

    /**
     * Main method of the class
     */
    @Override
    public void run() {
        while (true) {
            Date date = new Date();
            clean(date);
        }
    }

    /**
     * Method that review the Events data structure and delete
     * the events older than ten seconds
     * @param date
     */
    private void clean(Date date) {
        long difference;
        boolean delete;

        if (deque.size() == 0) {
            return;
        }

        delete = false;
        do {
            Event e = deque.getLast();
            difference = date.getTime() - e.getDate().getTime();
            if (difference > 10000) {
                System.out.printf("Cleaner: %s\n", e.getEvent());
                deque.removeLast();
                delete = true;
            }
        } while (difference > 10000);
        if (delete) {
            System.out.printf("Cleaner: Size of the queue: %d\n", deque.size());
        }
    }
}

class WriterTask implements Runnable {

    /**
     * Data structure to stores the events
     */
    Deque<Event> deque;

    /**
     * Constructor of the class
     * @param deque data structure that stores the event
     */
    public WriterTask(Deque<Event> deque) {
        this.deque = deque;
    }

    /**
     * Main class of the Runnable
     */
    @Override
    public void run() {

        // Writes 100 events
        for (int i = 1; i < 100; i++) {
            // Creates and initializes the Event objects
            Event event = new Event();
            event.setDate(new Date());
            event.setEvent(String.format("The thread %s has generated an event", Thread.currentThread().getId()));

            // Add to the data structure
            deque.addFirst(event);
            try {
                // Sleeps during one second
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Event {

    /**
     * Date of the event
     */
    private Date date;

    /**
     * Message of the event
     */
    private String event;

    /**
     * Reads the Date of the event
     * @return the Date of the event
     */
    public Date getDate() {
        return date;
    }

    /**
     * Writes the Date of the event
     * @param date the date of the event
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Reads the message of the event
     * @return the message of the event
     */
    public String getEvent() {
        return event;
    }

    /**
     * Writes the message of the event
     * @param event the message of the event
     */
    public void setEvent(String event) {
        this.event = event;
    }
}
