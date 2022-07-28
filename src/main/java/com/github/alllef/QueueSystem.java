package com.github.alllef;

import java.util.concurrent.atomic.AtomicInteger;

public class QueueSystem {
    private final ConsumerPool consumerPool;
    private final ObjectBuffer objectBuffer;
    private final AtomicInteger failuresNum = new AtomicInteger(0);
    private final AtomicInteger servicedObjectsNum = new AtomicInteger(0);

    public QueueSystem(int consumersNum, int bufferNum) {
        this.consumerPool = ConsumerPool.createPool(consumersNum, servicedObjectsNum);
        this.objectBuffer = new ObjectBuffer(bufferNum);
    }

    public CustomObject take() {
        return objectBuffer.take();
    }

    public boolean put(CustomObject object) {
        if (consumerPool.tryExecute(object))
            return true;
        else {
            if (objectBuffer.put(object))
                return true;
            else {
                failuresNum.incrementAndGet();
                return false;
            }
        }
    }

    public int getFailuresNum() {
        return failuresNum.get();
    }

    public int getServicedObjectsNum() {
        return servicedObjectsNum.get();
    }
}
