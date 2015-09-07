package com.olmlo.thread.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Main class of the example
 *
 */
public class ExchangerDemo2 {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {

        // Creates two buffers
        List<String> buffer1 = new ArrayList<>();
        List<String> buffer2 = new ArrayList<>();

        // Creates the exchanger
        Exchanger<List<String>> exchanger = new Exchanger<>();

        // Creates the producer
        Producer producer = new Producer(buffer1, exchanger);
        // Creates the consumer
        Consumer consumer = new Consumer(buffer2, exchanger);

        // Creates and starts the threads
        Thread threadProducer = new Thread(producer);
        Thread threadConsumer = new Thread(consumer);

        threadProducer.start();
        threadConsumer.start();

    }

}

class Consumer implements Runnable {

    /**
     * Buffer to save the events produced
     */
    private List<String> buffer;

    /**
     * Exchager to synchronize with the consumer
     */
    private final Exchanger<List<String>> exchanger;

    /**
     * Constructor of the class. Initializes its attributes
     * @param buffer Buffer to save the events produced
     * @param exchanger Exchanger to syncrhonize with the consumer
     */
    public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    /**
     * Main method of the producer. It consumes all the events produced by the Producer. After
     * processes ten events, it uses the exchanger object to synchronize with 
     * the producer. It sends to the producer an empty buffer and receives a buffer with ten events
     */
    @Override
    public void run() {
        int cycle = 1;

        for (int i = 0; i < 10; i++) {
            System.out.printf("Consumer: Cycle %d\n", cycle);

            try {
                // Wait for the produced data and send the empty buffer to the producer
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Consumer: %d\n", buffer.size());

            for (int j = 0; j < 10; j++) {
                String message = buffer.get(0);
                System.out.printf("Consumer: %s\n", message);
                buffer.remove(0);
            }
            cycle++;
        }
    }

}

class Producer implements Runnable {

    /**
     * Buffer to save the events produced
     */
    private List<String> buffer;

    /**
     * Exchager to synchronize with the consumer
     */
    private final Exchanger<List<String>> exchanger;

    /**
     * Constructor of the class. Initializes its attributes
     * @param buffer Buffer to save the events produced
     * @param exchanger Exchanger to syncrhonize with the consumer
     */
    public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
        this.buffer = buffer;
        this.exchanger = exchanger;
    }

    /**
     * Main method of the producer. It produces 100 events. 10 cicles of 10 events.
     * After produce 10 events, it uses the exchanger object to synchronize with 
     * the consumer. The producer sends to the consumer the buffer with ten events and
     * receives from the consumer an empty buffer
     */
    @Override
    public void run() {
        int cycle = 1;

        for (int i = 0; i < 10; i++) {
            System.out.printf("Producer: Cycle %d\n", cycle);

            for (int j = 0; j < 10; j++) {
                String message = "Event " + ((i * 10) + j);
                System.out.printf("Producer: %s\n", message);
                buffer.add(message);
            }

            try {
                /* Change the data buffer with the consumer */
                buffer = exchanger.exchange(buffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.printf("Producer: %d\n", buffer.size());

            cycle++;
        }

    }
}
