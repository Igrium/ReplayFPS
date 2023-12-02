package com.igrium.replayfps.core.util;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import com.mojang.logging.LogUtils;

/**
 * Represents a buffer of objects that can be loaded off-thread.
 * The implementation generally assumes that values are loaded faster than they
 * are read. The buffer is simply to account for any unexpected slowdowns. If
 * the loading process is too slow, each reading call will block until the next
 * item is loaded.
 */
public abstract class ConcurrentBuffer<T> {
    private final Executor executor;

    /**
     * Create a ConcurrentBuffer.
     * @param executor Thread to load the values on.
     */
    public ConcurrentBuffer(Executor executor) {
        this.executor = executor;
        buffer();
    }

    /**
     * The executor being used to load the values.
     */
    protected Executor getExecutor() {
        return executor;
    }

    private volatile int bufferSize = 1024;
    private volatile int bufferThreshold = 512;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getBufferThreshold() {
        return bufferThreshold;
    }

    public void setBufferThreshold(int bufferThreshold) {
        this.bufferThreshold = bufferThreshold;
    }

    
    private final Queue<T> buffer = new ConcurrentLinkedDeque<>();
    /*
     * The global index of the start of the buffer.
     */
    private volatile int startIndex;

    /**
     * Called on the loading thread to load the next item in the buffer.
     * 
     * @param index Index of the item being loaded.
     * @return The loaded value. <code>null</code> values indicate that we've
     *         reached the end of the buffer. At this point, read calls will return
     *         <code>null</code> instead of blocking.
     */
    protected abstract T load(int index) throws Exception;

    private T tryLoad(int index) {
        if (hasErrored()) return null;
        try {
            return load(index);
        } catch (Exception e) {
            // LogUtils.getLogger().error("Error loading buffer item " + index + ". Buffer will shutdown.", e);
            this.error = Optional.of(e);
            return null;
        }
    }

    private volatile Optional<Exception> error = Optional.empty();

    /**
     * If this buffer crashed due to an error, get it here.
     * @return The error.
     */
    public Optional<Exception> getError() {
        return error;
    }
    
    /**
     * If this buffer has crashed due to an error.
     */
    public boolean hasErrored() {
        return error.isPresent();
    }


    private volatile CountDownLatch bufferingLatch = new CountDownLatch(1);

    private boolean hasReachedEnd;
    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }

    private boolean shouldBlock() {
        return buffer.isEmpty() && bufferingLatch != null && !hasReachedEnd && !hasErrored();
    }
    
    /**
     * Wait for items to appear in the buffer.
     * @return Whether the thread needed to block.
     */
    protected boolean waitForBuffer() {
        if (shouldBlock()) {
            buffer(); // Make sure we're actually buffering.
            awaitBufferLatch();
            return true;
        }
        return false;
    }

    protected void awaitBufferLatch() {
        try {
            bufferingLatch.await();
        } catch (InterruptedException e) {
            LogUtils.getLogger().error("Thread was inturrupted while waiting for buffer.", e);
        }
    }

    /**
     * Asynchronously this buffer to its capacity.
     */
    public void buffer() {
        if (!isBuffering) executor.execute(this::doBuffer);
    }

    private volatile boolean isBuffering;
    private volatile boolean interruptBuffer;

    protected void doBuffer() {
        if (isBuffering || hasErrored())
                return;
        isBuffering = true;
        synchronized (buffer) {
            try {

                // LogUtils.getLogger().info("Buffering");

                int index = startIndex + buffer.size();
                if (hasErrored())
                    return;

                interruptBuffer = false;
                int i = 0;
                while (buffer.size() <= bufferSize && i <= bufferSize && !interruptBuffer && !hasReachedEnd) {
                    T val = tryLoad(index);
                    if (val == null)
                        hasReachedEnd = true;
                    else {
                        buffer.add(val);
                    }

                    index++;
                    i++;
                    openLatch();
                }
                interruptBuffer = false;
            } catch (Exception e) {
                e.printStackTrace();
                error = Optional.of(e);
            }
            openLatch();
            isBuffering = false;
        }

    }

    public void interruptBuffer() {
        if (isBuffering) interruptBuffer = true;
    }

    public final boolean isBuffering() {
        return isBuffering;
    }

    protected void openLatch() {
        // Create the new latch before opening the old one on the off chance that a
        // thread loops back around before method finishes
        if (bufferingLatch != null && bufferingLatch.getCount() == 0) return;
        
        CountDownLatch oldLatch = bufferingLatch;
        bufferingLatch = new CountDownLatch(1);
        if (oldLatch != null)
            oldLatch.countDown();
    }
    
    public synchronized T poll() {
        if (waitForBuffer()) {
            // Double-check the buffer before polling.
            return poll();
        }
        return pollImmediately();
    }

    public synchronized T pollImmediately() {
        startIndex += 1;
        T val = buffer.poll();

        // if (buffer.size() < bufferThreshold) {
        //     buffer();
        // }
        return val;
    }

    public synchronized T peek() {
        if (waitForBuffer()) {
            // Double-check the buffer before peeking.
            return peek();
        }
        return peekImmediately();
    }

    public T peekImmediately() {
        return buffer.peek();
    }

    public synchronized void seek(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException(index);

        // We can't seek while buffering.
        if (isBuffering) {
            interruptBuffer();
        }

        synchronized(buffer) {
            buffer.clear();
            this.startIndex = index;
            hasReachedEnd = false;
        }

        buffer();
    }

    /**
     * The index of the next item that will be returned.
     */
    public int getIndex() {
        return startIndex;
    }

    /**
     * Clear the buffer.
     */
    public void clear() {
        // We can't clear while buffering.
        if (isBuffering) {
            interruptBuffer();
        }

        synchronized (buffer) {
            buffer.clear();
        }
    }
}
