package axdev.magicconstruction.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import axdev.magicconstruction.MagicConstruction;

public record PacketUndo() implements CustomPacketPayload {
    public static final Type<PacketUndo> TYPE = new Type<>(MagicConstruction.loc("undo"));

    public static final StreamCodec<FriendlyByteBuf, PacketUndo> STREAM_CODEC = StreamCodec.of(
            PacketUndo::encode,
            PacketUndo::decode
    );

    public static void encode(FriendlyByteBuf buffer, PacketUndo msg) {
    }

    public static PacketUndo decode(FriendlyByteBuf buffer) {
        return new PacketUndo();
    }

    public static void handle(final PacketUndo msg, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if(ctx.player() instanceof ServerPlayer player) {
                MagicConstruction.instance.undoHistory.tryUndo(player);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
