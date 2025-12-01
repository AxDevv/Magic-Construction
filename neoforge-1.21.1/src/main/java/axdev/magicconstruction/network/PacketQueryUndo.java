package axdev.magicconstruction.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import axdev.magicconstruction.MagicConstruction;

public record PacketQueryUndo(boolean undoPressed) implements CustomPacketPayload
{
    public static final Type<PacketQueryUndo> TYPE = new Type<>(MagicConstruction.loc("query_undo"));

    public static final StreamCodec<FriendlyByteBuf, PacketQueryUndo> STREAM_CODEC = StreamCodec.of(
            PacketQueryUndo::encode,
            PacketQueryUndo::decode
    );

    public static void encode(FriendlyByteBuf buffer, PacketQueryUndo msg) {
        buffer.writeBoolean(msg.undoPressed);
    }

    public static PacketQueryUndo decode(FriendlyByteBuf buffer) {
        return new PacketQueryUndo(buffer.readBoolean());
    }

    public static void handle(final PacketQueryUndo msg, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if(ctx.player() instanceof ServerPlayer player) {
                MagicConstruction.instance.undoHistory.updateClient(player, msg.undoPressed);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
