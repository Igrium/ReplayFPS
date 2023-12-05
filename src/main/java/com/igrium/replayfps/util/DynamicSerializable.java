package com.igrium.replayfps.util;

import java.util.Collection;

import net.minecraft.network.PacketByteBuf;

/**
 * <p>
 * An object that can be serialized or deserialized with a dynamic amount of
 * values.
 * </p>
 * <p>
 * When serialized, a series of bits in the form of an integer is written,
 * declaring which fields contain values. These order of these bits is defined
 * by the order in which fields were passed in the constructor. Therefore, this
 * must be stable across runs.
 * </p>
 * 
 */
public class DynamicSerializable {

    private final SerializableField<?>[] fields;

    public DynamicSerializable(Collection<SerializableField<?>> fields) {
        this.fields = fields.toArray(SerializableField<?>[]::new);
    }

    public DynamicSerializable(SerializableField<?>... fields) {
        this.fields = fields.clone();
    }

    public SerializableField<?>[] getFields() {
        return fields;
    }

    public void read(PacketByteBuf buffer) throws Exception {
        int bitSet = buffer.readInt();

        for (int i = 0; i < fields.length; i++) {
            if (isNthBitSet(bitSet, i)) {
                fields[i].read(buffer);
            }
        }
        
    }

    public void write(PacketByteBuf buffer) {
        int bitSet = 0;

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].optional().isPresent()) {
                bitSet = setBit(bitSet, i);
            }
        }

        buffer.writeInt(bitSet);

        for (SerializableField<?> field : fields) {
            if (field.optional().isPresent()) field.write(buffer);
        }
    }

    private static boolean isNthBitSet(int bitSet, int n) {
        return ((bitSet >> n) & 1) == 1;
    }

    private static int setBit(int bitSet, int n) {
        return bitSet |= 1 << n;
    }
}
