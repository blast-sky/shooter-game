package com.astrog.shootergame.common.gamecore;

import com.astrog.shootergame.common.lambda.InterruptedRunnable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class Looper extends Thread {

    private final InterruptedRunnable inLoopAction;
    private boolean isStarted = false;
    private volatile boolean isThreadRunning = true;
    @Getter
    private volatile boolean isLoopRunning = false;

    public synchronized void stopThread() {
        isLoopRunning = false;
        isThreadRunning = false;
        notify();
    }

    public synchronized void runLoop() {
        if (!isStarted) {
            start();
            isStarted = true;
        }
        isLoopRunning = true;
        notify();
    }

    public void pause() {
        isLoopRunning = false;
    }

    @Override
    public void run() {
        try {
            while (isThreadRunning) {
                while (isLoopRunning) {
                    inLoopAction.run();
                }
                synchronized (this) {
                    wait();
                }
            }
        } catch (InterruptedException ignored) {

        }
    }

    @SneakyThrows
    public void stopAllAndJoin() {
        pause();
        stopThread();
        interrupt();
        join();
    }
}
