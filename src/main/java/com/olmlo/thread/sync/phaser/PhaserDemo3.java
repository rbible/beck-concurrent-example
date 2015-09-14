package com.olmlo.thread.sync.phaser;

import java.util.concurrent.Phaser;

public class PhaserDemo3 {

    public static void main(String args[]) throws Exception {
        final int phaseToTerminate = 3;
        final int count = 5;
        final Phaser phaser = new Phaser(count) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                System.out.println("====== " + phase + " ======");
                return phase >= phaseToTerminate || registeredParties == 0;
            }
        };

        for (int i = 0; i < count; i++) {
            System.out.println("starting thread, id: " + i);
            final Thread thread = new Thread(new Task(i, phaser));
            thread.start();
        }
    }

    public static class Task implements Runnable {
        //
        private final int id;
        private final Phaser phaser;

        public Task(int id, Phaser phaser) {
            this.id = id;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // NOP
                }
                System.out.println("in Task.run(), phase: " + phaser.getPhase() + ", id: " + this.id);
                phaser.arriveAndAwaitAdvance();
            } while (!phaser.isTerminated());
        }
    }
}