package com.igrium.replayfps.playback;

import com.igrium.replayfps.channel.handler.ChannelHandler;
import com.igrium.replayfps.recording.ClientCapHeader;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

/**
 * Represents the contents of a single frame before its written to disk.
 */
public record UnserializedFrame(ClientCapHeader header, Object[] values) {

    public UnserializedFrame(ClientCapHeader header, Object[] values) {
        if (values.length != header.numChannels()) {
            throw new IllegalArgumentException("Incorrect number of channels.");
        }

        this.header = header;
        this.values = values;
    }

    public UnserializedFrame(ClientCapHeader header) {
        this(header, new Object[header.numChannels()]);
    }

    /**
     * Get a map of all channels and their respective values.
     * @return Channel map.
     */
    public Map<ChannelHandler<?>, Object> getValues() {
        return new ChannelMap();
    }

    /**
     * Get the value belonging to a particular channel.
     * 
     * @param <T>     Channel type.
     * @param channel The channel.
     * @return The value. <code>null</code> if it does not exist for this frame.
     * @throws ClassCastException If the value exists but is of the wrong type.
     *                            Should not happen if deserialized correctly.
     */
    public <T> T getValue(ChannelHandler<T> channel) throws ClassCastException {
        Object value = getValues().get(channel);
        if (value == null) return null;

        return channel.getType().cast(value);
    }

    private class ChannelMap extends AbstractMap<ChannelHandler<?>, Object> {
        private ChannelEntrySet entrySet = new ChannelEntrySet();

        @Override
        public Set<Map.Entry<ChannelHandler<?>, Object>> entrySet() {
            return entrySet;
        }
    }

    private class ChannelEntrySet extends AbstractSet<Map.Entry<ChannelHandler<?>, Object>> {

        @Override
        public int size() {
            return values.length;
        }

        @Override
        public Iterator<Map.Entry<ChannelHandler<?>, Object>> iterator() {
            return new ChannelIterator();
        }

        @Override
        public boolean add(Map.Entry<ChannelHandler<?>, Object> e) {
            throw new UnsupportedOperationException("Unimplemented method 'add'");
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<ChannelHandler<?>, Object>> c) {
            throw new UnsupportedOperationException("Unimplemented method 'addAll'");
        }

    }

    private class ChannelIterator implements Iterator<Map.Entry<ChannelHandler<?>, Object>> {

        int currentIndex;

        @Override
        public boolean hasNext() {
            return currentIndex < values.length;
        }

        @Override
        public Map.Entry<ChannelHandler<?>, Object> next() {
            ChannelHandler<?> key = header.getChannels().get(currentIndex);
            Object value = values[currentIndex];
            currentIndex++;
            return new AbstractMap.SimpleEntry<>(key, value);
        }
        
    }
}