package com.igrium.replayfps.game.screen;

import java.util.Arrays;
import java.util.Optional;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.game.mixin.AbstractSignEditScreenAccessor;
import com.igrium.replayfps.util.DynamicSerializable;
import com.igrium.replayfps.util.SerializableField;
import com.igrium.replayfps.util.ArrayField.StringArrayField;
import com.igrium.replayfps.util.SerializableFields.BoolField;
import com.igrium.replayfps.util.SerializableFields.IntField;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class SignEditScreenSerializer implements ScreenSerializer<SignEditScreen, SignEditScreenValue> {

    @Override
    public Class<SignEditScreen> getScreenType() {
        return SignEditScreen.class;
    }

    @Override
    public Class<SignEditScreenValue> getSerializedType() {
        return SignEditScreenValue.class;
    }

    @Override
    public SignEditScreenValue readBuffer(PacketByteBuf buffer) throws Exception {
        SignEditScreenValue val = new SignEditScreenValue();
        val.serializer.read(buffer);
        return val;
    }

    @Override
    public void writeBuffer(SignEditScreenValue value, PacketByteBuf buffer) {
        value.serializer.write(buffer);
    }

    @Override
    public SignEditScreenValue serialize(SignEditScreen screen) {
        SignEditScreenValue value = new SignEditScreenValue();

        AbstractSignEditScreenAccessor accessor = (AbstractSignEditScreenAccessor) screen;
        SignBlockEntity blockEntity = accessor.getBlockEntity();

        value.blockPos.set(blockEntity.getPos());
        
        String[] messages = accessor.getMessages().clone();
        value.messages.set(messages);
        
        value.front.set(accessor.isFront());
        value.currentRow.set(accessor.getCurrentRow());

        return value;
    }

    @Override
    public void apply(MinecraftClient client, SignEditScreenValue value, SignEditScreen screen) {
        AbstractSignEditScreenAccessor accessor = (AbstractSignEditScreenAccessor) screen;

        if (value.messages.isPresent()) {
            String[] screenMessages = accessor.getMessages();
            String[] serializedMessages = value.messages.get();
            for (int i = 0; i < screenMessages.length && i < serializedMessages.length; i++) {
                screenMessages[i] = serializedMessages[i];
            }
        }

        if (value.currentRow.isPresent())
            accessor.setCurrentRow(value.currentRow.getInt());

    }

    @Override
    public SignEditScreen create(MinecraftClient client, SignEditScreenValue value) {
        Optional<SignBlockEntity> opt = client.world.getBlockEntity(value.blockPos.get(), BlockEntityType.SIGN);
        SignBlockEntity ent = opt.orElseThrow(() -> new IllegalStateException("No sign block entity found."));

        return new SignEditScreen(ent, value.front.getBool(), client.shouldFilterText());

    }

    @Override
    public boolean hasChanged(SignEditScreen screen, SignEditScreenValue value) {
        AbstractSignEditScreenAccessor accessor = (AbstractSignEditScreenAccessor) screen;
        if (value.messages.isPresent() && !Arrays.equals(value.messages.get(), accessor.getMessages()))
            return true;
        else if (value.front.isPresent() && value.front.getBool() != accessor.isFront())
            return true;
        else if (value.currentRow.isPresent() && value.currentRow.getInt() != accessor.getCurrentRow())
            return true;
        else return false;
    }
}

record OldSignEditScreenValue(BlockPos pos, String[] messages, boolean front, int currentRow) {
};

class SignEditScreenValue {
    final BlockPosField blockPos = new BlockPosField();
    final StringArrayField messages = new StringArrayField();
    final BoolField front = new BoolField();
    final IntField currentRow = new IntField();

    final DynamicSerializable serializer = new DynamicSerializable(
        blockPos,
        messages,
        front,
        currentRow
    );
}

class BlockPosField extends SerializableField<BlockPos> {

    @Override
    protected BlockPos doRead(PacketByteBuf buffer) {
        return buffer.readBlockPos();
    }

    @Override
    protected void doWrite(BlockPos value, PacketByteBuf buffer) {
        buffer.writeBlockPos(value);
    }
    
}