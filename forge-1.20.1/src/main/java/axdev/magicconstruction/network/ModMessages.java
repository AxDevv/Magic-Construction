package axdev.magicconstruction.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import axdev.magicconstruction.MagicConstruction;

import java.util.Optional;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MagicConstruction.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        INSTANCE.messageBuilder(PacketUndoBlocks.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketUndoBlocks::new)
                .encoder(PacketUndoBlocks::encode)
                .consumerMainThread(PacketUndoBlocks::handle)
                .add();

        INSTANCE.messageBuilder(PacketQueryUndo.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketQueryUndo::new)
                .encoder(PacketQueryUndo::encode)
                .consumerMainThread(PacketQueryUndo::handle)
                .add();

        INSTANCE.messageBuilder(PacketUndo.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketUndo::new)
                .encoder(PacketUndo::encode)
                .consumerMainThread(PacketUndo::handle)
                .add();

        INSTANCE.messageBuilder(PacketOpenPouch.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketOpenPouch::new)
                .encoder(PacketOpenPouch::encode)
                .consumerMainThread(PacketOpenPouch::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
