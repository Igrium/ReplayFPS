package com.igrium.replayfps.core.playback;

import java.util.Collections;
import java.util.HashMap;

import java.util.Map;
import com.igrium.replayfps.core.channel.ChannelHandler;

public class ChannelValueCache {
    private Map<ChannelHandler<?>, Object> map = new HashMap<>();
    private Map<ChannelHandler<?>, Object> unmodifiable = Collections.unmodifiableMap(map);

    // private List<CachedChannel<?>> list = new ArrayList<>();

    public <T> void put(ChannelHandler<T> channel, T value) {
        map.put(channel, value);
    }

    // Because we control the only way to add items to the map, we know this matches.
    @SuppressWarnings("unchecked")
    public <T> T get(ChannelHandler<T> channel) {
        return (T) map.get(channel);
    }

    public Map<ChannelHandler<?>, Object> map() {
        return unmodifiable;
    }

    // Because we control the only way to add items to the map, we know this matches.
    @SuppressWarnings("unchecked")
    public <T> T remove(ChannelHandler<T> channel) {
        return (T) map.remove(channel);
    }

    public void clear() {
        map.clear();
    }

    public void forEach(ChannelValueConsumer consumer) {
        for (var entry : map.entrySet()) {
            doCast(consumer, entry.getKey(), entry.getValue());
        }
    }

    // Arent generics fun??
    private <T> void doCast(ChannelValueConsumer consumer, ChannelHandler<T> channel, Object value) {
        consumer.accept(channel, channel.getType().cast(value));
    }

    public interface ChannelValueConsumer {
        <T> void accept(ChannelHandler<T> channel, T value);
    }
}
