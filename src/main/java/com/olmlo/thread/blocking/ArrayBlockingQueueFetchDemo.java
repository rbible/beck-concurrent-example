package com.olmlo.thread.blocking;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueueFetchDemo {
	public static void main(String[] args) throws Exception {
		fetchBlocking();
	}

	/**
	 * if ArrayBlockingQueue is empty ;fetch blocking, throw exception
	 */
	public static void fetchBlocking() throws InterruptedException {
		ArrayBlockingQueue<String> names = new ArrayBlockingQueue<String>(1);
		names.put("a");
		System.out.println("put(a) queue size " + names.size());
		names.remove();
		System.out.println("remove queue size " + names.size());

		names.remove();
		System.out.println("remove queue size " + names.size());

		names.put("b");
		System.out.println("put(b) queue size " + names.size());

		System.out.println("程序执行到此...");
	}
}
