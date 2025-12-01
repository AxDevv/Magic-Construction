package axdev.magicconstruction.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.network.PacketUndo;

public class ClientEvents {
    private static boolean lastModifierState = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (client.currentScreen != null) return;
            if (WandUtil.holdingWand(client.player) == null) return;

            boolean modifierDown = ModKeyBindings.isModifierDown();
            if (lastModifierState != modifierDown) {
                lastModifierState = modifierDown;
            }

            if (modifierDown && ModKeyBindings.UNDO.wasPressed()) {
                PacketUndo.send();
            }
        });
    }

    public static boolean isModifierDown() {
        return ModKeyBindings.isModifierDown();
    }
}
