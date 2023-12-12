package com.igrium.replayfps.core.recording;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.igrium.replayfps.core.channel.type.ChannelType;
import com.igrium.replayfps.core.playback.UnserializedFrame;

public class ClientCapWriter {
    private final OutputStream out;

    private int writtenFrames;

    /**
     * Create a ClientCap writer.
     * 
     * @param out    Output stream to write to.
     */
    public ClientCapWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Write a frame of animation.
     * 
     * @param frame Frame to write
     * @throws IOException If an IO exception occurs while writing the frame.
     */
    public void writeFrame(UnserializedFrame frame) throws IOException {
        try {
            DataOutputStream dataOut = new DataOutputStream(out);
            for (var entry : frame.getValues().entrySet()) {
                writeChannel(entry.getKey().getChannelType(), dataOut, entry.getValue());
            }
        } finally {
            writtenFrames++;

        }
    }

    // Seperate function needed to handle generic.
    private <T> void writeChannel(ChannelType<T> channelType, DataOutput out, Object value) throws IOException {
        T val = channelType.getType().cast(value);
        channelType.write(out, val);
    }


    /**
     * Get the number of frames that have been written.
     * @return Written frames.
     */
    public int getWrittenFrames() {
        return writtenFrames;
    }


}
