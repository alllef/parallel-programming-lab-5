package com.github.alllef;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Consumer implements Runnable {
    private boolean isFree = true;
    private final AtomicInteger servicedObjectsNum;
    private final BlockingQueue<CustomObject> objectBuffer;
    private final Logger log = Logger.getLogger("consumer");

    public Consumer(AtomicInteger servicedObjectsNum, BlockingQueue<CustomObject> objectBuffer) {
        this.servicedObjectsNum = servicedObjectsNum;
        this.objectBuffer = objectBuffer;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis()-start < Variables.PROCESSING_TIME_MS) {
            try {
                objectBuffer.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Random rand = new Random();
            try {
                Thread.sleep((long) (Math.abs(rand.nextGaussian() * Variables.CONSUMER_PROCESSING_MS)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            servicedObjectsNum.incrementAndGet();
           // log.log(Level.INFO, "" + servicedObjectsNum.get());
        }
    }

    public synchronized boolean isFree() {
        return isFree;
    }
}
