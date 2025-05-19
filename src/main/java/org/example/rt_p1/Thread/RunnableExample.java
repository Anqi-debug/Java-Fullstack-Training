package org.example.rt_p1.Thread;

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable: running in " + Thread.currentThread().getName());
    }
}

public class RunnableExample {
    public static void main(String[] args) {
        Thread t2 = new Thread(new MyRunnable());
        t2.start();  // Also runs in a new thread
    }
}

