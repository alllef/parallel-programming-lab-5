package com.github.alllef;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class QueueSystem implements Runnable {
    private final BlockingQueue<CustomObject> buffer;
    private final AtomicInteger failuresNum = new AtomicInteger(0);
    private final AtomicInteger servicedObjectsNum = new AtomicInteger(0);
    private final int consumersNum;

    public QueueSystem(int consumersNum, int bufferNum) {
        this.buffer = new ArrayBlockingQueue<>(bufferNum);
        this.consumersNum = consumersNum;
    }

    @Override
    public void run() {
        List.of(new Producer(buffer, failuresNum), new StatsProcess(buffer))
                .forEach(runnable -> new Thread(runnable).start());
        var consumerPool = Executors.newFixedThreadPool(consumersNum);
        IntStream.range(0, consumersNum).forEach(__ ->
                consumerPool.execute(new Consumer(servicedObjectsNum, buffer)));
    }
}
