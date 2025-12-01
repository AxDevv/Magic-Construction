package axdev.magicconstruction.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import axdev.magicconstruction.containers.BlockPouchMenu;
import axdev.magicconstruction.items.wand.ItemWand;

import java.util.function.Supplier;

public class PacketOpenPouch {
    public PacketOpenPouch() {
    }

    public PacketOpenPouch(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack wand = player.getMainHandItem();
            if (!(wand.getItem() instanceof ItemWand)) {
                wand = player.getOffhandItem();
            }
            if (!(wand.getItem() instanceof ItemWand)) return;
            if (!ItemWand.hasPouch(wand)) return;

            ItemStack finalWand = wand;
            NetworkHooks.openScreen(player, new SimpleMenuProvider(
                    (id, inv, p) -> new BlockPouchMenu(id, inv, finalWand),
                    Component.translatable("magicconstruction.gui.block_pouch")
            ));
        });
        ctx.get().setPacketHandled(true);
    }
}
