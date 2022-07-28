package com.github.alllef;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectBuffer {
    private final BlockingQueue<CustomObject> queue;
    private final Lock lock = new ReentrantLock();
    private final Condition elemAdded = lock.newCondition();
    private final Condition capacityRemains = lock.newCondition();
    private final AtomicInteger failuresNum = new AtomicInteger(0);
    private final Logger log = Logger.getLogger("Buffer stats");

    public ObjectBuffer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

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

    public boolean put(CustomObject object) {
        if (queue.remainingCapacity() != 0) {
            try {
                queue.put(object);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            elemAdded.signalAll();
            return true;
        } else
            return false;
    }
}
