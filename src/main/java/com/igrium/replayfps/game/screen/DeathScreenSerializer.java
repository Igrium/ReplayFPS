package com.igrium.replayfps.game.screen;

import com.igrium.replayfps.core.screen.ScreenSerializer;
import com.igrium.replayfps.game.mixin.DeathScreenAccessor;
import com.igrium.replayfps.util.DynamicSerializable;
import com.igrium.replayfps.util.SerializableFields.BoolField;
import com.igrium.replayfps.util.SerializableFields.StringField;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

public class DeathScreenSerializer implements ScreenSerializer<DeathScreen, DeathScreenValue> {

    @Override
    public Class<DeathScreen> getScreenType() {
        return DeathScreen.class;
    }

    @Override
    public Class<DeathScreenValue> getSerializedType() {
        return DeathScreenValue.class;
    }

    @Override
    public DeathScreenValue readBuffer(PacketByteBuf buffer) throws Exception {
        DeathScreenValue value = new DeathScreenValue();
        value.serializer.read(buffer);
        return value;
    }

    @Override
    public void writeBuffer(DeathScreenValue value, PacketByteBuf buffer) {
        value.serializer.write(buffer);
    }

    @Override
    public DeathScreenValue serialize(DeathScreen screen) {
        DeathScreenAccessor accessor = (DeathScreenAccessor) screen;
        DeathScreenValue val = new DeathScreenValue();

        val.message.set(accessor.getMessage().getString());
        val.isHardcore.set(accessor.isHardcore());
        val.scoreText.set(accessor.getScoreText().getString());

        return val;
    }

    @Override
    public void apply(MinecraftClient client, DeathScreenValue value, DeathScreen screen) {
        value.scoreText.optional().ifPresent(scoreText -> {
            ((DeathScreenAccessor) screen).setScoreText(Text.literal(scoreText));
        });
    }

    @Override
    public DeathScreen create(MinecraftClient client, DeathScreenValue value) throws Exception {
        String message = value.message.optional().orElse("[Unknown death message]");
        boolean hardcore = value.isHardcore.getBool();
        return new DeathScreen(Text.literal(message), hardcore);

    }

    @Override
    public boolean hasChanged(DeathScreen screen, DeathScreenValue value) {
        return value.scoreText.isPresent()
                && !value.scoreText.get().equals(((DeathScreenAccessor) screen).getScoreText().getString());
    }
    
}

class DeathScreenValue {
    public final StringField message = new StringField();
    public final BoolField isHardcore = new BoolField();
    public final StringField scoreText = new StringField();

    public final DynamicSerializable serializer = new DynamicSerializable(
        message, isHardcore, scoreText
    );
}