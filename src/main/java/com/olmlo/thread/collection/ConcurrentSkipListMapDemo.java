package com.olmlo.thread.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Main class of the example. It executes twenty-five tasks that
 * store contacts in the navigable map and then shows part of the content
 * of that navigable map
 *
 */
public class ConcurrentSkipListMapDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        /* Create the navigable map */
        ConcurrentSkipListMap<String, Contact> map;
        map = new ConcurrentSkipListMap<>();

        /* Create an array to store the 25 threads that execute the tasks */
        Thread threads[] = new Thread[25];
        int counter = 0;

        /* Execute the 25 tasks */
        for (char i = 'A'; i < 'Z'; i++) {
            ConcurrentSkipListMapTask task = new ConcurrentSkipListMapTask(map, String.valueOf(i));
            threads[counter] = new Thread(task);
            threads[counter].start();
            counter++;
        }

        /* Wait for the finalization of the threads */
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /* Write the size of the map */
        System.out.printf("Main: Size of the map: %d\n", map.size());

        /* Write the first element of the map */
        Map.Entry<String, Contact> element;
        Contact contact;

        element = map.firstEntry();
        contact = element.getValue();
        System.out.printf("Main: First Entry: %s: %s\n", contact.getName(), contact.getPhone());

        /* Write the last element of the map */
        element = map.lastEntry();
        contact = element.getValue();
        System.out.printf("Main: Last Entry: %s: %s\n", contact.getName(), contact.getPhone());

        /* Write a subset of the map */
        System.out.printf("Main: Submap from A1996 to B1002: \n");
        ConcurrentNavigableMap<String, Contact> submap = map.subMap("A1996", "B1002");
        do {
            element = submap.pollFirstEntry();
            if (element != null) {
                contact = element.getValue();
                System.out.printf("%s: %s\n", contact.getName(), contact.getPhone());
            }
        } while (element != null);
    }
}

class ConcurrentSkipListMapTask implements Runnable {

    /**
     * Navigable map to store the contacts
     */
    private ConcurrentSkipListMap<String, Contact> map;

    /**
     * Id of the task
     */
    private String id;

    /**
     * Constructor of the class that initializes its events
     * @param map Navigable map to store the events
     * @param id Id of the task
     */
    public ConcurrentSkipListMapTask(ConcurrentSkipListMap<String, Contact> map, String id) {
        this.id = id;
        this.map = map;
    }

    /**
     * Main method of the task. Generates 1000 contact objects and
     * store them in the navigable map
     */
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Contact contact = new Contact(id, String.valueOf(i + 1000));
            map.put(id + contact.getPhone(), contact);
        }
    }
}

class Contact {

    /**
     * Name of the contact
     */
    private String name;

    /**
     * Phone number of the contact
     */
    private String phone;

    /**
     * Constructor of the class
     * @param name Name of the contact
     * @param phone Phone number of the contact
     */
    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    /**
     * Method that returns the name of the contact
     * @return The name of the contact
     */
    public String getName() {
        return name;
    }

    /**
     * Method that returns the phone number of the contact
     * @return
     */
    public String getPhone() {
        return phone;
    }
}
