package com.igrium.replayfps.clientcap.channeltype;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents a single animation channel within the file.
 */
public interface ChannelType<T> {
    /**
     * Get the length of this channel in bytes.
     */
    int getLength();

    /**
     * Deserialize a frame of this channel.
     * @param in Input stream to deserialize.
     * @return Frame value.
     * @throws IOException If an IO exception occurs.
     */
    T read(DataInputStream in) throws IOException;

    /**
     * Serialize a frame of this channel.
     * @param out Output stream to serialize to.
     * @param val Frame value to serialize.
     * @throws IOException If an IO exception occurs.
     */
    void write(DataOutputStream out, T val) throws IOException;
}
