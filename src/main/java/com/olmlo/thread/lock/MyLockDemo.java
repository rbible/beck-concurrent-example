package com.olmlo.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLockDemo {
    public static void main(String[] args) {

        /* Create a new MyLock object */
        MyLock lock = new MyLock();

        /* Create and run ten task objects */
        for (int i = 0; i < 10; i++) {
            MyLockTask task = new MyLockTask("Task-" + i, lock);
            Thread thread = new Thread(task);
            thread.start();
        }

        /* The main thread also tries to get the lock */
        boolean value;
        do {
            try {
                value = lock.tryLock(1, TimeUnit.SECONDS);
                if (!value) {
                    System.out.printf("Main: Trying to get the Lock\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                value = false;
            }
        } while (!value);

        /* The main thread release the lock */
        System.out.printf("Main: Got the lock\n");
        lock.unlock();

        /* Write a message in the console indicating the end of the program */
        System.out.printf("Main: End of the program\n");
    }

}

class MyAbstractQueuedSynchronizer extends AbstractQueuedSynchronizer {

    private static final long serialVersionUID = 1L;

    /**
     * Attribute that stores the state of the lock. 0 if it's free, 1 if it's busy
     */
    private AtomicInteger state;

    /**
     * Constructor of the class
     */
    public MyAbstractQueuedSynchronizer() {
        state = new AtomicInteger(0);
    }

    /**
     * This method try to acquire the control of the lock
     * @return true if the thread acquires the lock, false otherwise
     */
    @Override
    protected boolean tryAcquire(int arg) {
        return state.compareAndSet(0, 1);
    }

    /**
     * This method try to free the control of the lock
     * @return true if the thread releases the lock, false otherwise
     */
    @Override
    protected boolean tryRelease(int arg) {
        return state.compareAndSet(1, 0);
    }
}

class MyLock implements Lock {

    /**
     * Synchronizer to implement the operations of the locks
     */
    private AbstractQueuedSynchronizer sync;

    /**
     * Constructor of the class. It initializes its attribute
     */
    public MyLock() {
        sync = new MyAbstractQueuedSynchronizer();
    }

    /**
     * Method that try to acquire the lock. If it can't, the thread
     * will be blocked until the thread that has it release the lock
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * Method that try to acquire the lock. If it can't, the thread will
     * be blocked until the thread that has it release the lock. The difference
     * with the lock() method is that in this case, the blocked threads can
     * be interrupted
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * Method that try to acquire the lock. If it can, the method returns the true
     * value. It it can't, the method return the false value
     */
    @Override
    public boolean tryLock() {
        try {
            return sync.tryAcquireNanos(1, 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method that try to acquire the lock. If it can, the method returns the true value.
     * If it can't, wait the time specified as parameter and if the lock hasn't been
     * released, it returns the false value. It the lock is released in that period of time,
     * the thread acquires the lock and the method returns the true value
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, TimeUnit.NANOSECONDS.convert(time, unit));
    }

    /**
     * Method that release the lock
     */
    @Override
    public void unlock() {
        sync.release(1);
    }

    /**
     * Method that creates a new condition for the lock
     */
    @Override
    public Condition newCondition() {
        return sync.new ConditionObject();
    }

}

class MyLockTask implements Runnable {

    /**
     * Lock used by the task
     */
    private MyLock lock;

    /**
     * Name of the task
     */
    private String name;

    /**
     * Constructor of the class
     * @param name Name of the task
     * @param lock Lock used by the task
     */
    public MyLockTask(String name, MyLock lock) {
        this.lock = lock;
        this.name = name;
    }

    /**
     * Main method of the task. It gets the lock, sleep the thread for two seconds
     * and then release the lock.
     */
    @Override
    public void run() {
        lock.lock();
        System.out.printf("Task: %s: Take the lock\n", name);
        try {
            TimeUnit.SECONDS.sleep(2);
            System.out.printf("Task: %s: Free the lock\n", name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
