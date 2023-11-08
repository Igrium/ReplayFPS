package com.igrium.replayfps.playback;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import com.igrium.replayfps.playback.ClientCapReader.ParsedChannelFrame;

public class PlaybackBuffer {
    private Deque<Collection<ParsedChannelFrame<?>>> buffer = new LinkedList<>();

    private int bufferStartFrame;

    /**
     * Get the index of the first frame within the buffer.
     * @return First frame index.
     */
    public int getBufferStartFrame() {
        return bufferStartFrame;
    }

    // Index of any frame within the buffer = bufferStartFrame + bufferIndex.
    // The start frame is the global index of the first frame within the buffer.
    /**
     * Get the index of the last frame within the buffer.
     * @return Last frame index.
     */
    public int getBufferEndFrame() {
        return bufferStartFrame + buffer.size() - 1;
    }

    /**
     * Get the size of the buffer.
     * @return Size of the buffer, in frames.
     */
    public int getBufferSize() {
        return buffer.size();
    }

    /**
     * Add a frame to the buffer.
     * @param frame Frame contents.
     * @param frameIndex Frame index.
     */
    public void bufferFrame(Collection<ParsedChannelFrame<?>> frame, int frameIndex) {
        if (frameIndex != getBufferEndFrame() + 1) {
            buffer.clear();
            bufferStartFrame = frameIndex;
        }
        buffer.add(frame);
    }

    /**
     * Get the frame at the start of this buffer.
     * @return First buffer frame.
     */
    public Collection<ParsedChannelFrame<?>> peekFrame() {
        return buffer.peek();
    }

    /**
     * Get the index of the frame at the start of this buffer.
     * @return Index of the first buffer frame.
     */
    public int peekIndex() {
        return bufferStartFrame;
    }
    
    public Collection<ParsedChannelFrame<?>> popFrame() {
        var frame = buffer.pop();
        bufferStartFrame++;
        return frame;
    }
}
