package axdev.magicconstruction.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModMessages {
    private static final String PROTOCOL_VERSION = "1";

    private ModMessages() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModMessages::registerPayloads);
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(
                PacketUndoBlocks.TYPE,
                PacketUndoBlocks.STREAM_CODEC,
                PacketUndoBlocks::handle
        );

        registrar.playToServer(
                PacketQueryUndo.TYPE,
                PacketQueryUndo.STREAM_CODEC,
                PacketQueryUndo::handle
        );

        registrar.playToServer(
                PacketUndo.TYPE,
                PacketUndo.STREAM_CODEC,
                PacketUndo::handle
        );
    }

    public static <MSG extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}
