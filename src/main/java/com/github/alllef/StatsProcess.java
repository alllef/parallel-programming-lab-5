package com.github.alllef;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsProcess implements Callable<StatsResult> {
    private final BlockingQueue<CustomObject> buffer;
    private final AtomicInteger failuresNum;
    private final AtomicInteger servicedObjectsNum;
    private final Logger log = Logger.getLogger("Stats");

    public StatsProcess(BlockingQueue<CustomObject> buffer, AtomicInteger failuresNum, AtomicInteger servicedObjectsNum) {
        this.buffer = buffer;
        this.failuresNum = failuresNum;
        this.servicedObjectsNum = servicedObjectsNum;
    }

    private double calcAvgBufferOccupancy(List<Integer> bufferSizes) {
        return bufferSizes.stream()
                .mapToDouble(integer -> (double) integer)
                .average()
                .getAsDouble();
    }

    private double calcFailureProbability() {
        return (double) failuresNum.get() / (failuresNum.get() + servicedObjectsNum.get() + buffer.size()) * 100;
    }

    @Override
    public StatsResult call() throws Exception {
        int imitationId = new Random().nextInt();

        long start = System.currentTimeMillis();
        List<Integer> bufferSizes = new ArrayList<>();

        while (System.currentTimeMillis() - start < Variables.PROCESSING_TIME_MS) {
            String logString = String.format("For imitation id %d objects in buffer: %d Number of serviced objects: %d Number of failures: %d",imitationId, buffer.size(), servicedObjectsNum.get(), failuresNum.get());
            bufferSizes.add(buffer.size());
            log.log(Level.INFO, logString);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return new StatsResult(calcAvgBufferOccupancy(bufferSizes), calcFailureProbability());
    }
}
