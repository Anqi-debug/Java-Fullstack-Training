package org.example.rt_p1.Thread;

import java.util.concurrent.*;

class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "Callable: result from " + Thread.currentThread().getName();
    }
}

public class CallableExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new MyCallable());

        // Get the result (blocks until done)
        String result = future.get();
        System.out.println(result);

        executor.shutdown();
    }
}

