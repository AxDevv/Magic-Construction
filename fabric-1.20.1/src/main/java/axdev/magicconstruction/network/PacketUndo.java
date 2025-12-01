package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import axdev.magicconstruction.MagicConstruction;

public class PacketUndo {
    public static void handle(MinecraftServer server, ServerPlayerEntity player,
                              ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            MagicConstruction.undoHistory.tryUndo(player, player.getServerWorld(), player.getMainHandStack());
        });
    }

    public static void send() {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(ModMessages.UNDO_PACKET_ID, buf);
    }
}
