package com.github.alllef;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class QueueSystem implements Callable<StatsResult> {
    private final BlockingQueue<CustomObject> buffer;
    private final AtomicInteger failuresNum = new AtomicInteger(0);
    private final AtomicInteger servicedObjectsNum = new AtomicInteger(0);
    private final int consumersNum;

    public QueueSystem(int consumersNum, int bufferNum) {
        this.buffer = new ArrayBlockingQueue<>(bufferNum);
        this.consumersNum = consumersNum;
    }

    @Override
    public StatsResult call() throws Exception {
        var producer = new Producer(buffer, failuresNum);
        var producerThread = new Thread(producer);
        producerThread.start();

        var consumerPool = Executors.newFixedThreadPool(consumersNum);
        IntStream.range(0, consumersNum)
                .forEach(__ -> consumerPool.execute(new Consumer(servicedObjectsNum, buffer)));

        var statsProcess = new StatsProcess(buffer, failuresNum, servicedObjectsNum);
        ExecutorService service = Executors.newSingleThreadExecutor();
        return service.submit(statsProcess).get();
    }
}
