package com.olmlo.thread.blocking;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueueDemo3 {
    public static void main(String[] args) throws Exception {
        insertBlocking();
    }

    /**
     * insert blocking: if ArrayBlockingQueue is full,insert blocking
     */
    public static void insertBlocking() throws InterruptedException {
        ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(1);
        names.put("a");
        System.out.println("put(a) queue size " + names.size());
        names.put("b"); // out of size blocking
        System.out.println("put(b) queue size " + names.size());

        System.out.println("end");
    }
}