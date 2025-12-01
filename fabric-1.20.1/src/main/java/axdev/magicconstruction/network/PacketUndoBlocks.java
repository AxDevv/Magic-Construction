package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.client.RenderBlockPreview;

import java.util.HashSet;
import java.util.Set;

public class PacketUndoBlocks {
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler,
                              PacketByteBuf buf, PacketSender responseSender) {
        int size = buf.readInt();
        HashSet<BlockPos> undoBlocks = new HashSet<>();
        for (int i = 0; i < size; i++) {
            undoBlocks.add(buf.readBlockPos());
        }

        client.execute(() -> {
            RenderBlockPreview.setUndoBlocks(undoBlocks);
        });
    }

    public static void send(ServerPlayerEntity player, Set<BlockPos> blocks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(blocks.size());
        for (BlockPos pos : blocks) {
            buf.writeBlockPos(pos);
        }
        ServerPlayNetworking.send(player, ModMessages.UNDO_BLOCKS_PACKET_ID, buf);
    }
}
