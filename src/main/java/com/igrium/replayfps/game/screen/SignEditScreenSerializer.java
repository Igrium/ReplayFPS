package com.igrium.replayfps.game.screen;

import java.util.Arrays;
import java.util.Optional;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.game.mixin.AbstractSignEditScreenAccessor;

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
    public SignEditScreenValue read(PacketByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        byte numMessages = buffer.readByte();
        String[] messages = new String[numMessages];

        for (int i = 0; i < numMessages; i++) {
            messages[i] = buffer.readString();
        }

        boolean front = buffer.readBoolean();
        int currentRow = buffer.readInt();

        return new SignEditScreenValue(pos, messages, front, currentRow);
    }

    @Override
    public void write(SignEditScreenValue value, PacketByteBuf buffer) {
        buffer.writeBlockPos(value.pos());

        if (value.messages().length > Byte.MAX_VALUE) {
            throw new IllegalStateException("Too many messages.");
        }

        buffer.writeByte(value.messages().length);
        for (var message : value.messages()) {
            buffer.writeString(message);
        }

        buffer.writeBoolean(value.front());
        buffer.writeInt(value.currentRow());
    }

    @Override
    public SignEditScreenValue parse(SignEditScreen screen) {
        AbstractSignEditScreenAccessor accessor = (AbstractSignEditScreenAccessor) screen;
        SignBlockEntity blockEntity = accessor.getBlockEntity();

        String[] messages = accessor.getMessages();
        messages = Arrays.copyOf(messages, messages.length);

        boolean front = accessor.isFront();
        int currentRow = accessor.getCurrentRow();

        return new SignEditScreenValue(blockEntity.getPos(), messages, front, currentRow);
    }

    @Override
    public void apply(MinecraftClient client, SignEditScreenValue value, SignEditScreen screen) {
        String[] screenMessages = ((AbstractSignEditScreenAccessor) screen).getMessages();
        for (int i = 0; i < screenMessages.length && i < value.messages().length; i++) {
            screenMessages[i] = value.messages()[i];
        }

        ((AbstractSignEditScreenAccessor) screen).setCurrentRow(value.currentRow());
    }

    @Override
    public SignEditScreen create(MinecraftClient client, SignEditScreenValue value) {
        Optional<SignBlockEntity> opt = client.world.getBlockEntity(value.pos(), BlockEntityType.SIGN);
        SignBlockEntity ent = opt.orElseThrow(() -> new IllegalStateException("No sign block entity found."));

        return new SignEditScreen(ent, value.front(), client.shouldFilterText());
    }

    @Override
    public boolean hasChanged(SignEditScreen screen, SignEditScreenValue value) {
        AbstractSignEditScreenAccessor accessor = (AbstractSignEditScreenAccessor) screen;

        if (!Arrays.equals(accessor.getMessages(), value.messages())) return true;
        else if (accessor.isFront() != value.front()) return true;
        else if (accessor.getCurrentRow() != value.currentRow()) return true;
        else return false;
    }
}

record SignEditScreenValue(BlockPos pos, String[] messages, boolean front, int currentRow) {
};