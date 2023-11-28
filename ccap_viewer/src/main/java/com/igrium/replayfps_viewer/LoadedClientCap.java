package com.igrium.replayfps_viewer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.igrium.replayfps.playback.ClientCapReader;
import com.igrium.replayfps.recording.ClientCapHeader;

public class LoadedClientCap implements Closeable {
    private final ClientCapReader reader;

    public LoadedClientCap(ClientCapReader reader) throws IOException {
        this.reader = reader;
        reader.readHeader();
    }

    public LoadedClientCap(File file) throws IOException {
        this.reader = new ClientCapReader(file);
        reader.readHeader();
    }

    public LoadedClientCap(RandomAccessFile file) throws IOException {
        this.reader = new ClientCapReader(file);
        reader.readHeader();
    }

    public final ClientCapHeader getHeader() {
        return reader.getHeader();
    }

    public ClientCapReader getReader() {
        return reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
    
}
