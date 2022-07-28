package com.github.alllef;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {
    private final BlockingQueue<CustomObject> buffer;
    private final AtomicInteger failuresNum;

    public Producer(BlockingQueue<CustomObject> buffer, AtomicInteger failuresNum) {
        this.buffer = buffer;
        this.failuresNum = failuresNum;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            CustomObject object = new CustomObject("object");
            if (!buffer.offer(object))
                failuresNum.incrementAndGet();
            try {
                Thread.sleep(random.nextInt(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
