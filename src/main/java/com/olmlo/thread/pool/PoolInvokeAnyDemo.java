package com.olmlo.thread.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the main class of the example. Creates two user validation systems and execute them in an Executor using the
 * invokeAny() method. If the user is validated by one of the user validation systems, then it shows a message. If both
 * system don't validate the user, the application proccess the ExecutionException throwed by the method
 */
public class PoolInvokeAnyDemo {

    public static void main(String[] args) {

        // Initialize the parameters of the user
        String username = "test";
        String password = "test";

        // Create two user validation objects
        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("DataBase");

        // Create two tasks for the user validation objects
        TaskValidator ldapTask = new TaskValidator(ldapValidator, username, password);
        TaskValidator dbTask = new TaskValidator(dbValidator, username, password);

        // Add the two tasks to a list of tasks
        List<TaskValidator> taskList = new ArrayList<>();
        taskList.add(ldapTask);
        taskList.add(dbTask);

        // Create a new Executor
        ExecutorService executor = Executors.newCachedThreadPool();
        String result;
        try {
            // Send the list of tasks to the executor and waits for the result of the first task
            // that finish without throw and Exception. If all the tasks throw and Exception, the
            // method throws and ExecutionException.
            result = executor.invokeAny(taskList);
            System.out.printf("Main: Result: %s\n", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Shutdown the Executor
        executor.shutdown();
        System.out.printf("Main: End of the Execution\n");
    }

}

class TaskValidator implements Callable<String> {

    /**
     * The user validator used to validate the user.
     */
    private UserValidator validator;
    /**
     * The name of the user
     */
    private String user;
    /**
     * The password of the user
     */
    private String password;

    /**
     * Constructor of the class
     * 
     * @param validator The user validator system used to validate it
     * @param user The name of the user
     * @param password The password of the user
     */
    public TaskValidator(UserValidator validator, String user, String password) {
        this.validator = validator;
        this.user = user;
        this.password = password;
    }

    /**
     * Core method of the Callable interface. Tries to validate the user using the user validation system. If the user
     * is validated, returns the name of the validation system. If not, throws and Exception
     * 
     * @return The name of the user validation system.
     * @throws Exception An exception when the user is not validated
     */
    @Override
    public String call() throws Exception {
        if (!validator.validate(user, password)) {
            System.out.printf("%s: The user has not been found\n", validator.getName());
            throw new Exception("Error validating user");
        }
        System.out.printf("%s: The user has been found\n", validator.getName());
        return validator.getName();
    }

}

class UserValidator {

    /**
     * The name of the validation system
     */
    private String name;

    /**
     * Constructor of the class
     * 
     * @param name The name of the user validation system
     */
    public UserValidator(String name) {
        this.name = name;
    }

    /**
     * Method that validates a user
     * 
     * @param name Name of the user
     * @param password Password of the user
     * @return true if the user is validated and false if not
     */
    public boolean validate(String name, String password) {
        // Create a new Random objects generator
        Random random = new Random();

        // Sleep the thread during a random period of time
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.printf("Validator %s: Validating a user during %d seconds\n", this.name, duration);
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            return false;
        }

        // Return a random boolean value
        return random.nextBoolean();
    }

    public String getName() {
        return name;
    }

}
