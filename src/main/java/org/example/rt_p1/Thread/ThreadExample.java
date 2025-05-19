package org.example.rt_p1.Thread;

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread: running in " + Thread.currentThread().getName());
    }
}

public class ThreadExample {
    public static void main(String[] args) {
        MyThread t1 = new MyThread();
        t1.start();  // Runs in a new thread
    }
}

