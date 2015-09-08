package com.olmlo.thread.communication;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example creates all the necessary objects and throws the tasks
 *
 */
public class CompletionServiceDemo {

    public static void main(String[] args) {
        // Create the executor and thee CompletionService using that executor
        ExecutorService executor = Executors.newCachedThreadPool();
        CompletionService<String> service = new ExecutorCompletionService<>(executor);

        // Crete two ReportRequest objects and two Threads to execute them
        Thread faceThread = new Thread(new ReportRequest("Face", service));
        Thread onlineThread = new Thread(new ReportRequest("Online", service));

        // Create a ReportSender object and a Thread to execute it
        ReportProcessor processor = new ReportProcessor(service);
        Thread senderThread = new Thread(processor);

        // Start the Threads
        System.out.printf("Main: Starting the Threads\n");
        faceThread.start();
        onlineThread.start();
        senderThread.start();

        // Wait for the end of the ReportGenerator tasks
        try {
            System.out.printf("Main: Waiting for the report generators.\n");
            faceThread.join();
            onlineThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutdown the executor
        System.out.printf("Main: Shuting down the executor.\n");
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // End the execution of the ReportSender
        processor.setEnd(true);
        System.out.printf("Main: Ends\n");
    }
}

class ReportGenerator implements Callable<String> {

    /**
     * The sender of the report
     */
    private String sender;
    /**
     * The title of the report
     */
    private String title;

    /**
     * Constructor of the class. Initializes the two attributes
     * @param sender The sender of the report
     * @param title The title of the report
     */
    public ReportGenerator(String sender, String title) {
        this.sender = sender;
        this.title = title;
    }

    /**
     * Main method of the ReportGenerator. Waits a random period of time
     * and then generates the report as a String.
     */
    @Override
    public String call() throws Exception {
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("%s_%s: ReportGenerator: Generating a report during %d seconds\n", this.sender, this.title, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String ret = sender + ": " + title;
        return ret;
    }
}

class ReportProcessor implements Runnable {

    /**
     * CompletionService that executes the ReportGenerator tasks
     */
    private CompletionService<String> service;
    /**
     * Variable to store the status of the Object. It will executes until the variable
     * takes the true value
     */
    private boolean end;

    /**
     * Constructor of the class. It initializes the attributes of the class
     * @param service The CompletionService used to execute the ReportGenerator tasks
     */
    public ReportProcessor(CompletionService<String> service) {
        this.service = service;
        end = false;
    }

    /**
     * Main method of the class. While the variable end is false, it
     * calls the poll method of the CompletionService and waits 20 seconds
     * for the end of a ReportGenerator task
     */
    @Override
    public void run() {
        while (!end) {
            try {
                Future<String> result = service.poll(20, TimeUnit.SECONDS);
                if (result != null) {
                    String report = result.get();
                    System.out.printf("ReportReceiver: Report Recived: %s\n", report);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("ReportSender: End\n");
    }

    /**
     * Method that establish the value of the end attribute
     * @param end New value of the end attribute.
     */
    public void setEnd(boolean end) {
        this.end = end;
    }

}

class ReportRequest implements Runnable {

    /**
     * Name of this ReportRequest
     */
    private String name;

    /**
     * CompletionService used for the execution of the ReportGenerator tasks
     */
    private CompletionService<String> service;

    /**
     * Constructor of the class. Initializes the parameters
     * @param name Name of the ReportRequest
     * @param service Service used for the execution of tasks
     */
    public ReportRequest(String name, CompletionService<String> service) {
        this.name = name;
        this.service = service;
    }

    /**
     * Main method of the class. Create three ReportGenerator tasks and executes them
     * through a CompletionService
     */
    @Override
    public void run() {
        ReportGenerator reportGenerator = new ReportGenerator(name, "Report");
        service.submit(reportGenerator);
    }
}