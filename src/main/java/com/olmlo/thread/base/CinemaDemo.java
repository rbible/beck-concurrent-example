package com.olmlo.thread.base;

/**
 * Core class of the example. Creates a cinema and two threads for
 * the ticket office. Run the threads to analyze the results obtained
 *
 */
public class CinemaDemo {

    /**
     * Main method of the example
     * @param args
     */
    public static void main(String[] args) {
        // Creates a Cinema
        Cinema cinema = new Cinema();

        // Creates a TicketOffice1 and a Thread to run it
        TicketOffice1 ticketOffice1 = new TicketOffice1(cinema);
        Thread thread1 = new Thread(ticketOffice1, "TicketOffice1");

        // Creates a TicketOffice2 and a Thread to run it
        TicketOffice2 ticketOffice2 = new TicketOffice2(cinema);
        Thread thread2 = new Thread(ticketOffice2, "TicketOffice2");

        // Starts the threads
        thread1.start();
        thread2.start();

        try {
            // Waits for the finalization of the threads
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print the vacancies in the cinemas
        System.out.printf("Room 1 Vacancies: %d\n", cinema.getVacanciesCinema1());
        System.out.printf("Room 2 Vacancies: %d\n", cinema.getVacanciesCinema2());
    }
}

class Cinema {

    /**
     * This two variables store the vacancies in two cinemas
     */
    private long vacanciesCinema1;
    private long vacanciesCinema2;

    /**
     * Two objects for the synchronization. ControlCinema1 synchronizes the
     * access to the vacancesCinema1 attribute and controlCinema2 synchronizes
     * the access to the vacanciesCinema2 attribute.
     */
    private final Object controlCinema1, controlCinema2;

    /**
     * Constructor of the class. Initializes the objects
     */
    public Cinema() {
        controlCinema1 = new Object();
        controlCinema2 = new Object();
        vacanciesCinema1 = 20;
        vacanciesCinema2 = 20;
    }

    /**
     * This method implements the operation of sell tickets for the cinema 1
     * @param number number of tickets sold
     * @return true if the tickets are sold, false if there is no vacancies
     */
    public boolean sellTickets1(int number) {
        synchronized (controlCinema1) {
            if (number < vacanciesCinema1) {
                vacanciesCinema1 -= number;
                return true;
            }
            return false;
        }
    }

    /**
     * This method implements the operation of sell tickets for the cinema 2
     * @param number number of tickets sold
     * @return true if the tickets are sold, false if there is no vacancies
     */
    public boolean sellTickets2(int number) {
        synchronized (controlCinema2) {
            if (number < vacanciesCinema2) {
                vacanciesCinema2 -= number;
                return true;
            }
            return false;
        }
    }

    /**
     * This method implements the operation of return tickets for the cinema 1
     * @param number number of the tickets returned
     * @return true
     */
    public boolean returnTickets1(int number) {
        synchronized (controlCinema1) {
            vacanciesCinema1 += number;
            return true;
        }
    }

    /**
     * This method implements the operation of return tickets for the cinema 1
     * @param number number of the tickets returned
     * @return true
     */
    public boolean returnTickets2(int number) {
        synchronized (controlCinema2) {
            vacanciesCinema2 += number;
            return true;
        }
    }

    /**
     * Return the vacancies in the cinema 1
     * @return the vacancies in the cinema 1
     */
    public long getVacanciesCinema1() {
        return vacanciesCinema1;
    }

    /**
     * Return the vacancies in the cinema 2
     * @return the vacancies in the cinema 2
     */
    public long getVacanciesCinema2() {
        return vacanciesCinema2;
    }

}

class TicketOffice1 implements Runnable {

    /**
     * The cinema 
     */
    private Cinema cinema;

    /**
     * Constructor of the class
     * @param cinema the cinema
     */
    public TicketOffice1(Cinema cinema) {
        this.cinema = cinema;
    }

    /**
     * Core method of this ticket office. Simulates selling and returning tickets
     */
    @Override
    public void run() {
        cinema.sellTickets1(3);
        cinema.sellTickets1(2);
        cinema.sellTickets2(2);
        cinema.returnTickets1(3);
        cinema.sellTickets1(5);
        cinema.sellTickets2(2);
        cinema.sellTickets2(2);
        cinema.sellTickets2(2);
    }
}


class TicketOffice2 implements Runnable {

    /**
     * The cinema 
     */
    private Cinema cinema;

    /**
     * Constructor of the class
     * @param cinema the cinema
     */
    public TicketOffice2(Cinema cinema) {
        this.cinema = cinema;
    }

    /**
     * Core method of this ticket office. Simulates selling and returning tickets
     */
    @Override
    public void run() { // 16-2
        cinema.sellTickets2(2);
        cinema.sellTickets2(4);
        cinema.sellTickets1(2);
        cinema.sellTickets1(1);
        cinema.returnTickets2(2);
        cinema.sellTickets1(3);
        cinema.sellTickets2(2);
        cinema.sellTickets1(2);
    }

}
