package com.olmlo.thread.base;

public class SynchronizedDemo3 extends Thread {

    public static int n = 0;

    public static synchronized void inc() {
        n++;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                inc();// n=n+1改成了inc()
                sleep(3);// 为了使运行结果更随即，延迟3毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int t = 100;
        int p = 0;
        while (t == 100 && p < 100) {
            System.out.println("n=" + SynchronizedDemo3.n);
            ThreadUtils2.newAndRunThreads(SynchronizedDemo3.class, 10);

            System.out.println("n=" + SynchronizedDemo3.n);
            t = SynchronizedDemo3.n;
            SynchronizedDemo3.n = 0;
            p++;
        }

        System.out.println("n=" + SynchronizedDemo3.n);
        System.out.println("p=" + p);
    }
}

class ThreadUtils2 {
    public static <T> void newAndRunThreads(Class<T> clazz, int num) throws InterruptedException {
        Thread threads[] = new Thread[num];
        for (int i = 0; i < threads.length; i++) {// 建立num个线程
            threads[i] = new SynchronizedDemo3();
        }
        for (int i = 0; i < threads.length; i++) { // 运行刚才建立的num个线程
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) { // num个线程都执行完后继续
            threads[i].join();
        }
    }
}
