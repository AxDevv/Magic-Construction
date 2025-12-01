package axdev.magicconstruction.containers;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import axdev.magicconstruction.MagicConstruction;

public class ModScreenHandlers {
    public static final ScreenHandlerType<BlockPouchScreenHandler> BLOCK_POUCH =
            new ExtendedScreenHandlerType<>(BlockPouchScreenHandler::new, BlockPouchData.PACKET_CODEC);

    public static void register() {
        Registry.register(Registries.SCREEN_HANDLER, MagicConstruction.id("block_pouch"), BLOCK_POUCH);
    }
}
