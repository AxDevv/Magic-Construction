package axdev.magicconstruction.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import axdev.magicconstruction.MagicConstruction;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketUndoBlocks {
    private final HashSet<BlockPos> undoBlocks;

    public PacketUndoBlocks(Set<BlockPos> undoBlocks) {
        this.undoBlocks = new HashSet<>(undoBlocks);
    }

    public PacketUndoBlocks(FriendlyByteBuf buffer) {
        undoBlocks = new HashSet<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++) {
            undoBlocks.add(buffer.readBlockPos());
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(undoBlocks.size());
        for(BlockPos pos : undoBlocks) {
            buffer.writeBlockPos(pos);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            MagicConstruction.instance.renderBlockPreview.setUndoBlocks(undoBlocks);
        });
        ctx.get().setPacketHandled(true);
    }
}
