package com.olmlo.thread.base;

public class VolatileDemo extends Thread {

    public static volatile int n = 0;

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                n = n + 1;
                sleep(3);// 为了使运行结果更随即，延迟3毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int t = 100, p = 0;
        while (t == 100 && p < 100) {
            System.out.printf("start \t n= %d \n", VolatileDemo.n);
            ThreadUtils.newAndRunThreads(VolatileDemo.class, 10);

            System.out.printf("end \t n= %d \n", VolatileDemo.n);
            t = VolatileDemo.n;
            VolatileDemo.n = 0;
            p++;
        }

        System.out.println("finally n=" + VolatileDemo.n);
        System.out.println("finally p=" + p);
    }

}

class ThreadUtils {
    public static <T> void newAndRunThreads(Class<T> clazz, int num) throws InterruptedException {
        Thread threads[] = new Thread[num];
        for (int i = 0; i < threads.length; i++) {// 建立num个线程
            threads[i] = new VolatileDemo();
        }
        for (int i = 0; i < threads.length; i++) { // 运行刚才建立的num个线程
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) { // num个线程都执行完后继续
            threads[i].join();
        }
    }
}
