package axdev.magicconstruction.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.client.RenderBlockPreview;

import java.util.HashSet;
import java.util.Set;

public record PacketUndoBlocks(HashSet<BlockPos> undoBlocks) implements CustomPayload {
    public static final Id<PacketUndoBlocks> ID = new Id<>(MagicConstruction.id("undo_blocks"));
    public static final PacketCodec<RegistryByteBuf, PacketUndoBlocks> CODEC = new PacketCodec<>() {
        @Override
        public PacketUndoBlocks decode(RegistryByteBuf buf) {
            int size = buf.readInt();
            HashSet<BlockPos> blocks = new HashSet<>();
            for (int i = 0; i < size; i++) {
                blocks.add(buf.readBlockPos());
            }
            return new PacketUndoBlocks(blocks);
        }

        @Override
        public void encode(RegistryByteBuf buf, PacketUndoBlocks packet) {
            buf.writeInt(packet.undoBlocks.size());
            for (BlockPos pos : packet.undoBlocks) {
                buf.writeBlockPos(pos);
            }
        }
    };

    public static void handle(PacketUndoBlocks packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            RenderBlockPreview.setUndoBlocks(packet.undoBlocks);
        });
    }

    public static void send(ServerPlayerEntity player, Set<BlockPos> blocks) {
        ServerPlayNetworking.send(player, new PacketUndoBlocks(new HashSet<>(blocks)));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
