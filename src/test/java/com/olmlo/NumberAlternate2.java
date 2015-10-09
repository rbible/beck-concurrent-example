package com.olmlo;

import java.util.concurrent.atomic.AtomicInteger;

public class NumberAlternate2 {

	public static void main(String[] args) {
		AtomicInteger i = new AtomicInteger(0);

		// Classa a = new Classa(i);
		Thread a1 = new Thread(new Classb2(i));
		Thread a2 = new Thread(new Classb2(i));
		Thread a3 = new Thread(new Classb2(i));

		Thread t1 = new Thread(new Classa2(i));
		Thread t2 = new Thread(new Classa2(i));
		Thread t3 = new Thread(new Classa2(i));

		a1.setName("Thread1");
		a2.setName("Thread1");
		a3.setName("Thread1");
		t1.setName("Thread2");
		t2.setName("Thread2");
		t3.setName("Thread2");

		a1.start();
		t1.start();

		a2.start();
		t2.start();

		a3.start();
		t3.start();
		
		try {
			a1.join();
			a2.join();
			a3.join();
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.printf("finish \n");
	}

}

class Classa2 implements Runnable {
	AtomicInteger i;

	public Classa2(AtomicInteger i) {
		this.i = i;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (i) {
				int temp = i.get() / 3;
				if (temp % 2 == 1) {
					System.out.printf("thread %s : %d \n", Thread.currentThread().getName(), i.getAndIncrement());
				} else {
					System.out.printf("\n");
					return;
				}
			}
		}
	}
}

class Classb2 implements Runnable {
	AtomicInteger i;

	public Classb2(AtomicInteger i) {
		this.i = i;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (i) {
				int temp = i.get() / 3;
				if (temp % 2 == 0) {
					System.out.printf("thread %s : %d \n", Thread.currentThread().getName(), i.getAndIncrement());
				} else {
					System.out.printf("\n");
					return;
				}
			}
		}
	}
}
