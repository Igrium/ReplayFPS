package com.igrium.replayfps.game;

import com.igrium.replayfps.util.SerializableField;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public final class DefaultSerializableFields {
    public static class BlockPosField extends SerializableField<BlockPos> {

        @Override
        protected BlockPos doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readBlockPos();
        }

        @Override
        protected void doWrite(BlockPos value, PacketByteBuf buffer) {
            buffer.writeBlockPos(value);
        }
        
    }

    public static class TextField extends SerializableField<Text> {

        @Override
        protected Text doRead(PacketByteBuf buffer) throws Exception {
            return buffer.readText();
        }

        @Override
        protected void doWrite(Text value, PacketByteBuf buffer) {
            buffer.writeText(value);
        }
        
    }
}
