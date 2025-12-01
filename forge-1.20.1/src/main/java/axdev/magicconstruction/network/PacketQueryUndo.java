package axdev.magicconstruction.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import axdev.magicconstruction.MagicConstruction;

import java.util.function.Supplier;

public class PacketQueryUndo {
    private final boolean undoPressed;

    public PacketQueryUndo(boolean undoPressed) {
        this.undoPressed = undoPressed;
    }

    public PacketQueryUndo(FriendlyByteBuf buffer) {
        this.undoPressed = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(undoPressed);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player != null) {
                MagicConstruction.instance.undoHistory.updateClient(player, undoPressed);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
