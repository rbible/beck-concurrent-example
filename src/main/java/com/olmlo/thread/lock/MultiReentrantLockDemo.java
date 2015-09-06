package com.olmlo.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Main class of the example
 */
public class MultiReentrantLockDemo {

	/**
	 * Main method of the example
	 * 
	 * @param args
	 */
	public static void main(String args[]) throws Exception {

		Lock lock1 = new ReentrantLock();
		Lock lock2 = new ReentrantLock();

		/* Create two tasks */
		Task1 task1 = new Task1(lock1, lock2);
		Task2 task2 = new Task2(lock1, lock2);

		/* Execute the two tasks */
		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task2);

		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();
		/* While the tasks haven't finished, write a message every 500 milliseconds */
		/*
		 * while ((thread1.isAlive()) &&(thread2.isAlive())) { System.out.println("Core: The example is running"); try {
		 * TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException ex) { ex.printStackTrace(); } }
		 */
	}
}

class Task1 implements Runnable {

	/**
	 * Two locks that will be used by the example
	 */
	private Lock lock1, lock2;

	/**
	 * Constructor of the class. Initialize its attributes
	 * 
	 * @param lock1
	 *            A lock used by the class
	 * @param lock2
	 *            A lock used by the class
	 */
	public Task1(Lock lock1, Lock lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}

	/**
	 * Main method of the task
	 */
	@Override
	public void run() {
		lock1.lock();
		System.out.printf("Task 1: Lock 1 locked\n");
		lock2.lock();
		System.out.printf("Task 1: Lock 2 locked\n");
		lock2.unlock();
		lock1.unlock();
	}

}

class Task2 implements Runnable {

	/**
	 * Two locks used by the example
	 */
	private Lock lock1, lock2;

	/**
	 * Constructor for the class. Initialize its attributes
	 * 
	 * @param lock1
	 *            A lock used by the class
	 * @param lock2
	 *            A lock used by the class
	 */
	public Task2(Lock lock1, Lock lock2) {
		this.lock1 = lock1;
		this.lock2 = lock2;
	}

	/**
	 * Main method of the task
	 */
	@Override
	public void run() {
		lock2.lock();
		System.out.printf("Task 2: Lock 2 locked\n");
		lock1.lock();
		System.out.printf("Task 2: Lock 1 locked\n");
		lock1.unlock();
		lock2.unlock();
	}

}
