package com.olmlo.thread.demo;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerDemo4 {

    public static void main(String[] args) {
        /**
         * Creates a simulated file with 100 lines
         */
        FileMock5 mock = new FileMock5(101, 10);

        /**
         * Creates a buffer with a maximum of 20 lines
         */
        Buffer buffer = new Buffer(20);

        /**
         * Creates a producer and a thread to run it
         */
        Producer5 producer = new Producer5(mock, buffer);
        Thread threadProducer = new Thread(producer, "Producer");

        /**
         * Creates three consumers and threads to run them
         */
        Consumer5 consumers[] = new Consumer5[3];
        Thread threadConsumers[] = new Thread[3];

        for (int i = 0; i < 3; i++) {
            consumers[i] = new Consumer5(buffer);
            threadConsumers[i] = new Thread(consumers[i], "Consumer " + i);
        }

        /**
         * Strats the producer and the consumers
         */
        threadProducer.start();
        for (int i = 0; i < 3; i++) {
            threadConsumers[i].start();
        }
    }

}

class Buffer {

    /**
     * The buffer
     */
    private LinkedList<String> buffer;

    /**
     * Size of the buffer
     */
    private int maxSize;

    /**
     * Lock to control the access to the buffer
     */
    private ReentrantLock lock;

    /**
     * Conditions to control that the buffer has lines and has empty space
     */
    private Condition lines;
    private Condition space;

    /**
     * Attribute to control where are pending lines in the buffer
     */
    private boolean pendingLines;

    /**
     * Constructor of the class. Initialize all the objects
     * 
     * @param maxSize
     *            The size of the buffer
     */
    public Buffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedList<>();
        lock = new ReentrantLock();
        lines = lock.newCondition();
        space = lock.newCondition();
        pendingLines = true;
    }

    /**
     * Insert a line in the buffer
     * 
     * @param line
     *            line to insert in the buffer
     */
    public void insert(String line) {
        lock.lock();
        try {
            while (buffer.size() == maxSize) {
                space.await();
            }
            buffer.offer(line);
            System.out.printf("%s: Inserted Line: %d\n", Thread.currentThread().getName(), buffer.size());
            lines.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a line from the buffer
     * 
     * @return a line from the buffer
     */
    public String get() {
        String line = null;
        lock.lock();
        try {
            while ((buffer.size() == 0) && (hasPendingLines())) {
                lines.await();
            }

            if (hasPendingLines()) {
                line = buffer.poll();
                System.out.printf("%s: Line Readed: %d\n", Thread.currentThread().getName(), buffer.size());
                space.signalAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return line;
    }

    /**
     * Establish the value of the variable
     * 
     * @param pendingLines
     */
    public void setPendingLines(boolean pendingLines) {
        this.pendingLines = pendingLines;
    }

    /**
     * Returns the value of the variable
     * 
     * @return the value of the variable
     */
    public boolean hasPendingLines() {
        return pendingLines || buffer.size() > 0;
    }
}

class FileMock5 {

    /**
     * Content of the simulate file
     */
    private String content[];
    /**
     * Number of the line we are processing
     */
    private int index;

    /**
     * Constructor of the class. Generate the random data of the file
     * @param size: Number of lines in the simulate file
     * @param length: Length of the lines
     */
    public FileMock5(int size, int length) {
        content = new String[size];
        for (int i = 0; i < size; i++) {
            StringBuilder buffer = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                int indice = (int) Math.random() * 255;
                buffer.append((char) indice);
            }
            content[i] = buffer.toString();
        }
        index = 0;
    }

    /**
     * Returns true if the file has more lines to process or false if not
     * @return true if the file has more lines to process or false if not
     */
    public boolean hasMoreLines() {
        return index < content.length;
    }

    /**
     * Returns the next line of the simulate file or null if there aren't more lines
     * @return
     */
    public String getLine() {
        if (this.hasMoreLines()) {
            System.out.println("Mock: " + (content.length - index));
            return content[index++];
        }
        return null;
    }

}

class Consumer5 implements Runnable {

    /**
     * The buffer
     */
    private Buffer buffer;

    /**
     * Constructor of the class. Initialize the buffer
     * @param buffer
     */
    public Consumer5(Buffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Core method of the consumer. While there are pending lines in the
     * buffer, try to read one.
     */
    @Override
    public void run() {
        while (buffer.hasPendingLines()) {
            String line = buffer.get();
            processLine(line);
        }
    }

    /**
     * Method that simulates the processing of a line. Waits 10 milliseconds
     * @param line
     */
    private void processLine(String line) {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class Producer5 implements Runnable {

    /**
     * Simulated File
     */
    private FileMock5 mock;

    /**
     * Buffer
     */
    private Buffer buffer;

    /**
     * Constructor of the class. Initialize the objects
     * @param mock Simulated file
     * @param buffer Buffer
     */
    public Producer5(FileMock5 mock, Buffer buffer) {
        this.mock = mock;
        this.buffer = buffer;
    }

    /**
     * Core method of the producer. While are pending lines in the
     * simulated file, reads one and try to store it in the buffer.
     */
    @Override
    public void run() {
        buffer.setPendingLines(true);
        while (mock.hasMoreLines()) {
            String line = mock.getLine();
            buffer.insert(line);
        }
        buffer.setPendingLines(false);
    }

}
