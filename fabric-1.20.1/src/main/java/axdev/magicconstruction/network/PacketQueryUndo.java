package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PacketQueryUndo {
    public static void handle(MinecraftServer server, ServerPlayerEntity player,
                              ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean undoPressed = buf.readBoolean();
        server.execute(() -> {
        });
    }

    public static void send(boolean undoPressed) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(undoPressed);
        ClientPlayNetworking.send(ModMessages.QUERY_UNDO_PACKET_ID, buf);
    }
}
