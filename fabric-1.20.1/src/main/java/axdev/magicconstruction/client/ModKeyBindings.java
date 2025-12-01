package axdev.magicconstruction.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final String CATEGORY = "key.magicconstruction.category";

    public static KeyBinding UNDO;
    public static KeyBinding OPEN_POUCH;
    public static void register() {
        UNDO = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.magicconstruction.undo",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_LEFT,
                CATEGORY
        ));

        OPEN_POUCH = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.magicconstruction.open_pouch",
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_RIGHT,
                CATEGORY
        ));
    }

    public static boolean isModifierDown() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        return mc.player != null && mc.player.isSneaking();
    }
}
