package com.igrium.replayfps.clientcap.animchannels;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;

public final class AnimChannels {
    private AnimChannels() {};

    public static final BiMap<String, AnimChannelType<?>> REGISTRY = HashBiMap.create();

    public static final CameraPosChannelType CAMERA_POS = new CameraPosChannelType();
    public static final CameraRotChannelType CAMERA_ROT = new CameraRotChannelType();
    public static final FovChannelType FOV = new FovChannelType();

    static {
        REGISTRY.put("camerapos", CAMERA_POS);
        REGISTRY.put("camerarot", CAMERA_ROT);
        REGISTRY.put("fov", FOV);
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
    public static <T> AnimChannelType<T> getChannel(String name, Class<T> clazz) throws ClassCastException {
        AnimChannelType<?> channel = REGISTRY.get(name);
        if (channel == null) return null;
        return castChannel(channel, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> AnimChannelType<T> castChannel(AnimChannelType<?> channel, Class<T> clazz) throws ClassCastException {
        if (!channel.getChannelClass().equals(clazz)) {
            throw new ClassCastException(clazz.getSimpleName()+" is not compatible with anim channel type: "+channel.getChannelClass());
        }
        return (AnimChannelType<T>) channel;
    }

    /**
     * Get the name ID of an anim channel.
     * @param channel The channel.
     * @return The channel name.
     * @throws IllegalStateException If the channel has not been registered.
     */
    public static String getName(AnimChannelType<?> channel) throws IllegalStateException {
        String name = REGISTRY.inverse().get(channel);
        if (name == null) {
            throw new IllegalStateException("That channel has not been registered!");
        }
        return name;
    }

    private static List<AnimChannelType<?>> standardChannels = ImmutableList.of(CAMERA_POS, CAMERA_ROT, FOV);

    public static List<AnimChannelType<?>> getStandardChannels() {
        return standardChannels;
    }
}
