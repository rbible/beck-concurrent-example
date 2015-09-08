package com.olmlo.thread.blocking;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueueDemo {
    public static void main(String[] args) throws Exception {
        ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(10);
        producerConsumerRun(arrayBlockingQueue);
    }

    public static void producerConsumerRun(ArrayBlockingQueue<String> abq) {
        Thread tConsumer = new Consumer(abq);
        Thread tProducer = new Producer(abq);
        tConsumer.start();
        tProducer.start();
    }
}

class Consumer extends Thread {
    ArrayBlockingQueue<String> abq = null;

    public Consumer(ArrayBlockingQueue<String> abq) {
        super();
        this.abq = abq;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
                String msg = abq.remove();
                System.out.println("get      data ：" + msg + "\t surplus data size：" + abq.size());
            } catch (Exception e) {
                System.out.println(" no data");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}

class Producer extends Thread {
    ArrayBlockingQueue<String> abq = null;

    public Producer(ArrayBlockingQueue<String> abq) {
        this.abq = abq;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                Thread.sleep(1000);
                abq.put("" + i);
                System.out.println("generate data ：" + i + "\t surplus data size：" + abq.size());
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}