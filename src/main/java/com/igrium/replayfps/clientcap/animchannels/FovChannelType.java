package com.igrium.replayfps.clientcap.animchannels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.ClientPlaybackContext;
import com.igrium.replayfps.clientcap.channeltype.DoubleChannelType;

public class FovChannelType extends DoubleChannelType implements AnimChannelType<Double> {

    @Override
    public Double capture(ClientCaptureContext context) {
        return context.fov();
    }

    @Override
    public void apply(Double frame, ClientPlaybackContext context) {
        
    }

    @Override
    public Class<Double> getChannelClass() {
        return Double.class;
    }
    
}
