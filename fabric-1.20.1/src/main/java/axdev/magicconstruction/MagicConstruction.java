package axdev.magicconstruction;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import axdev.magicconstruction.basics.ReplacementRegistry;
import axdev.magicconstruction.containers.ModScreenHandlers;
import axdev.magicconstruction.crafting.ModRecipes;
import axdev.magicconstruction.items.ModItems;
import axdev.magicconstruction.network.ModMessages;
import axdev.magicconstruction.wand.undo.UndoHistory;

public class MagicConstruction implements ModInitializer {
    public static final String MODID = "magicconstruction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static MagicConstruction instance;
    public static UndoHistory undoHistory;

    @Override
    public void onInitialize() {
        instance = this;
        undoHistory = new UndoHistory();

        ModItems.register();
        ModRecipes.register();
        ModScreenHandlers.register();
        ModMessages.registerServerReceivers();
        ReplacementRegistry.init();

        LOGGER.info("Magic Construction loaded!");
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}
