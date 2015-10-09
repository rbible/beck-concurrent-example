package com.olmlo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberAlternate {

	public static void main(String[] args) {
		AtomicInteger i = new AtomicInteger(0);

		new Thread(new Classa(i)).start();
		new Thread(new Classb(i)).start();
		new Thread(new Classa(i)).start();
		new Thread(new Classb(i)).start();
		new Thread(new Classa(i)).start();
		new Thread(new Classb(i)).start();

		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("finish \n");
	}

}

class Classa implements Runnable {
	AtomicInteger i;

	public Classa(AtomicInteger i) {
		this.i = i;

	}

	@Override
	public void run() {

		synchronized (i) {
			while (true) {
				int temp = i.get() / 3;
				if (temp % 2 == 0) {
					System.out.printf(Thread.currentThread() + " " + i.getAndIncrement() + "\n");
				} else {
					System.out.println(Thread.currentThread() + "  over");
					return;
				}
			}
		}

	}
}

class Classb implements Runnable {
	AtomicInteger i;

	public Classb(AtomicInteger i) {
		this.i = i;
	}

	@Override
	public void run() {
		synchronized (i) {
			while (true) {
				int temp = i.get() / 3;
				if (temp % 2 == 1) {
					System.out.printf(Thread.currentThread() + " " + i.getAndIncrement() + "\n");
				} else {
					System.out.println(Thread.currentThread() + "  over");
					return;
				}
			}
		}
	}
}
