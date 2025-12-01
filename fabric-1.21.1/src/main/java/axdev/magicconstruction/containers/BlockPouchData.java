package axdev.magicconstruction.containers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BlockPouchData(int slot) {
    public static final Codec<BlockPouchData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("slot").forGetter(BlockPouchData::slot)
            ).apply(instance, BlockPouchData::new)
    );

    public static final PacketCodec<RegistryByteBuf, BlockPouchData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, BlockPouchData::slot,
            BlockPouchData::new
    );
}
