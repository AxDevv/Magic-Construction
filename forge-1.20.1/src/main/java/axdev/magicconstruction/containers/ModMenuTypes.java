package axdev.magicconstruction.containers;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import axdev.magicconstruction.MagicConstruction;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MagicConstruction.MODID);

    public static final RegistryObject<MenuType<BlockPouchMenu>> BLOCK_POUCH_MENU = MENU_TYPES.register("block_pouch_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> new BlockPouchMenu(windowId, inv, inv.player.getMainHandItem())));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
