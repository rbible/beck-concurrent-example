package com.olmlo.thread.sync;

import com.olmlo.thread.util.ThreadUtils;

public class JoinThreadDemo1 extends Thread {

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
        int t = 1000;
        int p = 0;
        while (t == 1000 && p < 1000) {
            System.out.println("n=" + JoinThreadDemo1.n);
            ThreadUtils.newAndRunThreads(JoinThreadDemo1.class, 100);

            System.out.println("n=" + JoinThreadDemo1.n);
            t = JoinThreadDemo1.n;
            JoinThreadDemo1.n = 0;
            p++;
        }

        System.out.println("n=" + JoinThreadDemo1.n);
        System.out.println("p=" + p);
    }

}
