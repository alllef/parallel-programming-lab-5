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
        long start = System.currentTimeMillis();
        Random random = new Random();
        while (System.currentTimeMillis() - start < Variables.PROCESSING_TIME_MS) {
            CustomObject object = new CustomObject("object");
            if (!buffer.offer(object))
                failuresNum.incrementAndGet();
            try {
                Thread.sleep(Variables.PRODUCER_DELAY_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
