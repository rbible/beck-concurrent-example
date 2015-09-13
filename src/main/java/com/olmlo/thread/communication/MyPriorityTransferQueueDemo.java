package com.olmlo.thread.communication;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MyPriorityTransferQueueDemo {

    public static void main(String[] args) throws Exception {

        MyPriorityTransferQueue<Event> transferQueue = new MyPriorityTransferQueue<>();
        TransferProducer producer = new TransferProducer(transferQueue);
        Thread producerThreads[] = new Thread[10];
        for (int i = 0; i < producerThreads.length; i++) {
            producerThreads[i] = new Thread(producer);
            producerThreads[i].start();
        }

        TransferConsumer consumer = new TransferConsumer(transferQueue);
        Thread consumerThread = new Thread(consumer);
        consumerThread.start();
        System.out.printf("Main: Buffer: Consumer count: %d\n", transferQueue.getWaitingConsumerCount());

        Event myEvent = new Event("Core Event", 0);
        transferQueue.transfer(myEvent);
        System.out.printf("Main: My Event has ben transfered.\n");

        for (int i = 0; i < producerThreads.length; i++) {
            producerThreads[i].join();
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.printf("Main: Buffer: Consumer count: %d\n", transferQueue.getWaitingConsumerCount());

        myEvent = new Event("Core Event 2", 0);
        transferQueue.transfer(myEvent);

        consumerThread.join();
        System.out.printf("Main: End of the program\n");
    }

}

class TransferConsumer implements Runnable {

    /**
     * Buffer from which the consumer takes the events
     */
    private MyPriorityTransferQueue<Event> buffer;

    /**
     * Constructor of the class. Initializes its attributes
     * @param buffer Buffer from which the consumer takes the events
     */
    public TransferConsumer(MyPriorityTransferQueue<Event> buffer) {
        this.buffer = buffer;
    }

    /**
     * Main method of the consumer. It takes 1002 events from the buffer
     */
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
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

class TransferProducer implements Runnable {

    /**
     * Buffer used to store the events
     */
    private MyPriorityTransferQueue<Event> buffer;

    /**
     * Constructor of the class. It initializes its parameters
     * @param buffer Buffer to store the events
     */
    public TransferProducer(MyPriorityTransferQueue<Event> buffer) {
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
