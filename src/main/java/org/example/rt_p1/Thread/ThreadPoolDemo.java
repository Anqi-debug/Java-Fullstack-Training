package org.example.rt_p1.Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MyTask implements Runnable {
    private final int taskId;

    public MyTask(int id) {
        this.taskId = id;
    }

    @Override
    public void run() {
        System.out.println("Task " + taskId + " is running in " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000); // simulate some work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class ThreadPoolDemo {
    public static void main(String[] args) {
        // Create a thread pool with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit 5 tasks to the pool
        for (int i = 1; i <= 5; i++) {
            executor.submit(new MyTask(i));
        }

        // Shutdown the executor after task submission
        executor.shutdown();
    }
}
