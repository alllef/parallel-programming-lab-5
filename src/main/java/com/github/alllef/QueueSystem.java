package com.github.alllef;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueSystem {
    private final ConsumerPool consumerPool;
    private final BlockingQueue<CustomObject> buffer;
    private final AtomicInteger failuresNum = new AtomicInteger(0);
    private final AtomicInteger servicedObjectsNum = new AtomicInteger(0);

    public QueueSystem(int consumersNum, int bufferNum) {
        this.buffer = new ArrayBlockingQueue<>(bufferNum);
        this.consumerPool = ConsumerPool.createPool(consumersNum, servicedObjectsNum, buffer);
        consumerPool.run();
        Thread thread = new Thread(new StatsProcess(buffer));
        new Producer(buffer, failuresNum).run();
    }

    public int getFailuresNum() {
        return failuresNum.get();
    }

    public int getServicedObjectsNum() {
        return servicedObjectsNum.get();
    }
}
