package com.olmlo.thread.chapter7.recipe09.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class of the example.
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        /*
         * Create a Prioriy Transfer Queue
         */
        MyPriorityTransferQueue<Event> buffer = new MyPriorityTransferQueue<>();

        /*
         * Create a Producer object
         */
        Producer producer = new Producer(buffer);

        /*
         * Launch 10 producers
         */
        Thread producerThreads[] = new Thread[10];
        for (int i = 0; i < producerThreads.length; i++) {
            producerThreads[i] = new Thread(producer);
            producerThreads[i].start();
        }

        /*
         * Create and launch the consumer
         */
        Consumer consumer = new Consumer(buffer);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

        /*
         * Write in the console the actual consumer count
         */
        System.out.printf("Main: Buffer: Consumer count: %d\n", buffer.getWaitingConsumerCount());

        /*
         * Transfer an event to the consumer
         */
        Event myEvent = new Event("Core Event", 0);
        buffer.transfer(myEvent);
        System.out.printf("Main: My Event has ben transfered.\n");

        /*
         * Wait for the finalization of the producers
         */
        for (int i = 0; i < producerThreads.length; i++) {
            producerThreads[i].join();
        }

        /*
         * Sleep the thread for one second
         */
        TimeUnit.SECONDS.sleep(1);

        /*
         * Write the actual consumer count
         */
        System.out.printf("Main: Buffer: Consumer count: %d\n", buffer.getWaitingConsumerCount());

        /*
         * Transfer another event
         */
        myEvent = new Event("Core Event 2", 0);
        buffer.transfer(myEvent);

        /*
         * Wait for the finalization of the consumer
         */
        consumerThread.join();

        /*
         * Write a message indicating the end of the program
         */
        System.out.printf("Main: End of the program\n");
    }

}

class Consumer implements Runnable {

    /**
     * Buffer from which the consumer takes the events
     */
    private MyPriorityTransferQueue<Event> buffer;

    /**
     * Constructor of the class. Initializes its attributes
     * @param buffer Buffer from which the consumer takes the events
     */
    public Consumer(MyPriorityTransferQueue<Event> buffer) {
        this.buffer = buffer;
    }

    /**
     * Main method of the consumer. It takes 1002 events from the buffer
     */
    @Override
    public void run() {
        for (int i = 0; i < 1002; i++) {
            try {
                Event value = buffer.take();
                System.out.printf("Consumer: %s: %d\n", value.getThread(), value.getPriority());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

class Event implements Comparable<Event> {

    /**
     * Number of the thread that generates the event
     */
    private String thread;
    /**
     * Priority of the thread
     */
    private int priority;

    /**
     * Constructor of the thread. It initializes its attributes
     * @param thread Number of the thread that generates the event
     * @param priority Priority of the event
     */
    public Event(String thread, int priority) {
        this.thread = thread;
        this.priority = priority;
    }

    /**
     * Method that returns the number of the thread that generates the
     * event
     * @return The number of the thread that generates the event
     */
    public String getThread() {
        return thread;
    }

    /**
     * Method that returns the priority of the event
     * @return The priority of the event
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Method that compares two events and decide which has more priority
     */
    @Override
    public int compareTo(Event e) {
        if (this.priority > e.getPriority()) {
            return -1;
        } else if (this.priority < e.getPriority()) {
            return 1;
        } else {
            return 0;
        }
    }
}

class MyPriorityTransferQueue<E> extends PriorityBlockingQueue<E> implements TransferQueue<E> {

    /**
    * Serial Version of the class
    */
    private static final long serialVersionUID = 1L;

    /**
    * Number of consumers waiting
    */
    private AtomicInteger counter;

    /**
    * Blocking queue to store the transfered elements
    */
    private LinkedBlockingQueue<E> transfered;

    /**
    * Lock to control the acces to the operations
    */
    private ReentrantLock lock;

    /**
    * Constructor of the class
    */
    public MyPriorityTransferQueue() {
        counter = new AtomicInteger(0);
        lock = new ReentrantLock();
        transfered = new LinkedBlockingQueue<>();
    }

    /**
    * This method tries to transfer an element to a consumer. If there is
    * a consumer waiting, we puts the element in the queue and return the
    * true value. Else, return the false value.
    */
    @Override
    public boolean tryTransfer(E e) {
        lock.lock();
        boolean value;
        if (counter.get() == 0) {
            value = false;
        } else {
            put(e);
            value = true;
        }
        lock.unlock();
        return value;
    }

    /**
    * Transfer an element to the consumer. If there is a consumer waiting,
    * puts the element on the queue and return the true value. Else, puts the
    * value in the transfered queue and returns the false value. In this case, the
    * thread than makes the call will be blocked until a consumer takes the transfered
    * elements
    */
    @Override
    public void transfer(E e) throws InterruptedException {
        lock.lock();
        if (counter.get() != 0) {
            put(e);
            lock.unlock();
        } else {
            transfered.add(e);
            lock.unlock();
            synchronized (e) {
                e.wait();
            }
        }
    }

    /**
    * This method tries to transfer an element to a consumer waiting a maximum period
    * of time. If there is a consumer waiting, puts the element in the queue. Else,
    * puts the element in the queue of transfered elements and wait the specified period of time
    * until that time pass or the thread is interrupted.
    */
    @Override
    public boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        if (counter.get() != 0) {
            put(e);
            lock.unlock();
            return true;
        }
        transfered.add(e);
        long newTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
        lock.unlock();
        e.wait(newTimeout);
        lock.lock();
        if (transfered.contains(e)) {
            transfered.remove(e);
            lock.unlock();
            return false;
        }
        lock.unlock();
        return true;
    }

    /**
    * Method that returns if the queue has waiting consumers
    */
    @Override
    public boolean hasWaitingConsumer() {
        return (counter.get() != 0);
    }

    /**
    * Method that returns the number of waiting consumers
    */
    @Override
    public int getWaitingConsumerCount() {
        return counter.get();
    }

    /**
    * Method that returns the first element of the queue or is blocked if the queue
    * is empty. If there is transfered elements, takes the first transfered element and
    * wake up the thread that is waiting for the transfer of that element. Else, takes the
    * first element of the queue or is blocked until there is one element in the queue.
    */
    @Override
    public E take() throws InterruptedException {
        lock.lock();
        counter.incrementAndGet();
        E value = transfered.poll();
        if (value == null) {
            lock.unlock();
            value = super.take();
            lock.lock();
        } else {
            synchronized (value) {
                value.notify();
            }
        }
        counter.decrementAndGet();
        lock.unlock();
        return value;
    }
}

class Producer implements Runnable {

    /**
     * Buffer used to store the events
     */
    private MyPriorityTransferQueue<Event> buffer;

    /**
     * Constructor of the class. It initializes its parameters
     * @param buffer Buffer to store the events
     */
    public Producer(MyPriorityTransferQueue<Event> buffer) {
        this.buffer = buffer;
    }

    /**
     * Main method of the producer. Store 100 events in the buffer with
     * incremental priority
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            Event event = new Event(Thread.currentThread().getName(), i);
            buffer.put(event);
        }
    }

}
