package axdev.magicconstruction;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import axdev.magicconstruction.basics.ConfigServer;
import axdev.magicconstruction.basics.ConfigClient;
import axdev.magicconstruction.basics.ModStats;
import axdev.magicconstruction.client.BlockPouchScreen;
import axdev.magicconstruction.client.ClientEvents;
import axdev.magicconstruction.client.ModKeyBindings;
import axdev.magicconstruction.client.RenderBlockPreview;
import net.minecraft.client.gui.screens.MenuScreens;
import axdev.magicconstruction.containers.ContainerManager;
import axdev.magicconstruction.containers.ContainerRegistrar;
import axdev.magicconstruction.containers.ModMenuTypes;
import axdev.magicconstruction.crafting.ModRecipes;
import axdev.magicconstruction.items.ModItems;
import axdev.magicconstruction.network.ModMessages;
import axdev.magicconstruction.wand.undo.UndoHistory;

@Mod(MagicConstruction.MODID)
public class MagicConstruction {
    public static final String MODID = "magicconstruction";
    public static final String MODNAME = "Magic Construction";

    public static MagicConstruction instance;
    public static final Logger LOGGER = LogManager.getLogger();

    public ContainerManager containerManager;
    public UndoHistory undoHistory;
    public RenderBlockPreview renderBlockPreview;

    public MagicConstruction() {
        instance = this;

        containerManager = new ContainerManager();
        undoHistory = new UndoHistory();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerKeyBindings);

        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_TABS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigServer.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigClient.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Magic Construction loaded!");
        ContainerRegistrar.register();
        ModMessages.register();
        ModStats.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        renderBlockPreview = new RenderBlockPreview();
        MinecraftForge.EVENT_BUS.register(renderBlockPreview);
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.BLOCK_POUCH_MENU.get(), BlockPouchScreen::new);
        });
    }

    private void registerKeyBindings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.UNDO);
        event.register(ModKeyBindings.OPEN_POUCH);
        event.register(ModKeyBindings.MODIFIER);
    }

    public static ResourceLocation loc(String name) {
        return new ResourceLocation(MODID, name);
    }
}
