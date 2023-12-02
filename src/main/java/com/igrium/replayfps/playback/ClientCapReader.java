package com.igrium.replayfps.playback;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

import org.jetbrains.annotations.Nullable;

import com.google.common.io.CountingInputStream;
import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.recording.ClientCapHeader;
import com.igrium.replayfps.util.NoHeaderException;
import com.mojang.logging.LogUtils;

/**
 * Reads a ClientCap file.
 */
public class ClientCapReader implements Closeable {

    private int headerLength;
    private int frameLength;
    private final RandomAccessFile file;

    private ClientCapHeader header;

    /**
     * Create a ClientCap reader.
     * 
     * @param stream An input stream containing the file contents. The entire file
     *               will be read and stored in a temp directory, where it can be
     *               randomly accessed.
     * @throws IOException If an IO exception occurs reading the file.
     */
    public ClientCapReader(InputStream stream) throws IOException {
        File tempFile = File.createTempFile("client", ".ccap");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            stream.transferTo(out);
        };

        tempFile.deleteOnExit();
        this.file = new RandomAccessFile(tempFile, "r");
    }

    /**
     * Create a ClientCap reader.
     * @param file RandomAccessFile to use. Will be closed upon reader close.
     */
    public ClientCapReader(RandomAccessFile file) {
        this.file = file;
    }

    /**
     * Create a ClientCap reader.
     * @param file File to read.
     * @throws FileNotFoundException If the file does not exist.
     */
    public ClientCapReader(File file) throws FileNotFoundException {
        this.file = new RandomAccessFile(file, "r");
    }

    @Nullable
    public final ClientCapHeader getHeader() {
        return header;
    }

    /**
     * Read the header of this file.
     * 
     * @throws IOException           If an IO exception occurs.
     * @throws IllegalStateException If the header has already been read.
     */
    public synchronized void readHeader() throws IOException, IllegalStateException {
        if (header != null) {
            throw new IllegalStateException("The header has already been read!");
        }
        
        CountingInputStream counter = new CountingInputStream(Channels.newInputStream(file.getChannel()));
        header = new ClientCapHeader();
        header.readHeader(counter);
        frameLength = header.calculateFrameLength();
        headerLength = (int) counter.getCount();
    }

    private int playhead;

    /**
     * Get the position of the playhead.
     * @return Index of the frame that will be read on next call to {@link #readFrame()}.
     */
    public int getPlayhead() {
        return playhead;
    }

    private boolean endOfFile;

    /**
     * If this reader has reached the end of the file.
     */
    public boolean isEndOfFile() {
        return endOfFile;
    }

    /**
     * Read the current frame and advance the playhead by 1.
     * 
     * @return An array of all channels and their parsed values. If we've reached
     *         the end of the file, this array is empty.
     * @throws IOException       If an IO exception occurs while reading the file.
     * @throws NoHeaderException If the header has not been read.
     */
    public synchronized UnserializedFrame readFrame() throws IOException, NoHeaderException {
        assertHeaderRead();
        if (endOfFile) return new UnserializedFrame(header);

        var channels = new Object[header.numChannels()];
        try {
            int i = 0;
            for (ChannelHandler<?> handler : header.getChannels()) {
                channels[i] = handler.getChannelType().read(file);
                i++;
            }
        } catch (EOFException e) {
            endOfFile = true;
            return new UnserializedFrame(header);
        }

        playhead += 1;
        return new UnserializedFrame(header, channels);

    }

    /**
     * Get the byte offset of a given frame in the file.
     * 
     * @param frame Frame index.
     * @return Byte offset of the beginning of the frame.
     * @throws NoHeaderException If the header has not been read (required for frame
     *                           length.)
     */
    public long getFrameOffset(int frame) throws NoHeaderException {
        assertHeaderRead();
        return ((long) frame) * frameLength + headerLength;
    }

    /**
     * Count the number of frames within the file.
     * 
     * @return Number of frames. <code>-1</code> if there's an error reading the
     *         file.
     * @throws NoHeaderException If the header has not been read (required for
     *                           frame length.)
     */
    public int countFrames() throws NoHeaderException {
        try {
            return countFramesOrThrow();
        } catch (IOException e) {
            LogUtils.getLogger().error("Error getting length of file.", e);
            return -1;
        }
    }
    
    /**
     * Count the number of frames within the file.
     * 
     * @return Number of frames.
     * @throws NoHeaderException If the header has not been read (required for frame
     *                           length.)
     * @throws IOException       If an IO exception occurs reading the file.
     */
    public synchronized int countFramesOrThrow() throws NoHeaderException, IOException {
        assertHeaderRead();
        return (int) ((file.length() - headerLength) / frameLength);
    }

    /**
     * Jump to a specific frame in the file, queuing it for {@link #readFrame()}.
     * 
     * @param frame Frame index.
     * @throws NoHeaderException         If the file header has not been read.
     * @throws IndexOutOfBoundsException If frame is less than 0.
     * @throws IOException               If an IO exception occurs seeking within
     *                                   the file.
     */
    public synchronized void seek(int frame) throws NoHeaderException, IndexOutOfBoundsException, IOException {
        assertHeaderRead();
        if (frame == playhead) return;
        if (frame < 0) {
            throw new IndexOutOfBoundsException(frame);
        }
        
        long offset = getFrameOffset(frame);
        file.seek(offset);
        endOfFile = offset > file.length();

        playhead = frame;
    }

    private void assertHeaderRead() throws NoHeaderException {
        if (header == null) {
            throw new NoHeaderException("The header has not been read!");
        }
    }

    /**
     * Close this reader and the underlying file.
     * 
     * @throws IOException If an IO exception is thrown when closing the file.
     */
    @Override
    public synchronized void close() throws IOException {
        file.close();
    }
}
