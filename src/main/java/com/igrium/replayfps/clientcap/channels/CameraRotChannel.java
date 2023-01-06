package com.igrium.replayfps.clientcap.channels;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.channeltype.ChannelTypes;
import com.igrium.replayfps.clientcap.channeltype.QuaternionChannelType;

public class CameraRotChannel implements AnimChannel<Quaternionfc> {

    @Override
    public Quaternionf capture(ClientCaptureContext context) {
        // So we don't get fucked over by quaternion mutability.
        return new Quaternionf(context.camera().getRotation());
    }

    @Override
    public void apply(Quaternionfc frame) {
        
    }

    @Override
    public QuaternionChannelType getChannelType() {
        return ChannelTypes.QUATERNION;
    }
    
    @Override
    public Class<Quaternionfc> getChannelClass() {
        return Quaternionfc.class;
    }
}
