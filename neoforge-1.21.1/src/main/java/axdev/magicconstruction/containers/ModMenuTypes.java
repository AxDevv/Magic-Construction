package axdev.magicconstruction.containers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import axdev.magicconstruction.MagicConstruction;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MagicConstruction.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<BlockPouchMenu>> BLOCK_POUCH_MENU =
            MENUS.register("block_pouch", () -> IMenuTypeExtension.create(BlockPouchMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
