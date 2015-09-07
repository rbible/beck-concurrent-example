package com.olmlo.thread.util;

import com.olmlo.thread.sync.JoinThreadDemo1;

public class ThreadUtils {

    public static <T> void newAndRunThreads(Class<T> clazz, int num) throws InterruptedException {
        Thread threads[] = new Thread[num];
        for (int i = 0; i < threads.length; i++) {// 建立num个线程
            threads[i] = new JoinThreadDemo1();
        }
        for (int i = 0; i < threads.length; i++) { // 运行刚才建立的num个线程
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) { // num个线程都执行完后继续
            threads[i].join();
        }
    }
}
