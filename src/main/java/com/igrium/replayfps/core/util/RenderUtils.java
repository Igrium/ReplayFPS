package com.igrium.replayfps.core.util;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public final class RenderUtils {

    /**
     * For some really dumb reason, all the methods in <code>DrawContext</code> take ints instead of floats.
     */
    public static void drawTexturedQuad(Identifier texture,
            float x1, float x2, float y1, float y2, float z,
            float u1, float u2, float v1, float v2, MatrixStack matrices) {
        
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, x1, y2, z).texture(u1, v2).next();
        bufferBuilder.vertex(matrix, x2, y2, z).texture(u2, v2).next();
        bufferBuilder.vertex(matrix, x2, y1, z).texture(u2, v1).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

}
