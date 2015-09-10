package com.olmlo.thread.chapter8.recipe07.core;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Main class of the example. It launch five Task objects and write some log messages indicating the evolution of the
 * execution of the program
 */
public class Main {

    public static void main(String[] args) {

        /* Get the Logger object */
        Logger logger = MyLogger.getLogger("Core");

        /* Write a message indicating the start of the execution */
        logger.entering("Core", "main()", args);

        /* Create and launch five Task objects */
        Thread threads[] = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            logger.log(Level.INFO, "Launching thread: " + i);
            Task task = new Task();
            threads[i] = new Thread(task);
            logger.log(Level.INFO, "Thread created: " + threads[i].getName());
            threads[i].start();
        }

        /* Write a log message indicating that the threads have been created */
        logger.log(Level.INFO, "Ten Threads created. Waiting for its finalization");

        /* Wait for the finalization of the threads */
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
                logger.log(Level.INFO, "Thread has finished its execution", threads[i]);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Exception", e);
            }
        }

        /* Write a log message indicating the end of the program */
        logger.exiting("Main", "main()");
    }

}

class MyFormatter extends Formatter {

    /**
     * Method that formats the log message. It's declared as abstract in the Formatter class. It's called by the Logger
     * class. It receives a LogRecord object as parameter with all the information of the log message
     */
    @Override
    public String format(LogRecord record) {

        /*
         * Create a string buffer to construct the message.
         */
        StringBuilder sb = new StringBuilder();

        /*
         * Add the parts of the message with the desired format.
         */
        sb.append("[" + record.getLevel() + "] - ");
        sb.append(new Date(record.getMillis()) + " : ");
        sb.append(record.getSourceClassName() + "." + record.getSourceMethodName() + " : ");
        sb.append(record.getMessage() + "\n");

        /*
         * Convert the string buffer to string and return it
         */
        return sb.toString();
    }
}

class MyLogger {

    /**
     * Handler to control that the log messages are written in the recipe8.log file
     */
    private static Handler handler;

    /**
     * Static method that returns the log object associated with the name received as parameter. If it's a new Logger
     * object, this method configures it with your configuration.
     * 
     * @param name
     *            Name of the Logger object you want to obtain.
     * @return The Logger object generated.
     */
    public static Logger getLogger(String name) {
        /*
         * Get the logger
         */
        Logger logger = Logger.getLogger(name);
        /*
         * Set the level to show all the messages
         */
        logger.setLevel(Level.ALL);
        try {
            /*
             * If the Handler object is null, we create one to write the log messages in the recipe8.log file with the
             * format specified by the MyFormatter class
             */
            if (handler == null) {
                handler = new FileHandler("recipe8.log");
                Formatter format = new MyFormatter();
                handler.setFormatter(format);
            }
            /*
             * If the Logger object hasn't handler, we add the Handler object to it
             */
            if (logger.getHandlers().length == 0) {
                logger.addHandler(handler);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
         * Return the Logger object.
         */
        return logger;
    }

}

class Task implements Runnable {

    /**
     * Main method of the task
     */
    @Override
    public void run() {
        /* Get the Logger */
        Logger logger = MyLogger.getLogger(this.getClass().getName());

        /* Write a message indicating the start of the task */
        logger.entering(Thread.currentThread().getName(), "run()");

        /* Sleep the task for two seconds */
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /* Write a message indicating the end of the task */
        logger.exiting(Thread.currentThread().getName(), "run()", Thread.currentThread());
    }
}
