package com.github.alllef;

public class Consumer implements Runnable{
    private final ObjectBuffer objectBuffer;

    public Consumer(ObjectBuffer objectBuffer) {
        this.objectBuffer = objectBuffer;
    }

    @Override
    public void run() {

    }
}
