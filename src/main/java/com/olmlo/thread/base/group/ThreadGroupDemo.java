package com.olmlo.thread.base.group;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ThreadGroupDemo {

    public static void main(String[] args) {
        try {
            threadGroup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void threadGroup() throws InterruptedException {

        ThreadGroup threadGroup = new ThreadGroup("myGroup");
        System.out.println(threadGroup);
        System.out.println(Thread.currentThread().getThreadGroup());

        int loopCount = 3;
        Thread interruptRandomThread = null;
        for (int i = 0; i < loopCount; i++) {
            Thread t = newThread(threadGroup, i);
            t.setPriority(new Random().nextInt(10) + 1);
            t.start();
            if (i == 1)
                interruptRandomThread = t;
        }
        System.out.println("threadGroup list...");
        threadGroup.list();

        TimeUnit.SECONDS.sleep(4);

        System.out.println("threadGroup.interrupt...");
        threadGroup.interrupt();

        // interruptRandomThread.interrupt();
        System.out.println("interrupt 1:" + interruptRandomThread);
        Thread.currentThread().interrupt();

        // System.out.println("interrupt 2:" + interruptRandomThread);
        TimeUnit.SECONDS.sleep(1);
    }

    private static Thread newThread(ThreadGroup threadGroup, int i) {
        return new Thread(threadGroup, "a-" + i) {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.err.println(this + " group:" + this.getThreadGroup());
                    } catch (InterruptedException e) {
                        System.out.println("is 1:" + Thread.currentThread().isInterrupted());
                        System.out.println("is 2:" + Thread.currentThread().isInterrupted());
                        Thread.currentThread().interrupt();
                        System.out.println("is 3:" + Thread.currentThread().isInterrupted());
                        // e.printStackTrace();
                    }
                }
            }
        };
    }
}
