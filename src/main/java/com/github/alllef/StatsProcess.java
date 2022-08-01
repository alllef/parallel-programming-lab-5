package com.github.alllef;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatsProcess implements Runnable {
    private final BlockingQueue<CustomObject> buffer;
    private final Logger log = Logger.getLogger("Stats");

    public StatsProcess(BlockingQueue<CustomObject> buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "Objects in buffer: " + buffer.size());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
