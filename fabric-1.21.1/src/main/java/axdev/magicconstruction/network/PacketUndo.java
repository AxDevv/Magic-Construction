package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import axdev.magicconstruction.MagicConstruction;

public record PacketUndo() implements CustomPayload {
    public static final Id<PacketUndo> ID = new Id<>(MagicConstruction.id("undo"));
    public static final PacketCodec<RegistryByteBuf, PacketUndo> CODEC = PacketCodec.unit(new PacketUndo());

    public static void handle(PacketUndo packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        context.server().execute(() -> {
            MagicConstruction.undoHistory.tryUndo(player, player.getServerWorld(), player.getMainHandStack());
        });
    }

    public static void send() {
        ClientPlayNetworking.send(new PacketUndo());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
