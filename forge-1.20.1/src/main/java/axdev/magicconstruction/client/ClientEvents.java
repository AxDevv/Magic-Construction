package axdev.magicconstruction.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.network.ModMessages;
import axdev.magicconstruction.network.PacketOpenPouch;
import axdev.magicconstruction.network.PacketUndo;
import axdev.magicconstruction.items.wand.ItemWand;

@OnlyIn(Dist.CLIENT)
public class ClientEvents {
    private boolean lastModifierState = false;

    public ClientEvents() {
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;
        if(mc.screen != null) return;
        if(WandUtil.holdingWand(player) == null) return;

        if(!isModifierDown()) return;

        if(event.getAction() == 1) {
            if(ModKeyBindings.UNDO.matches(event.getKey(), event.getScanCode())) {
                ModMessages.sendToServer(new PacketUndo());
                return;
            }
            else if(ModKeyBindings.OPEN_POUCH.matches(event.getKey(), event.getScanCode())) {
                var wand = WandUtil.holdingWand(player);
                if(wand != null && ItemWand.hasPouch(wand)) {
                    ModMessages.sendToServer(new PacketOpenPouch());
                }
            }
        }

        boolean modifierDown = isModifierDown();
        if(lastModifierState != modifierDown) {
            lastModifierState = modifierDown;
        }
    }

    public static boolean isModifierDown() {
        return ModKeyBindings.MODIFIER.isDown();
    }
}
