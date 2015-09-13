package com.olmlo.thread.app;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;

public class ThreadStateDemo {
    // private static String fileName = ".\\data\\log.txt";
    private static String fileName = "d:\\log.txt";

    public static void main(String[] args) {

        System.out.printf("Minimum Priority: %s\n", Thread.MIN_PRIORITY);
        System.out.printf("Normal Priority: %s\n", Thread.NORM_PRIORITY);
        System.out.printf("Maximun Priority: %s\n", Thread.MAX_PRIORITY);

        Thread threads[];
        Thread.State status[];

        threads = new Thread[10];
        status = new Thread.State[10];
        setPriority(threads);

        try (FileWriter file = new FileWriter(fileName); PrintWriter pw = new PrintWriter(file);) {

            threadsState(threads, status, pw);
            threadsStart(threads);

            boolean finish = false;
            while (!finish) {
                writeThreadStateChangeInfo(threads, status, pw);
                finish = true;
                finish = checkFinishState(threads, finish);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkFinishState(Thread[] threads, boolean finish) {
        for (int i = 0; i < 10; i++) {
            finish = finish && (threads[i].getState() == State.TERMINATED);
        }
        return finish;
    }

    private static void writeThreadStateChangeInfo(Thread[] threads, Thread.State[] status, PrintWriter pw) {
        for (int i = 0; i < 10; i++) {
            if (threads[i].getState() != status[i]) {
                writeThreadInfo(pw, threads[i], status[i]);
                status[i] = threads[i].getState();
            }
        }
    }

    private static void threadsState(Thread[] threads, Thread.State[] status, PrintWriter pw) {
        for (int i = 0; i < 10; i++) {
            pw.println("Main : Status of Thread " + i + " : " + threads[i].getState());
            status[i] = threads[i].getState();
        }
    }

    private static void threadsStart(Thread[] threads) {
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
    }

    private static void setPriority(Thread[] threads) {
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator(i));
            if ((i % 2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("Thread " + i);
        }
    }

    private static void writeThreadInfo(PrintWriter pw, Thread thread, State state) {
        pw.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
        pw.printf("Main : Priority: %d\n", thread.getPriority());
        pw.printf("Main : Old State: %s\n", state);
        pw.printf("Main : New State: %s\n", thread.getState());
        pw.printf("Main : ************************************\n");
    }
}

class Calculator implements Runnable {
    private int number;

    public Calculator(int number) {
        this.number = number;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.printf("%s: %d * %d = %d\n", Thread.currentThread().getName(), number, i, i * number);
        }
    }
}
