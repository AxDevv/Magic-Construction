package axdev.magicconstruction.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
    public static final Item COBBLE_WAND = new ItemWand(new FabricItemSettings().maxDamage(256), 16, Items.COBBLESTONE);
    public static final Item BUILDER_WAND = new ItemWand(new FabricItemSettings().maxDamage(512), 32, Items.IRON_INGOT);
    public static final Item ENGINEER_WAND = new ItemWand(new FabricItemSettings().maxDamage(1024), 64, Items.GOLD_INGOT);
    public static final Item ARCHITECT_WAND = new ItemWand(new FabricItemSettings().maxDamage(2048), 128, Items.DIAMOND);

    public static final Item BLOCK_POUCH_1 = new ItemBlockPouch(new FabricItemSettings().maxCount(1), 128, 1);
    public static final Item BLOCK_POUCH_2 = new ItemBlockPouch(new FabricItemSettings().maxCount(1), 256, 2);
    public static final Item BLOCK_POUCH_3 = new ItemBlockPouch(new FabricItemSettings().maxCount(1), 512, 3);
    public static final Item BLOCK_POUCH_4 = new ItemBlockPouch(new FabricItemSettings().maxCount(1), 2048, 4);

    public static final Item REPAIR_RUNE_MINOR = new ItemRepairRune(new FabricItemSettings().maxCount(16), 0.25f);
    public static final Item REPAIR_RUNE_MAJOR = new ItemRepairRune(new FabricItemSettings().maxCount(16), 1.0f);

    public static final RegistryKey<ItemGroup> MAGIC_CONSTRUCTION_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, MagicConstruction.id("magic_construction"));

    public static void register() {
        Registry.register(Registries.ITEM, MagicConstruction.id("cobble_wand"), COBBLE_WAND);
        Registry.register(Registries.ITEM, MagicConstruction.id("builder_wand"), BUILDER_WAND);
        Registry.register(Registries.ITEM, MagicConstruction.id("engineer_wand"), ENGINEER_WAND);
        Registry.register(Registries.ITEM, MagicConstruction.id("architect_wand"), ARCHITECT_WAND);
        Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_1"), BLOCK_POUCH_1);
        Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_2"), BLOCK_POUCH_2);
        Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_3"), BLOCK_POUCH_3);
        Registry.register(Registries.ITEM, MagicConstruction.id("block_pouch_4"), BLOCK_POUCH_4);
        Registry.register(Registries.ITEM, MagicConstruction.id("repair_rune_minor"), REPAIR_RUNE_MINOR);
        Registry.register(Registries.ITEM, MagicConstruction.id("repair_rune_major"), REPAIR_RUNE_MAJOR);

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
