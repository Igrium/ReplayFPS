package com.igrium.replayfps.test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.LockSupport;

public class SimpleSingleThreadExecutor implements Executor {
    private Queue<Runnable> queue = new ConcurrentLinkedDeque<>();
    private Thread thread;
    private boolean shouldShutdown;

    public SimpleSingleThreadExecutor(ThreadFactory threadFactory) {
        thread = threadFactory.newThread(this::runThread);
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public void execute(Runnable command) {
        queue.add(command);
        LockSupport.unpark(thread);
    }

    private void runThread() {
        while (!shouldShutdown) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
            LockSupport.parkNanos(100000l);
        }
    }

    public void shutdown() {
        shouldShutdown = true;
    }
}
