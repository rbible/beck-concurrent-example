package com.olmlo.thread.sync;

/**
 * volatile demo
 * @author zhaoming@yy.com
 * 2014-4-8
 */
public class VolatileExample extends Thread {

    // 共享变量
    private static volatile boolean flag = false;

    // private static boolean flag = false;

    // 无限循环读共享变量
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
            new VolatileExample().start();
        }
        Thread.sleep(100);
        flag = true;
    }
}
