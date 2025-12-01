package axdev.magicconstruction;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import axdev.magicconstruction.client.BlockPouchScreen;
import axdev.magicconstruction.client.ClientEvents;
import axdev.magicconstruction.client.ModKeyBindings;
import axdev.magicconstruction.client.RenderBlockPreview;
import axdev.magicconstruction.containers.ModScreenHandlers;
import axdev.magicconstruction.network.ModMessages;

public class MagicConstructionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.register();
        ClientEvents.register();
        RenderBlockPreview.register();
        ModMessages.registerClientReceivers();
        HandledScreens.register(ModScreenHandlers.BLOCK_POUCH, BlockPouchScreen::new);
    }
}
