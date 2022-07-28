package com.github.alllef;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsumerPool implements Runnable {
    private final List<Consumer> consumers;

    public ConsumerPool(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    @Override
    public void run() {
        for (Consumer consumer : consumers) {
            Thread thread = new Thread(consumer);
            thread.start();
        }
    }

    public static ConsumerPool createPool(int consumersNum, AtomicInteger servicedObjectsNum, BlockingQueue<CustomObject> buffer) {
        List<Consumer> consumers = IntStream.range(0, consumersNum)
                .mapToObj(__ -> new Consumer(servicedObjectsNum, buffer))
                .collect(Collectors.toList());
        return new ConsumerPool(consumers);
    }
}