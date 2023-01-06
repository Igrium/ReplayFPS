package com.igrium.replayfps.clientcap.channels;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public final class AnimChannels {
    private AnimChannels() {};

    public static final BiMap<String, AnimChannel<?>> REGISTRY = HashBiMap.create();

    static {
        REGISTRY.put("camerapos", new CameraPosChannel());
        REGISTRY.put("camerarot", new CameraRotChannel());
    }

    /**
     * Get a channel from the registry and cast it against a particular type.
     * 
     * @param <T>   The type of the channel.
     * @param name  Name of the channel to get.
     * @param clazz Channel type to cast to.
     * @return The channel, or <code>null</code> if no channel with that name was
     *         found.
     * @throws ClassCastException If the channel types do not match.
     */
    @Nullable
    public static <T> AnimChannel<T> getChannel(String name, Class<T> clazz) throws ClassCastException {
        AnimChannel<?> channel = REGISTRY.get(name);
        if (channel == null) return null;
        return castChannel(channel, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> AnimChannel<T> castChannel(AnimChannel<?> channel, Class<T> clazz) throws ClassCastException {
        if (!channel.getChannelClass().equals(clazz)) {
            throw new ClassCastException(clazz.getSimpleName()+" is not compatible with anim channel type: "+channel.getChannelClass());
        }
        return (AnimChannel<T>) channel;
    }

    /**
     * Get the name ID of an anim channel.
     * @param channel The channel.
     * @return The channel name.
     * @throws IllegalStateException If the channel has not been registered.
     */
    public static String getName(AnimChannel<?> channel) throws IllegalStateException {
        String name = REGISTRY.inverse().get(channel);
        if (name == null) {
            throw new IllegalStateException("That channel has not been registered!");
        }
        return name;
    }
}
