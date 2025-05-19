package org.example.rt_p1.Thread;

public class ThreadLifecycleDemo {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread state inside run(): " + Thread.currentThread().getState()); // RUNNABLE
                Thread.sleep(2000); // TIMED_WAITING
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // NEW
        System.out.println("Before start(): " + thread.getState()); // NEW

        thread.start(); // moves to RUNNABLE

        try {
            Thread.sleep(100); // main thread waits briefly
            System.out.println("Just after start(): " + thread.getState()); // RUNNABLE or TIMED_WAITING

            thread.join(); // main waits for thread to finish
            System.out.println("After join(): " + thread.getState()); // TERMINATED
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

