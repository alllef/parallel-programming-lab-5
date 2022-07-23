package com.github.alllef;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectBuffer {
    private final BlockingQueue<CustomObject> queue = new ArrayBlockingQueue<>(1000);
    private final Lock lock = new ReentrantLock();
    private final Condition elemAdded = lock.newCondition();
    private final Condition capacityRemains = lock.newCondition();
    private final AtomicInteger servicedObjectsNum = new AtomicInteger(0);
    private final AtomicInteger failuresNum = new AtomicInteger(0);

    public CustomObject take() {
        try {
            while (queue.isEmpty())
                elemAdded.await();
            CustomObject object = queue.take();
            capacityRemains.signalAll();
            return object;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void put(CustomObject object) {
        if (queue.remainingCapacity() != 0) {
            try {
                queue.put(object);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            elemAdded.signalAll();
        } else
            failuresNum.incrementAndGet();
    }
}
