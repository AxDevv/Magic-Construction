package axdev.magicconstruction.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.items.wand.ItemWand;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MagicConstruction.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicConstruction.MODID);

    public static final RegistryObject<Item> COBBLE_WAND = ITEMS.register("cobble_wand",
            () -> new ItemWand(new Item.Properties().durability(256), 16, Items.COBBLESTONE));

    public static final RegistryObject<Item> BUILDER_WAND = ITEMS.register("builder_wand",
            () -> new ItemWand(new Item.Properties().durability(512), 32, Items.IRON_INGOT));

    public static final RegistryObject<Item> ENGINEER_WAND = ITEMS.register("engineer_wand",
            () -> new ItemWand(new Item.Properties().durability(1024), 64, Items.GOLD_INGOT));

    public static final RegistryObject<Item> ARCHITECT_WAND = ITEMS.register("architect_wand",
            () -> new ItemWand(new Item.Properties().durability(2048), 128, Items.DIAMOND));

    public static final RegistryObject<Item> BLOCK_POUCH_1 = ITEMS.register("block_pouch_1",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 64, 1));

    public static final RegistryObject<Item> BLOCK_POUCH_2 = ITEMS.register("block_pouch_2",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 256, 2));

    public static final RegistryObject<Item> BLOCK_POUCH_3 = ITEMS.register("block_pouch_3",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 512, 3));

    public static final RegistryObject<Item> BLOCK_POUCH_4 = ITEMS.register("block_pouch_4",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 1024, 4));

    public static final RegistryObject<Item> REPAIR_RUNE_MINOR = ITEMS.register("repair_rune_minor",
            () -> new ItemRepairRune(new Item.Properties().stacksTo(16), 0.25f));

    public static final RegistryObject<Item> REPAIR_RUNE_MAJOR = ITEMS.register("repair_rune_major",
            () -> new ItemRepairRune(new Item.Properties().stacksTo(16), 1.0f));

    public static final RegistryObject<CreativeModeTab> MAGIC_CONSTRUCTION_TAB = CREATIVE_TABS.register("magic_construction_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.magicconstruction"))
                    .icon(() -> ARCHITECT_WAND.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(COBBLE_WAND.get());
                        output.accept(BUILDER_WAND.get());
                        output.accept(ENGINEER_WAND.get());
                        output.accept(ARCHITECT_WAND.get());
                        output.accept(BLOCK_POUCH_1.get());
                        output.accept(BLOCK_POUCH_2.get());
                        output.accept(BLOCK_POUCH_3.get());
                        output.accept(BLOCK_POUCH_4.get());
                        output.accept(REPAIR_RUNE_MINOR.get());
                        output.accept(REPAIR_RUNE_MAJOR.get());
                    })
                    .build());
}
