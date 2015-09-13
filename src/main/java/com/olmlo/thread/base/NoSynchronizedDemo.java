package com.olmlo.thread.base;

/**
 * Main class of the example. It creates an account, a company and a bank
 * to work with the account. The final balance should be equal to the initial, but....
 *
 */
public class NoSynchronizedDemo {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {
        // Creates a new account ...
        Account2 account = new Account2();
        // an initialize its balance to 1000
        account.setBalance(1000);

        // Creates a new Company and a Thread to run its task
        Company2 company = new Company2(account);
        Thread companyThread = new Thread(company);
        // Creates a new Bank and a Thread to run its task
        Bank2 bank = new Bank2(account);
        Thread bankThread = new Thread(bank);

        // Prints the initial balance
        System.out.printf("Account : Initial Balance: %f\n", account.getBalance());

        // Starts the Threads
        companyThread.start();
        bankThread.start();

        try {
            // Wait for the finalization of the Threads
            companyThread.join();
            bankThread.join();
            // Print the final balance
            System.out.printf("Account : Final Balance: %f\n", account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Company2 implements Runnable {

    /**
     * The account affected by the operations
     */
    private Account2 account;

    /**
     * Constructor of the class. Initializes the account
     * @param account the account affected by the operations
     */
    public Company2(Account2 account) {
        this.account = account;
    }

    /**
     * Core method of the Runnable
     */
    public void run() {
        for (int i = 0; i < 100; i++) {
            account.addAmount(1000);
        }
    }

}

class Bank2 implements Runnable {

    /**
     * The account affected by the operations
     */
    private Account2 account;

    /**
     * Constructor of the class. Initializes the account
     * @param account The account affected by the operations
     */
    public Bank2(Account2 account) {
        this.account = account;
    }

    /**
     * Core method of the Runnable
     */
    public void run() {
        for (int i = 0; i < 100; i++) {
            account.subtractAmount(1000);
        }
    }
}

class Account2 {

    /**
     * Balance of the bank account
     */
    private double balance;

    /**
     * Returns the balance of the account
     * @return the balance of the account
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Establish the balance of the account
     * @param balance the new balance of the account
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Add an import to the balance of the account
     * @param amount import to add to the balance
     */
    public void addAmount(double amount) {
        double tmp = balance;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tmp += amount;
        balance = tmp;
    }

    /**
     * Subtract an import to the balance of the account
     * @param amount import to subtract to the balance
     */
    public void subtractAmount(double amount) {
        double tmp = balance;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tmp -= amount;
        balance = tmp;
    }

}
