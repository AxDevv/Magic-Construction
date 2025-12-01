package axdev.magicconstruction.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.items.ItemBlockPouch;

import java.util.function.Supplier;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MagicConstruction.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicConstruction.MODID);

    public static final DeferredHolder<Item, Item> COBBLE_WAND = ITEMS.register("cobble_wand",
            () -> new ItemWand(propWand(256), 16, Items.COBBLESTONE));

    public static final DeferredHolder<Item, Item> BUILDER_WAND = ITEMS.register("builder_wand",
            () -> new ItemWand(propWand(512), 32, Items.IRON_INGOT));

    public static final DeferredHolder<Item, Item> ENGINEER_WAND = ITEMS.register("engineer_wand",
            () -> new ItemWand(propWand(1024), 64, Items.DIAMOND));

    public static final DeferredHolder<Item, Item> ARCHITECT_WAND = ITEMS.register("architect_wand",
            () -> new ItemWand(propWand(2048), 128, Items.NETHERITE_INGOT));

    public static final DeferredHolder<Item, Item> BLOCK_POUCH_1 = ITEMS.register("block_pouch_1",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 1));

    public static final DeferredHolder<Item, Item> BLOCK_POUCH_2 = ITEMS.register("block_pouch_2",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 2));

    public static final DeferredHolder<Item, Item> BLOCK_POUCH_3 = ITEMS.register("block_pouch_3",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 3));

    public static final DeferredHolder<Item, Item> BLOCK_POUCH_4 = ITEMS.register("block_pouch_4",
            () -> new ItemBlockPouch(new Item.Properties().stacksTo(1), 4));

    @SuppressWarnings("unchecked")
    public static final Supplier<Item>[] WANDS = new Supplier[] {COBBLE_WAND, BUILDER_WAND, ENGINEER_WAND, ARCHITECT_WAND};

    @SuppressWarnings("unchecked")
    public static final Supplier<Item>[] POUCHES = new Supplier[] {BLOCK_POUCH_1, BLOCK_POUCH_2, BLOCK_POUCH_3, BLOCK_POUCH_4};

    public static final DeferredHolder<Item, Item> REPAIR_RUNE_MINOR = ITEMS.register("repair_rune_minor",
            () -> new ItemRepairRune(new Item.Properties().stacksTo(16), 0.25f));

    public static final DeferredHolder<Item, Item> REPAIR_RUNE_MAJOR = ITEMS.register("repair_rune_major",
            () -> new ItemRepairRune(new Item.Properties().stacksTo(16), 1.0f));

    @SuppressWarnings("unchecked")
    public static final Supplier<Item>[] RUNES = new Supplier[] {REPAIR_RUNE_MINOR, REPAIR_RUNE_MAJOR};

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONSTRUCTION_WAND_TAB = CREATIVE_TABS.register("construction_wand_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.magicconstruction"))
                    .icon(() -> new ItemStack(ARCHITECT_WAND.get()))
                    .displayItems((params, output) -> {
                        for(Supplier<Item> itemSupplier : WANDS) {
                            output.accept(itemSupplier.get());
                        }
                        for(Supplier<Item> itemSupplier : POUCHES) {
                            output.accept(itemSupplier.get());
                        }
                        for(Supplier<Item> itemSupplier : RUNES) {
                            output.accept(itemSupplier.get());
                        }
                    })
                    .build());

    public static Item.Properties propWand(int durability) {
        return new Item.Properties().durability(durability).stacksTo(1);
    }
}
