package axdev.magicconstruction.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.items.wand.ItemWand;

public class ModItems {
    public static Item COBBLE_WAND;
    public static Item BUILDER_WAND;
    public static Item ENGINEER_WAND;
    public static Item ARCHITECT_WAND;
    public static Item BLOCK_POUCH_1;
    public static Item BLOCK_POUCH_2;
    public static Item BLOCK_POUCH_3;
    public static Item BLOCK_POUCH_4;
    public static Item REPAIR_RUNE_MINOR;
    public static Item REPAIR_RUNE_MAJOR;

    public static final RegistryKey<ItemGroup> MAGIC_CONSTRUCTION_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, MagicConstruction.id("magic_construction"));

    public static void register() {
        COBBLE_WAND = Registry.register(Registries.ITEM, MagicConstruction.id("cobble_wand"), new ItemWand(new Item.Settings().maxDamage(256), 16, Items.COBBLESTONE));
        BUILDER_WAND = Registry.register(Registries.ITEM, MagicConstruction.id("builder_wand"), new ItemWand(new Item.Settings().maxDamage(512), 32, Items.IRON_INGOT));
        ENGINEER_WAND = Registry.register(Registries.ITEM, MagicConstruction.id("engineer_wand"), new ItemWand(new Item.Settings().maxDamage(1024), 64, Items.GOLD_INGOT));
        ARCHITECT_WAND = Registry.register(Registries.ITEM, MagicConstruction.id("architect_wand"), new ItemWand(new Item.Settings().maxDamage(2048), 128, Items.DIAMOND));
        BLOCK_POUCH_1 = Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_1"), new ItemBlockPouch(new Item.Settings().maxCount(1), 128, 1));
        BLOCK_POUCH_2 = Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_2"), new ItemBlockPouch(new Item.Settings().maxCount(1), 256, 2));
        BLOCK_POUCH_3 = Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_3"), new ItemBlockPouch(new Item.Settings().maxCount(1), 512, 3));
        BLOCK_POUCH_4 = Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_4"), new ItemBlockPouch(new Item.Settings().maxCount(1), 2048, 4));
        REPAIR_RUNE_MINOR = Registry.register(Registries.ITEM, MagicConstruction.id("repair_rune_minor"), new ItemRepairRune(new Item.Settings().maxCount(16), 0.25f));
        REPAIR_RUNE_MAJOR = Registry.register(Registries.ITEM, MagicConstruction.id("repair_rune_major"), new ItemRepairRune(new Item.Settings().maxCount(16), 1.0f));

        Registry.register(Registries.ITEM_GROUP, MAGIC_CONSTRUCTION_GROUP,
            FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.magicconstruction"))
                .icon(() -> new ItemStack(ARCHITECT_WAND))
                .entries((context, entries) -> {
                    entries.add(COBBLE_WAND);
                    entries.add(BUILDER_WAND);
                    entries.add(ENGINEER_WAND);
                    entries.add(ARCHITECT_WAND);
                    entries.add(BLOCK_POUCH_1);
                    entries.add(BLOCK_POUCH_2);
                    entries.add(BLOCK_POUCH_3);
                    entries.add(BLOCK_POUCH_4);
                    entries.add(REPAIR_RUNE_MINOR);
                    entries.add(REPAIR_RUNE_MAJOR);
                })
                .build()
        );
    }
}
