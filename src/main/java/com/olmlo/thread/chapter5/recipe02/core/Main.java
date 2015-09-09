package com.olmlo.thread.chapter5.recipe02.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. 
 */
public class Main {

    /**
     * Main method of the class
     */
    public static void main(String[] args) {

        // Generate a document with 100 lines and 1000 words per line
        DocumentMock mock = new DocumentMock();
        String[][] document = mock.generateDocument(100, 1000, "the");

        // Create a DocumentTask
        DocumentTask task = new DocumentTask(document, 0, 100, "the");

        // Create a ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();

        // Execute the Task
        pool.execute(task);

        // Write statistics about the pool
        do {
            System.out.printf("******************************************\n");
            System.out.printf("Main: Parallelism: %d\n", pool.getParallelism());
            System.out.printf("Main: Active Threads: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Task Count: %d\n", pool.getQueuedTaskCount());
            System.out.printf("Main: Steal Count: %d\n", pool.getStealCount());
            System.out.printf("******************************************\n");

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (!task.isDone());

        // Shutdown the pool
        pool.shutdown();

        // Wait for the finalization of the tasks
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write the results of the tasks
        try {
            System.out.printf("Main: The word appears %d in the document", task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}

class LineTask extends RecursiveTask<Integer> {

    /**
     * Serial Version of the class. You have to add it because the
     * ForkJoinTask class implements the serializable interface
     */
    private static final long serialVersionUID = 1L;

    /**
     * A line of the document
     */
    private String line[];

    /**
     * Range of positions the task has to process
     */
    private int start, end;

    /**
     * Word we are looking for
     */
    private String word;

    /**
     * Constructor of the class
     * @param line A line of the document
     * @param start Position of the line where the task starts its process
     * @param end Position of the line where the task starts its process
     * @param word Work we are looking for
     */
    public LineTask(String line[], int start, int end, String word) {
        this.line = line;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    /**
     * If the part of the line it has to process is smaller that 100, it
     * calculates the number of appearances of the word in the block. Else,
     * it divides the block in two blocks and throws to LineTask to calculate
     * the number of appearances.
     */
    @Override
    protected Integer compute() {
        Integer result = null;
        if (end - start < 100) {
            result = count(line, start, end, word);
        } else {
            int mid = (start + end) / 2;
            LineTask task1 = new LineTask(line, start, mid, word);
            LineTask task2 = new LineTask(line, mid, end, word);
            invokeAll(task1, task2);
            try {
                result = groupResults(task1.get(), task2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Groups the results of two LineTasks
     * @param number1 The result of the first LineTask
     * @param number2 The result of the second LineTask
     * @return The sum of the numbers
     */
    private Integer groupResults(Integer number1, Integer number2) {
        Integer result;

        result = number1 + number2;
        return result;
    }

    /**
     * Count the appearances of a word in a part of a line of a document
     * @param line A line of the document
     * @param start Position of the line where the method begin to count
     * @param end Position of the line where the method finish the count
     * @param word Word the method looks for
     * @return The number of appearances of the word in the part of the line
     */
    private Integer count(String[] line, int start, int end, String word) {
        int counter;
        counter = 0;
        for (int i = start; i < end; i++) {
            if (line[i].equals(word)) {
                counter++;
            }
        }
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return counter;
    }

}

class DocumentTask extends RecursiveTask<Integer> {

    /**
     * Serial Version of the class. You have to include it because
     * the ForkJoinTask class implements the Serializable interface
     */
    private static final long serialVersionUID = 1L;

    /**
     * Document to process
     */
    private String document[][];

    /**
     * Range of lines of the document this task has to process
     */
    private int start, end;

    /**
     * Word we are looking for
     */
    private String word;

    /**
     * Constructor of the class
     * @param document Document to process
     * @param start Starting position of the block of the document this task has to process
     * @param end End position of the block of the document this task has to process
     * @param word Word we are looking for
     */
    public DocumentTask(String document[][], int start, int end, String word) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    /**
     * If the task has to process more that ten lines, it divide
     * the block of lines it two subblocks and throws two DocumentTask
     * two process them.
     * In other case, it throws LineTask tasks to process each line of its block
     */
    @Override
    protected Integer compute() {
        Integer result = null;
        if (end - start < 10) {
            result = processLines(document, start, end, word);
        } else {
            int mid = (start + end) / 2;
            DocumentTask task1 = new DocumentTask(document, start, mid, word);
            DocumentTask task2 = new DocumentTask(document, mid, end, word);
            invokeAll(task1, task2);
            try {
                result = groupResults(task1.get(), task2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Throws a LineTask task for each line of the block of lines this task has to process
     * @param document Document to process
     * @param start Starting position of the block of lines it has to process
     * @param end Finish position of the block of lines it has to process
     * @param word Word we are looking for
     * @return
     */
    private Integer processLines(String[][] document, int start, int end, String word) {
        List<LineTask> tasks = new ArrayList<LineTask>();

        for (int i = start; i < end; i++) {
            LineTask task = new LineTask(document[i], 0, document[i].length, word);
            tasks.add(task);
        }
        invokeAll(tasks);

        int result = 0;
        for (int i = 0; i < tasks.size(); i++) {
            LineTask task = tasks.get(i);
            try {
                result = result + task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new Integer(result);
    }

    /**
     * Method that group the results of two DocumentTask tasks
     * @param number1 Result of the first DocumentTask
     * @param number2 Result of the second DocumentTask
     * @return The sum of the two results
     */
    private Integer groupResults(Integer number1, Integer number2) {
        Integer result;

        result = number1 + number2;
        return result;
    }

}

class DocumentMock {

    /**
     * String array with the words of the document
     */
    private String words[] = { "the", "hello", "goodbye", "packt", "java", "thread", "pool", "random", "class", "main" };

    /**
     * Method that generates the String matrix
     * @param numLines Number of lines of the document
     * @param numWords Number of words of the document
     * @param word Word we are going to search for
     * @return The String matrix
     */
    public String[][] generateDocument(int numLines, int numWords, String word) {

        int counter = 0;
        String document[][] = new String[numLines][numWords];
        Random random = new Random();
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < numWords; j++) {
                int index = random.nextInt(words.length);
                document[i][j] = words[index];
                if (document[i][j].equals(word)) {
                    counter++;
                }
            }
        }
        System.out.printf("DocumentMock: The word appears %d times in the document.\n", counter);
        return document;
    }
}
