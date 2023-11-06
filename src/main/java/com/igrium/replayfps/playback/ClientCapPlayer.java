package com.igrium.replayfps.playback;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;

import com.google.common.io.CountingInputStream;
import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.recording.ClientCapHeader;
import com.igrium.replayfps.util.SeekableInputStream;
import com.igrium.replayfps.util.SeekableInputStream.InputStreamSupplier;

public class ClientCapPlayer {
    @Nullable 
    private ClientCapHeader header;
    private SeekableInputStream stream;

    private int maxCache = 0x100000;
    private int headerLength;
    private int frameLength;

    public long getMaxCache() {
        return maxCache;
    }

    public void setMaxCache(int maxCache) {
        this.maxCache = maxCache;
    }

    /**
     * Create a clientcap player.
     * @param streamSupplier Input stream supplier (sometimes needs to be re-created for seeking).
     * @throws IOException If there's an error creating the input stream.
     */
    public ClientCapPlayer(InputStreamSupplier streamSupplier) throws IOException {
        this.stream = new SeekableInputStream(streamSupplier, maxCache);
    }

    @Nullable
    public ClientCapHeader getHeader() {
        return header;
    }

    /**
     * The frame that was most recently read.
     */
    private int currentFrame = -1;

    /**
     * Get the index of the frame that was most recently read.
     * @return Frame index. <code>-1</code> if no frames have been read.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    private boolean endOfFile;

    public void readFrame() throws Exception {
        assertHeaderRead();
        if (endOfFile) return;
        
        try {
            DataInputStream dataIn = new DataInputStream(stream);
            for (ChannelHandler<?> handler : header.getChannels()) {
                ChannelHandler.readChannel(dataIn, handler);
            }
        } catch (EOFException e) {
            endOfFile = true;
        } finally {
            currentFrame++;
        }
    }

    public void readHeader() throws IOException, IllegalStateException {
        if (header != null) {
            throw new IllegalStateException("Header has already been read!");
        }

        header = new ClientCapHeader();
        CountingInputStream counter = new CountingInputStream(stream);
        header.readHeader(stream);
        frameLength = header.calculateFrameLength();
        headerLength = (int) counter.getCount();

        this.stream.mark(maxCache);
    }

    /**
     * Get the byte offset in the file of a given frame.
     * @param frame Frame number.
     * @return Byte offset of the start of the frame.
     */
    public long getFrameOffset(int frame) {
        assertHeaderRead();
        return ((long) frame) * frameLength + headerLength;
    }

    private void assertHeaderRead() {
        if (header == null) {
            throw new IllegalStateException("The header has not been read!");
        }
    }
}
