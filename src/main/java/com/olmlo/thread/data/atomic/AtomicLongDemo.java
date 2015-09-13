package com.olmlo.thread.data.atomic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Main class of the example. It creates an account, a company and a bank
 * to work with the account. The final balance should be equal to the initial, but....
 *
 */
public class AtomicLongDemo {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {
        // Creates a new account ...
        Account account = new Account();
        // an initialize its balance to 1000
        account.setBalance(1000);

        // Creates a new Company and a Thread to run its task
        Company company = new Company(account);
        Thread companyThread = new Thread(company);
        // Creates a new Bank and a Thread to run its task
        Bank bank = new Bank(account);
        Thread bankThread = new Thread(bank);

        // Prints the initial balance
        System.out.printf("Account : Initial Balance: %d\n", account.getBalance());

        // Starts the Threads
        companyThread.start();
        bankThread.start();

        try {
            // Wait for the finalization of the Threads
            companyThread.join();
            bankThread.join();
            // Print the final balance
            System.out.printf("Account : Final Balance: %d\n", account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Account {

    /**
     * Balance of the bank account
     */
    private AtomicLong balance;

    public Account() {
        balance = new AtomicLong();
    }

    /**
     * Returns the balance of the account
     * @return the balance of the account
     */
    public long getBalance() {
        return balance.get();
    }

    /**
     * Establish the balance of the account
     * @param balance the new balance of the account
     */
    public void setBalance(long balance) {
        this.balance.set(balance);
    }

    /**
     * Add an import to the balance of the account
     * @param amount import to add to the balance
     */
    public void addAmount(long amount) {
        this.balance.getAndAdd(amount);
    }

    /**
     * Subtract an import to the balance of the account
     * @param amount import to subtract to the balance
     */
    public void subtractAmount(long amount) {
        this.balance.getAndAdd(-amount);
    }

}

class Bank implements Runnable {

    /**
     * The account affected by the operations
     */
    private Account account;

    /**
     * Constructor of the class. Initializes the account
     * @param account The account affected by the operations
     */
    public Bank(Account account) {
        this.account = account;
    }

    /**
     * Core method of the Runnable
     */
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            account.subtractAmount(1000);
        }
    }

}

class Company implements Runnable {

    /**
     * The account affected by the operations
     */
    private Account account;

    /**
     * Constructor of the class. Initializes the account
     * @param account the account affected by the operations
     */
    public Company(Account account) {
        this.account = account;
    }

    /**
     * Core method of the Runnable
     */
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            account.addAmount(1000);
        }
    }

}
