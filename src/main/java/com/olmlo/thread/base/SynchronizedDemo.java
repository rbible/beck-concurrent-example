package com.olmlo.thread.base;

/**
 * Main class of the example. It creates an account, a company and a bank
 * to work with the account. The final balance is equal to the initial.
 *
 */
public class SynchronizedDemo {

    public static void main(String[] args) {

        Account1 account = new Account1();
        account.setBalance(1000);

        Company1 company = new Company1(account);
        Thread companyThread = new Thread(company);

        Bank1 bank = new Bank1(account);
        Thread bankThread = new Thread(bank);

        System.out.printf("Account : Initial Balance: %f\n", account.getBalance());

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

class Company1 implements Runnable {
    /**
     * The account affected by the operations
     */
    private Account1 account;

    /**
     * Constructor of the class. Initializes the account
     * @param account the account affected by the operations
     */
    public Company1(Account1 account) {
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

class Bank1 implements Runnable {

    /**
     * The account affected by the operations
     */
    private Account1 account;

    /**
     * Constructor of the class. Initializes the account
     * @param account The account affected by the operations
     */
    public Bank1(Account1 account) {
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

class Account1 {

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
     * @param amount the import to add to the balance of the account
     */
    public synchronized void addAmount(double amount) {
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
     * @param amount the import to subtract to the balance of the account 
     */
    public synchronized void subtractAmount(double amount) {
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
