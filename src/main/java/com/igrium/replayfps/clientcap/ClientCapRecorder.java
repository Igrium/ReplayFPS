package com.igrium.replayfps.clientcap;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import com.igrium.replayfps.clientcap.ClientCapFile.Chunk;
import com.igrium.replayfps.clientcap.ClientCapFile.Frame;

public class ClientCapRecorder implements Closeable {

    public interface OutputStreamSupplier {
        OutputStream get() throws IOException;
    }
    
    protected final ClientCapFile file = new ClientCapFile();
    protected final OutputStreamSupplier outputStreamSupplier;

    private OutputStream outputStream;
    private long startTime;

    private boolean serverWasPaused;
    private long lastTimestamp;
    private long timePassedWhilePaused;

    private Chunk currentChunk;
    private int currentChunkIndex;

    private Queue<Chunk> chunksToSave = new ArrayDeque<>();
    
    public ClientCapRecorder(OutputStreamSupplier outputStreamSupplier) {
        this.outputStreamSupplier = outputStreamSupplier;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public ClientCapFile getFile() {
        return file;
    }

    public long getStartTime() {
        return startTime;
    }

    public final boolean serverWasPaused() {
        return serverWasPaused;
    }

    public void setServerWasPaused() {
        this.serverWasPaused = true;
    }

    /**
     * Begin capturing the world.
     */
    public void beginCapture() {
        try {
            outputStream = outputStreamSupplier.get();
            writeHeader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        startTime = System.currentTimeMillis();
    }

    public void captureFrame(ClientCaptureContext context) {
        long now = System.currentTimeMillis();

        // This math makes sense to me now, but don't ask me to explain it later.
        if (serverWasPaused) {
            timePassedWhilePaused = now - startTime - lastTimestamp;
            serverWasPaused = false;
        }
        int timestamp = (int) (now - startTime - timePassedWhilePaused);
        lastTimestamp = timestamp;

        int chunkIndex = Math.floorDiv(timestamp, file.getChunkLength());
        if (currentChunk == null || chunkIndex != currentChunkIndex) {
            createNewChunk(chunkIndex);
        }

        int chunkStart = chunkIndex * file.getChunkLength();
        
        Frame frame = file.captureFrame(context, timestamp - chunkStart);
        currentChunk.frames.add(frame);
    }

    private void createNewChunk(int chunkIndex) {
        if (chunkIndex <= currentChunkIndex) {
            throw new IllegalArgumentException("Supplied timestamp is in a chunk that has already been written!");
        }

        if (currentChunk != null) {
            chunksToSave.add(currentChunk);
        }

        // Your framerate is so bad you missed an entire chunk lol
        if (chunkIndex > currentChunkIndex + 1) {
            for (int i = currentChunkIndex + 1; i < chunkIndex; i++) {
                chunksToSave.add(new Chunk());
            }
        }

        currentChunk = new Chunk();
        currentChunkIndex = chunkIndex;
    }

    /*
     * Serialization
     */

    protected void writeHeader() throws IOException {
        file.writeHeader(outputStream);
    }

    public void saveChunks() throws IOException {
        Chunk chunk;
        while ((chunk = chunksToSave.poll()) != null) {
            chunk.serialize(outputStream);
        }
    }

    @Override
    public void close() throws IOException {
        saveChunks();
        outputStream.close();
    }
}
