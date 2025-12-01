package axdev.magicconstruction.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.network.ModMessages;
import axdev.magicconstruction.network.PacketUndo;

public class ClientEvents {
    private boolean lastModifierState = false;

    public ClientEvents() {
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;
        if(mc.screen != null) return;
        if(WandUtil.holdingWand(player) == null) return;

        if(!isModifierDown()) return;

        if(event.getAction() == 1) {
            if(ModKeyBindings.UNDO.matchesMouse(event.getButton())) {
                ModMessages.sendToServer(new PacketUndo());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player == null) return;
        if(WandUtil.holdingWand(player) == null) return;

        boolean modifierDown = isModifierDown();
        if(lastModifierState != modifierDown) {
            lastModifierState = modifierDown;
        }
    }

    public static boolean isModifierDown() {
        return ModKeyBindings.MODIFIER.isDown();
    }
}
