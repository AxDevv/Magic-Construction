package axdev.magicconstruction;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import axdev.magicconstruction.basics.ConfigClient;
import axdev.magicconstruction.basics.ConfigServer;
import axdev.magicconstruction.basics.ModStats;
import axdev.magicconstruction.client.BlockPouchScreen;
import axdev.magicconstruction.client.ClientEvents;
import axdev.magicconstruction.client.ModKeyBindings;
import axdev.magicconstruction.client.RenderBlockPreview;
import axdev.magicconstruction.containers.ContainerManager;
import axdev.magicconstruction.containers.ContainerRegistrar;
import axdev.magicconstruction.containers.ModMenuTypes;
import axdev.magicconstruction.items.ModItems;
import axdev.magicconstruction.network.ModMessages;
import axdev.magicconstruction.wand.undo.UndoHistory;
import axdev.magicconstruction.crafting.ModRecipes;


@Mod(MagicConstruction.MODID)
public class MagicConstruction {
    public static final String MODID = "magicconstruction";
    public static final String MODNAME = "Magic Construction";

    public static MagicConstruction instance;
    public static final Logger LOGGER = LogManager.getLogger();

    public ContainerManager containerManager;
    public UndoHistory undoHistory;
    public RenderBlockPreview renderBlockPreview;

    public MagicConstruction(IEventBus modEventBus, ModContainer modContainer) {
        instance = this;

        containerManager = new ContainerManager();
        undoHistory = new UndoHistory();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerKeyBindings);

        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_TABS.register(modEventBus);
        ModStats.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigServer.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigClient.SPEC);

        ModMessages.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Magic Construction loaded!");
        ContainerRegistrar.register();
        ModStats.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        renderBlockPreview = new RenderBlockPreview();
        NeoForge.EVENT_BUS.register(renderBlockPreview);
        NeoForge.EVENT_BUS.register(new ClientEvents());
    }

    private void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.BLOCK_POUCH_MENU.get(), BlockPouchScreen::new);
    }

    private void registerKeyBindings(final RegisterKeyMappingsEvent event) {
        event.register(ModKeyBindings.UNDO);
        event.register(ModKeyBindings.OPEN_POUCH);
        event.register(ModKeyBindings.MODIFIER);
    }

    public static ResourceLocation loc(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}
