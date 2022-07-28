package com.github.alllef;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable {
    private boolean isFree = true;
    private final AtomicInteger servicedObjectsNum;

    public Consumer(AtomicInteger servicedObjectsNum) {
        this.servicedObjectsNum = servicedObjectsNum;
    }

    @Override
    public void run() {
        while (true) {
            while (isFree) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Random rand = new Random();
            try {
                Thread.sleep((long) (rand.nextGaussian() * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            servicedObjectsNum.incrementAndGet();
            isFree = true;
        }
    }

    public synchronized boolean isFree() {
        return isFree;
    }

    public synchronized void put(CustomObject customObject) {
        isFree = false;
        notifyAll();
    }
}
