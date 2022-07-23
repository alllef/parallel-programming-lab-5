package com.github.alllef;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectBuffer {
    private final BlockingQueue<CustomObject> queue = new ArrayBlockingQueue<>(1000);
    private final Lock lock = new ReentrantLock();
    private final Condition elemAdded = lock.newCondition();
    private final Condition capacityRemains = lock.newCondition();

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
        try {
            while (queue.remainingCapacity() == 0)
                capacityRemains.await();
            queue.put(object);
            elemAdded.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
