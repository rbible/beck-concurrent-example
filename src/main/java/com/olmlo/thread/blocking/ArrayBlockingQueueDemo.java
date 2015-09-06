package com.olmlo.thread.blocking;

import java.util.concurrent.ArrayBlockingQueue;

public class ArrayBlockingQueueDemo {
	public static void main(String[] args) throws Exception {
		ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(10);
		producerConsumerRun(arrayBlockingQueue);
	}

	/**
	 * @作用 此方法用来测试生产者和消费者 为了让程序在获取不到元素时不报错有两种方式：
	 * 
	 *     1.让生产者的生产速度大于消费者的消费速度
	 * 
	 *     2.在消费者获取资源出错时让消费者线程暂停一段时间，不输出错误。
	 * @param abq
	 */
	public static void producerConsumerRun(ArrayBlockingQueue<String> abq) {
		Thread tConsumer = new Consumer(abq);
		Thread tProducer = new Producer(abq);
		tConsumer.start();
		tProducer.start();
	}

}

/**
 * @作用 定义消费者
 */
class Consumer extends Thread {
	ArrayBlockingQueue<String> abq = null;

	public Consumer(ArrayBlockingQueue<String> abq) {
		super();
		this.abq = abq;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
				String msg = abq.remove();
				System.out.println("取数据：====" + msg + "\t剩余数据量：" + abq.size());
			} catch (Exception e) {
				System.out.println(" no data");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}

/**
 * @作用 定义生产者
 */
class Producer extends Thread {
	ArrayBlockingQueue<String> abq = null;

	public Producer(ArrayBlockingQueue<String> abq) {
		this.abq = abq;
	}

	@Override
	public void run() {
		int i = 0;
		while (true) {
			try {
				Thread.sleep(1000);
				abq.put("" + i);
				System.out.println("存放数据：====" + i + "\t剩余数据量：" + abq.size());
				i++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}