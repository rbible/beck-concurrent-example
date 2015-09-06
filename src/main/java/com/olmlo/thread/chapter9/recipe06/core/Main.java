package com.olmlo.thread.chapter9.recipe06.core;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example
 */
public class Main {

	public static void main(String[] args) {

		OneSecondLongTask task = new OneSecondLongTask();
		Handler handler = new Handler();
		AlwaysThrowsExceptionWorkerThreadFactory factory = new AlwaysThrowsExceptionWorkerThreadFactory();
		ForkJoinPool pool = new ForkJoinPool(2, factory, handler, false);
		
		pool.execute(task);

		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.printf("Main: The program has finished.\n");
	}
}
