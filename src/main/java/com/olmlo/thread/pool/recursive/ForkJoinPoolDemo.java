package com.olmlo.thread.pool.recursive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the example. It creates a list of products, a ForkJoinPool and 
 * a task to execute the actualization of products. 
 */
public class ForkJoinPoolDemo {

    public static void main(String[] args) {

        ProductListGenerator generator = new ProductListGenerator();
        List<Product> products = generator.generate(100);

        ForkJoinTask task = new ForkJoinTask(products, 0, products.size(), 0.20);

        ForkJoinPool pool = new ForkJoinPool();
        pool.execute(task);

        do {
            System.out.printf("Main: Thread Count: %d\n", pool.getActiveThreadCount());
            System.out.printf("Main: Thread Steal: %d\n", pool.getStealCount());
            System.out.printf("Main: Paralelism: %d\n", pool.getParallelism());
            try {
                TimeUnit.MILLISECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!task.isDone());

        pool.shutdown();

        // Check if the task has completed normally
        if (task.isCompletedNormally()) {
            System.out.printf("Main: The process has completed normally.\n");
        }

        // Expected result: 12. Write products which price is not 12
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getPrice() != 12) {
                System.out.printf("Product %s: %f\n", product.getName(), product.getPrice());
            }
        }

        System.out.println("Main: End of the program.\n");
    }
}

class Product {

    /**
     * Name of the product
     */
    private String name;

    /**
     * Price of the product
     */
    private double price;

    /**
     * This method returns the name of the product
     * @return the name of the product
     */
    public String getName() {
        return name;
    }

    /**
     * This method establish the name of the product
     * @param name the name of the product
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns the price of the product
     * @return the price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * This method establish the price of the product
     * @param price the price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }
}

class ProductListGenerator {
    /**
     * This method generates the list of products
     * @param size the size of the product list
     * @return the generated list of products
     */
    public List<Product> generate(int size) {
        List<Product> ret = new ArrayList<Product>();

        for (int i = 0; i < size; i++) {
            Product product = new Product();
            product.setName("Product " + i);
            product.setPrice(10);
            ret.add(product);
        }
        return ret;
    }
}

class ForkJoinTask extends RecursiveAction {

    /**
     * serial version UID. The ForkJoinTask class implements the serializable interface.
     */
    private static final long serialVersionUID = 1L;

    /**
     * List of products
     */
    private List<Product> products;

    /**
     * Fist and Last position of the interval assigned to the task
     */
    private int first;
    private int last;

    /**
     * Increment in the price of products this task has to apply
     */
    private double increment;

    /**
     * Constructor of the class. Initializes its attributes
     * @param products list of products
     * @param first first element of the list assigned to the task
     * @param last last element of the list assigned to the task
     * @param increment price increment that this task has to apply
     */
    public ForkJoinTask(List<Product> products, int first, int last, double increment) {
        this.products = products;
        this.first = first;
        this.last = last;
        this.increment = increment;
    }

    /**
     * Method that implements the job of the task
     */
    @Override
    protected void compute() {
        if (last - first < 10) {
            updatePrices();
        } else {
            int middle = (last + first) / 2;
            System.out.printf("Task: Pending tasks: %s\n", getQueuedTaskCount());
            ForkJoinTask t1 = new ForkJoinTask(products, first, middle + 1, increment);
            ForkJoinTask t2 = new ForkJoinTask(products, middle + 1, last, increment);
            invokeAll(t1, t2);
        }
    }

    /**
     * Method that updates the prices of the assigned products to the task
     */
    private void updatePrices() {
        for (int i = first; i < last; i++) {
            Product product = products.get(i);
            product.setPrice(product.getPrice() * (1 + increment));
        }
    }
}
