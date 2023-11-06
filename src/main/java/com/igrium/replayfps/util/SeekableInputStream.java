package com.igrium.replayfps.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SeekableInputStream extends InputStream {

    public interface InputStreamSupplier {
        InputStream get() throws IOException;
    }

    protected final InputStreamSupplier supplier;

    private long head;
    private long mark;
    private long prevMark;
    private InputStream inputStream;
    private int bufferSize = 0x10000;

    /**
     * Create a managed input stream.
     * 
     * @param supplier A supplier of a base input stream. This supplier may be
     *                 called any number of times, and it must return an input
     *                 stream with its head at the start of the file every time.
     * @throws IOException If an I/O exception occurs while loading the stream.
     */
    public SeekableInputStream(InputStreamSupplier supplier) throws IOException {
        this.supplier = supplier;
        genStream();
    }

    public SeekableInputStream(InputStreamSupplier supplier, int bufferSize) throws IOException {
        this.supplier = supplier;
        this.bufferSize = bufferSize;
        genStream();
    }

    private void genStream() throws IOException {
        if (inputStream != null) inputStream.close();
        inputStream = new BufferedInputStream(inputStream, bufferSize);
        prevMark = -1;
    }

    @Override
    public synchronized int read() throws IOException {
        int result = inputStream.read();
        if (result != -1) {
            head++;
        }
        return result;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        int result = inputStream.read(b, off, len);
        head += result;
        return result;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        long result = inputStream.skip(n);
        head += result;
        return result;
    }
    
    /**
     * Get the current head of the stream.
     */
    public long getHead() {
        return head;
    }

    @Override
    public boolean markSupported() {
        return true;
    }
    
    /**
     * <p>
     * Marks the current position in this input stream. A subsequent call to the
     * reset method repositions this stream at the last marked position so that
     * subsequent reads re-read the same bytes.
     * </p>
     * <p>
     * Because <code>ManagedInputStream</code> can always jump to anywhere in a
     * file, this method works slightly different than normal. If the underlying
     * input stream doesn't support mark/reset, this simply sets a flag internally so
     * <code>reset()</code> knows where to jump to. However, if the underlying input
     * stream DOES support mark/reset, the underlying stream is marked as well, and
     * <code>jumpTo</code> is made slightly more efficient by utilizing the reset
     * capabilities of the underlying stream rather than constructing a new one.
     * </p>
     */
    @Override
    public synchronized void mark(int readlimit) {
        mark = head;
        if (inputStream.markSupported()) {
            inputStream.mark(readlimit);
            prevMark = head;
        }
    }
    
    @Override
    public synchronized void reset() throws IOException {
        jumpTo(mark);
    }    

    /**
     * Attempt to jump to a particular address in the file.
     * @param address Byte address to jump to.
     * @return The actual address that was reached.
     * @throws IOException If an I/O exception occurs.
     */
    public synchronized long jumpTo(long address) throws IOException {
        if (address == head) return head;
        else if (address > head) {
            skip(address - head);
            return head;
        } else {
            boolean resetSuccess = false;
            // Attempt to use mark before creating a whole new stream.
            if (inputStream.markSupported() && prevMark >= 0 && address >= prevMark) {
                try {
                    inputStream.reset();
                    head = prevMark + inputStream.skip(address - prevMark);
                    resetSuccess = true;
                } catch (IOException e) {}
            }

            if (!resetSuccess) {
                genStream();
                head = inputStream.skip(address);
            }
            return head;
        }
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) inputStream.close();
    }
}
