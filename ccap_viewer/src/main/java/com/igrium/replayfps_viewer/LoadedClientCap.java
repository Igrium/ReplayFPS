package com.igrium.replayfps_viewer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.igrium.replayfps.playback.ClientCapReader;
import com.igrium.replayfps.recording.ClientCapHeader;

public class LoadedClientCap implements Closeable {
    private final ClientCapReader reader;
    private int length;

    public LoadedClientCap(ClientCapReader reader) throws IOException {
        this.reader = reader;
        reader.readHeader();
        length = reader.countFramesOrThrow();
    }

    public LoadedClientCap(File file) throws IOException {
        this.reader = new ClientCapReader(file);
        reader.readHeader();
        length = reader.countFramesOrThrow();
    }

    public LoadedClientCap(RandomAccessFile file) throws IOException {
        this.reader = new ClientCapReader(file);
        reader.readHeader();
        length = reader.countFramesOrThrow();
    }

    public final ClientCapHeader getHeader() {
        return reader.getHeader();
    }

    public ClientCapReader getReader() {
        return reader;
    }

    /**
     * Get the length of this client-capture.
     * @return Number of frames.
     */
    public int getLength() {
        return length;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
    
}
