package com.igrium.replayfps.recording;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.jetbrains.annotations.Nullable;

import com.igrium.replayfps.channel.handler.ChannelHandler;

/**
 * Captures and saves frames to a file.
 */
public class ClientCapRecorder implements Closeable {

    private final BufferedOutputStream out;

    private int saveInterval = 120;

    @Nullable
    private ClientCapHeader header;
    
    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    private int framesSinceLastSave;

    public ClientCapRecorder(OutputStream out) {
        this.out = new BufferedOutputStream(out);
    }

    public void writeHeader(ClientCapHeader header) throws IOException {
        if (this.header != null) {
            throw new IllegalStateException("The header has already been written!");
        }

        this.header = header;
        header.writeHeader(out);
        out.flush();
    }

    public void captureFrame() throws Exception {
        if (header == null) {
            throw new IllegalStateException("Header has not been written!");
        }

        for (ChannelHandler<?> handler : header.getChannels()) {
            ChannelHandler.writeChannel(out, handler);
        }

        if (framesSinceLastSave >= saveInterval) {
            out.flush();
            framesSinceLastSave = 0;
        } else {
            framesSinceLastSave++;
        }
    }
    

    @Override
    public void close() throws IOException {
        out.close();
    }
    
}
