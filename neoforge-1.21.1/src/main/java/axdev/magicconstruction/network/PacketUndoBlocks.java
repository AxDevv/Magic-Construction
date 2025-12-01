package axdev.magicconstruction.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import axdev.magicconstruction.MagicConstruction;

import java.util.HashSet;
import java.util.Set;

public record PacketUndoBlocks(HashSet<BlockPos> undoBlocks) implements CustomPacketPayload
{
    public static final Type<PacketUndoBlocks> TYPE = new Type<>(MagicConstruction.loc("undo_blocks"));

    public static final StreamCodec<FriendlyByteBuf, PacketUndoBlocks> STREAM_CODEC = StreamCodec.of(
            PacketUndoBlocks::encode,
            PacketUndoBlocks::decode
    );

    public PacketUndoBlocks(Set<BlockPos> undoBlocks) {
        this(new HashSet<>(undoBlocks));
    }

    public static void encode(FriendlyByteBuf buffer, PacketUndoBlocks msg) {
        buffer.writeInt(msg.undoBlocks.size());
        for(BlockPos pos : msg.undoBlocks) {
            buffer.writeBlockPos(pos);
        }
    }

    public static PacketUndoBlocks decode(FriendlyByteBuf buffer) {
        HashSet<BlockPos> undoBlocks = new HashSet<>();
        int size = buffer.readInt();
        for(int i = 0; i < size; i++) {
            undoBlocks.add(buffer.readBlockPos());
        }
        return new PacketUndoBlocks(undoBlocks);
    }

    public static void handle(final PacketUndoBlocks msg, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            MagicConstruction.instance.renderBlockPreview.setUndoBlocks(msg.undoBlocks);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
