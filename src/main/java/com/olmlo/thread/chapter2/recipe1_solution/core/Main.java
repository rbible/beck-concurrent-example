package com.olmlo.thread.chapter2.recipe1_solution.core;

/**
 * Main class of the example. It creates an account, a company and a bank
 * to work with the account. The final balance is equal to the initial.
 *
 */
public class Main {

    public static void main(String[] args) {

        Account account = new Account();
        account.setBalance(1000);

        Company company = new Company(account);
        Thread companyThread = new Thread(company);
        Bank bank = new Bank(account);
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
