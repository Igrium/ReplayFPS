package com.igrium.replayfps.clientcap.animchannels;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.QuaternionChannelType;
import com.igrium.replayfps.util.CameraUtils;

public class CameraRotChannelType extends QuaternionChannelType implements AnimChannelType<Quaternionfc> {

    @Override
    public Quaternionf capture(ClientCaptureContext context) {
        // So we don't get fucked over by quaternion mutability.
        return new Quaternionf(context.camera().getRotation());
    }

    @Override
    public void apply(Quaternionfc frame, ClientPlaybackContext context) {
        CameraUtils.setRotation(context.camera(), frame);
    }
    
    @Override
    public Class<Quaternionfc> getChannelClass() {
        return Quaternionfc.class;
    }
}
