package com.olmlo.thread.communication;

import java.util.Date;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example 
 *
 */
public class PhaserDemo2 {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {

        // Creates the Phaser
        MyPhaser phaser = new MyPhaser();

        // Creates 5 students and register them in the phaser
        Student students[] = new Student[5];
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student(phaser);
            phaser.register();
        }

        // Create 5 threads for the students and start them
        Thread threads[] = new Thread[students.length];
        for (int i = 0; i < students.length; i++) {
            threads[i] = new Thread(students[i], "Student " + i);
            threads[i].start();
        }

        // Wait for the finalization of the threads
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check that the Phaser is in the Terminated state
        System.out.printf("Main: The phaser has finished: %s.\n", phaser.isTerminated());

    }

}

class MyPhaser extends Phaser {

    /**
     * This method is called when the last register thread calls one of the advance methods
     * in the actual phase
     * @param phase Actual phase
     * @param registeredParties Number of registered threads
     * @return false to advance the phase, true to finish
     */
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch (phase) {
        case 0:
            return studentsArrived();
        case 1:
            return finishFirstExercise();
        case 2:
            return finishSecondExercise();
        case 3:
            return finishExam();
        default:
            return true;
        }
    }

    /**
     * This method is called in the change from phase 0 to phase 1
     * @return false to continue with the execution
     */
    private boolean studentsArrived() {
        System.out.printf("Phaser: The exam are going to start. The students are ready.\n");
        System.out.printf("Phaser: We have %d students.\n", getRegisteredParties());
        return false;
    }

    /**
     * This method is called in the change from phase 1 to phase 2
     * @return false to continue with the execution
     */
    private boolean finishFirstExercise() {
        System.out.printf("Phaser: All the students has finished the first exercise.\n");
        System.out.printf("Phaser: It's turn for the second one.\n");
        return false;
    }

    /**
     * This method is called in the change form phase 2 to phase 3
     * @return false to continue with the execution
     */
    private boolean finishSecondExercise() {
        System.out.printf("Phaser: All the students has finished the second exercise.\n");
        System.out.printf("Phaser: It's turn for the third one.\n");
        return false;
    }

    /**
     * This method is called in the change from phase 3 to phase 4
     * @return true. There are no more phases
     */
    private boolean finishExam() {
        System.out.printf("Phaser: All the students has finished the exam.\n");
        System.out.printf("Phaser: Thank you for your time.\n");
        return true;
    }

}

class Student implements Runnable {

    /**
     * Phaser to control the execution
     */
    private Phaser phaser;

    /**
     * Constructor of the class. Initialize its objects
     * @param phaser Phaser to control the execution
     */
    public Student(Phaser phaser) {
        this.phaser = phaser;
    }

    /**
     * Main method of the student. It arrives to the exam and does three exercises. After each
     * exercise, it calls the phaser to wait that all the students finishes the same exercise
     */
    public void run() {
        System.out.printf("%s: Has arrived to do the exam. %s\n", Thread.currentThread().getName(), new Date());
        phaser.arriveAndAwaitAdvance();
        System.out.printf("%s: Is going to do the first exercise. %s\n", Thread.currentThread().getName(), new Date());
        doExercise1();
        System.out.printf("%s: Has done the first exercise. %s\n", Thread.currentThread().getName(), new Date());
        phaser.arriveAndAwaitAdvance();
        System.out.printf("%s: Is going to do the second exercise. %s\n", Thread.currentThread().getName(), new Date());
        doExercise2();
        System.out.printf("%s: Has done the second exercise. %s\n", Thread.currentThread().getName(), new Date());
        phaser.arriveAndAwaitAdvance();
        System.out.printf("%s: Is going to do the third exercise. %s\n", Thread.currentThread().getName(), new Date());
        doExercise3();
        System.out.printf("%s: Has finished the exam. %s\n", Thread.currentThread().getName(), new Date());
        phaser.arriveAndAwaitAdvance();
    }

    /**
     * Does an exercise is to wait a random time 
     */
    private void doExercise1() {
        try {
            Long duration = (long) (Math.random() * 10);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does an exercise is wait a random time 
     */
    private void doExercise2() {
        try {
            Long duration = (long) (Math.random() * 10);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does an exercise is wait a random time 
     */
    private void doExercise3() {
        try {
            Long duration = (long) (Math.random() * 10);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
