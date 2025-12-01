package axdev.magicconstruction.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import axdev.magicconstruction.MagicConstruction;

import java.util.function.Supplier;

public class PacketUndo {
    public PacketUndo() {
    }

    public PacketUndo(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if(player != null) {
                MagicConstruction.instance.undoHistory.tryUndo(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
