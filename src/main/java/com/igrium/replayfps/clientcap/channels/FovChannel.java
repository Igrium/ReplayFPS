package com.igrium.replayfps.clientcap.channels;

import com.igrium.replayfps.clientcap.ClientCaptureContext;
import com.igrium.replayfps.clientcap.channeltype.ChannelTypes;
import com.igrium.replayfps.clientcap.channeltype.DoubleChannelType;

public class FovChannel implements AnimChannel<Double> {

    @Override
    public Double capture(ClientCaptureContext context) {
        return context.fov();
    }

    @Override
    public void apply(Double frame) {
        
    }

    @Override
    public DoubleChannelType getChannelType() {
        return ChannelTypes.DOUBLE;
    }

    @Override
    public Class<Double> getChannelClass() {
        return Double.class;
    }
    
}
