package com.olmlo.thread.chapter9.recipe03.core;

import java.util.concurrent.LinkedTransferQueue;

/**
 * This class implements a Producer of Strings. It generates 10000 strings and stores them in the buffer.
 *
 */
public class Producer implements Runnable {

	/**
	 * Buffer to store the Strings
	 */
	private LinkedTransferQueue<String> buffer;

	/**
	 * Name of the producer
	 */
	private String name;

	/**
	 * Constructor of the class. Initializes its parameters
	 * 
	 * @param name
	 *            Name of the producer
	 * @param buffer
	 *            Buffer to store the objects
	 */
	public Producer(String name, LinkedTransferQueue<String> buffer) {
		this.name = name;
		this.buffer = buffer;
	}

	/**
	 * Main method of the producer. Generates 10000 of Strings and stores them in the buffer
	 */
	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			buffer.put(name + ": Element " + i);
		}
		System.out.printf("Producer: %s: Producer done size:%d \n", name, buffer.size());
	}
}
