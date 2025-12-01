package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import axdev.magicconstruction.MagicConstruction;

public class ModMessages {
    public static final Identifier UNDO_PACKET_ID = MagicConstruction.id("undo");
    public static final Identifier QUERY_UNDO_PACKET_ID = MagicConstruction.id("query_undo");
    public static final Identifier UNDO_BLOCKS_PACKET_ID = MagicConstruction.id("undo_blocks");

    public static void registerCommon() {
        PayloadTypeRegistry.playC2S().register(PacketUndo.ID, PacketUndo.CODEC);
        PayloadTypeRegistry.playC2S().register(PacketQueryUndo.ID, PacketQueryUndo.CODEC);
        PayloadTypeRegistry.playS2C().register(PacketUndoBlocks.ID, PacketUndoBlocks.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(PacketUndo.ID, PacketUndo::handle);
        ServerPlayNetworking.registerGlobalReceiver(PacketQueryUndo.ID, PacketQueryUndo::handle);
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(PacketUndoBlocks.ID, PacketUndoBlocks::handle);
    }
}
