package com.olmlo.thread.sync.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class of the example. It launch ten Task objects
 *
 */
public class ReentrantLockDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* Create a Lock */
		ReentrantLock lock = new ReentrantLock();

		/*
		 * Executes the threads. There is a problem with this block of code. It uses the run() method instead of the
		 * start() method.
		 */
		for (int i = 0; i < 10; i++) {
			Task task = new Task(lock);
			Thread thread = new Thread(task);
			thread.run();
		}

	}
}

class Task implements Runnable {

	private ReentrantLock lock;

	public Task(ReentrantLock lock) {
		this.lock = lock;
	}

	/**
	 * Main method of the task.
	 */
	@Override
	public void run() {
		lock.lock();

		try {
			System.out.printf("thread : %s \n", Thread.currentThread().getThreadGroup());
			TimeUnit.SECONDS.sleep(1);
			/*
			 * There is a problem with this unlock. If the thread is interrupted while it is sleeping, the lock won't be
			 * unlocked and it will cause that the threads that are waiting for this block will be blocked and never
			 * will get the control of the Lock.
			 */
			lock.unlock();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
