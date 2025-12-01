package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import axdev.magicconstruction.MagicConstruction;

public record PacketQueryUndo(boolean undoPressed) implements CustomPayload {
    public static final Id<PacketQueryUndo> ID = new Id<>(MagicConstruction.id("query_undo"));
    public static final PacketCodec<RegistryByteBuf, PacketQueryUndo> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, PacketQueryUndo::undoPressed,
            PacketQueryUndo::new
    );

    public static void handle(PacketQueryUndo packet, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
        });
    }

    public static void send(boolean undoPressed) {
        ClientPlayNetworking.send(new PacketQueryUndo(undoPressed));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
