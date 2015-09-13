package com.olmlo.thread.app;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 *  Main class of the example. Search for the autoexect.bat file
 *  on the Windows root folder and its subfolders during ten seconds
 *  and then, interrupts the Thread
 */
public class FileSearchDemo1 {

    /**
     * Main method of the core. Search for the autoexect.bat file
     * on the Windows root folder and its subfolders during ten seconds
     * and then, interrupts the Thread
     * @param args
     */
    public static void main(String[] args) {
        // Creates the Runnable object and the Thread to run it
        FileSearch searcher = new FileSearch("C:\\", "AMTAG.BIN");
        Thread thread = new Thread(searcher);

        // Starts the Thread
        thread.start();

        // Wait for ten seconds
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrupts the thread
        thread.interrupt();
    }

}

class FileSearch implements Runnable {

    /**
     * Initial path for the search
     */
    private String initPath;
    /**
     * Name of the file we are searching for
     */
    private String fileName;

    /**
     * Constructor of the class
     * 
     * @param initPath
     *            : Initial path for the search
     * @param fileName
     *            : Name of the file we are searching for
     */
    public FileSearch(String initPath, String fileName) {
        this.initPath = initPath;
        this.fileName = fileName;
    }

    /**
     * Main method of the class
     */
    @Override
    public void run() {
        File file = new File(initPath);
        if (file.isDirectory()) {
            try {
                directoryProcess(file);
            } catch (InterruptedException e) {
                System.out.printf("%s: The search has been interrupted", Thread.currentThread().getName());
                cleanResources();
            }
        }
    }

    /**
     * Method for cleaning the resources. In this case, is empty
     */
    private void cleanResources() {

    }

    /**
     * Method that process a directory
     * 
     * @param file
     *            : Directory to process
     * @throws InterruptedException
     *             : If the thread is interrupted
     */
    private void directoryProcess(File file) throws InterruptedException {

        // Get the content of the directory
        File list[] = file.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // If is a directory, process it
                    directoryProcess(list[i]);
                } else {
                    // If is a file, process it
                    fileProcess(list[i]);
                }
            }
        }
        // Check the interruption
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * Method that process a File
     * 
     * @param file
     *            : File to process
     * @throws InterruptedException
     *             : If the thread is interrupted
     */
    private void fileProcess(File file) throws InterruptedException {
        // Check the name
        if (file.getName().equals(fileName)) {
            System.out.printf("%s : %s\n", Thread.currentThread().getName(), file.getAbsolutePath());
        }

        // Check the interruption
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}
