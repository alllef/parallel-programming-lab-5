package com.github.alllef;

import java.util.Random;

public class Producer implements Runnable {
    private final ObjectBuffer objectBuffer;

    public Producer(ObjectBuffer objectBuffer) {
        this.objectBuffer = objectBuffer;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            CustomObject object = new CustomObject("object");
            objectBuffer.put(object);
            try {
                Thread.sleep(random.nextInt(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
