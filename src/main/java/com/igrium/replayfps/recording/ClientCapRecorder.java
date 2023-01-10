package com.igrium.replayfps.recording;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

import com.igrium.replayfps.clientcap.ClientCapFile;
import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientCapFile.Chunk;
import com.igrium.replayfps.clientcap.ClientCapFile.Frame;

import net.minecraft.client.network.ClientPlayerEntity;

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
    private int currentChunkIndex = -1;

    private Supplier<ClientPlayerEntity> playerSupplier = () -> null;
    private boolean headerWritten;

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
    
    /**
     * Set the function used to retrieve the local player entity. This function
     * <i>may</i> return null, and if it does, the header will not be written until
     * it returns a value.
     * 
     * @param playerSupplier The player supplier.
     * @return <code>this</code>
     */
    public ClientCapRecorder setPlayerSupplier(Supplier<ClientPlayerEntity> playerSupplier) {
        this.playerSupplier = playerSupplier;
        return this;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        startTime = System.currentTimeMillis();
    }

    public void captureFrame(ClientCaptureContext context) {
        if (outputStream == null) {
            throw new IllegalStateException("Please call beginCapture() first!");
        }
        if (!headerWritten) {
            if (!tryWriteHeader()) return;
        }

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

    private boolean tryWriteHeader() {
        if (headerWritten) {
            throw new IllegalStateException("The header has already been written.");
        }
        ClientPlayerEntity player = playerSupplier.get();
        if (player == null) return false;

        file.setLocalPlayerId(player.getId());
        try {
            file.writeHeader(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error writing header: ", e);
        }
        headerWritten = true;
        return true;
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
    @Deprecated
    protected void writeHeader() throws IOException {
        file.writeHeader(outputStream);
        headerWritten = true;
    }

    public void saveChunks() throws IOException {
        DataOutputStream buffer = new DataOutputStream(outputStream);
        Chunk chunk;
        while ((chunk = chunksToSave.poll()) != null) {
            chunk.serialize(buffer);
        }
        buffer.flush();
    }

    @Override
    public void close() throws IOException {
        saveChunks();
        outputStream.close();
    }
}
