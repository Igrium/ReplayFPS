package com.igrium.replayfps.channel.handler;

import org.joml.Matrix4fc;

import com.igrium.replayfps.channel.type.ChannelType;
import com.igrium.replayfps.channel.type.ChannelTypes;
import com.igrium.replayfps.recording.ClientCaptureContext;

public class ViewMatrixChannelHandler implements ChannelHandler<Matrix4fc> {

    @Override
    public ChannelType<Matrix4fc> getChannelType() {
        return ChannelTypes.MATRIX4F;
    }

    @Override
    public Matrix4fc capture(ClientCaptureContext context) {
        return context.renderContext().matrixStack().peek().getPositionMatrix();
    }

    @Override
    public void apply(Matrix4fc val) {
        
    }
    
}
