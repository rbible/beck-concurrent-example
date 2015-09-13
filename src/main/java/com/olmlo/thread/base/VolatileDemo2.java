package com.olmlo.thread.base;

public class VolatileDemo2 extends Thread {

    private static volatile boolean flag = false;

    // private static boolean flag = false;

    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!flag) {
            System.out.println(this + ":" + System.currentTimeMillis());
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            new VolatileDemo2().start();
        }
        Thread.sleep(100);
        flag = true;
    }
}
