package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import axdev.magicconstruction.MagicConstruction;

public class ModMessages {
    public static final Identifier UNDO_PACKET_ID = MagicConstruction.id("undo");
    public static final Identifier QUERY_UNDO_PACKET_ID = MagicConstruction.id("query_undo");
    public static final Identifier UNDO_BLOCKS_PACKET_ID = MagicConstruction.id("undo_blocks");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(UNDO_PACKET_ID, PacketUndo::handle);
        ServerPlayNetworking.registerGlobalReceiver(QUERY_UNDO_PACKET_ID, PacketQueryUndo::handle);
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(UNDO_BLOCKS_PACKET_ID, PacketUndoBlocks::handle);
    }

    public static void sendToServer(net.minecraft.network.PacketByteBuf buf, Identifier id) {
        ClientPlayNetworking.send(id, buf);
    }
}
