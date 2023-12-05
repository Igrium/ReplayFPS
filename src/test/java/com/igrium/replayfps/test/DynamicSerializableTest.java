package com.igrium.replayfps.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import com.igrium.replayfps.util.DynamicSerializable;
import com.igrium.replayfps.util.SerializableFields.ByteField;
import com.igrium.replayfps.util.SerializableFields.DoubleField;
import com.igrium.replayfps.util.SerializableFields.FloatField;
import com.igrium.replayfps.util.SerializableFields.IntField;
import com.igrium.replayfps.util.SerializableFields.LongField;
import com.igrium.replayfps.util.SerializableFields.ShortField;
import com.igrium.replayfps.util.SerializableFields.StringField;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

@ExtendWith(RandomBeansExtension.class)
public class DynamicSerializableTest {


    @RepeatedTest(5)
    public void testDynamicSerializable(@Random byte randByte, @Random short randShort, @Random int randInt,
            @Random long randLong, @Random float randFloat, @Random double randDouble, @Random String randString,
            @Random(size = 7, type = Boolean.class) List<Boolean> include) throws Exception {

        ByteField byteField = new ByteField();
        if (include.get(0)) byteField.set(randByte);

        ShortField shortField = new ShortField();
        if (include.get(1)) shortField.set(randShort);

        IntField intField = new IntField();
        if (include.get(2)) intField.set(randInt);

        StringField stringField = new StringField();
        if (include.get(3)) stringField.set(randString);

        LongField longField = new LongField();
        if (include.get(4)) longField.set(randLong);

        FloatField floatField = new FloatField();
        if (include.get(5)) floatField.set(randFloat);

        DoubleField doubleField = new DoubleField();
        if (include.get(6)) doubleField.set(randDouble);

        DynamicSerializable serializable = new DynamicSerializable(byteField, shortField, intField, stringField,
                longField, floatField, doubleField);
        
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.markReaderIndex();

        serializable.write(buffer);
        buffer.resetReaderIndex();

        byteField.clear();
        shortField.clear();
        intField.clear();
        stringField.clear();
        longField.clear();
        floatField.clear();
        doubleField.clear();
        
        DynamicSerializable val2 = new DynamicSerializable(byteField, shortField, intField, stringField, longField, floatField, doubleField);
        val2.read(buffer);

        if (include.get(0)) assertEquals(randByte, byteField.getByte());
        if (include.get(1)) assertEquals(randShort, shortField.getShort());
        if (include.get(2)) assertEquals(randInt, intField.getInt());
        if (include.get(3)) assertEquals(randString, stringField.get());
        if (include.get(4)) assertEquals(randLong, longField.getLong());
        if (include.get(5)) assertEquals(randFloat, floatField.getFloat());
        if (include.get(6)) assertEquals(randDouble, doubleField.get());
    }
}
