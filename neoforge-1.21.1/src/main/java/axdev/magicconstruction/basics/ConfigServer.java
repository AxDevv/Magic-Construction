package axdev.magicconstruction.basics;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfigServer
{
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue MAX_RANGE;
    public static final ModConfigSpec.BooleanValue ENABLE_UNDO;
    public static final ModConfigSpec.IntValue UNDO_HISTORY_SIZE;
    public static final ModConfigSpec.IntValue UNDO_TIMEOUT;

    public static final ModConfigSpec.ConfigValue<List<?>> SIMILAR_BLOCKS;
    private static final String[] SIMILAR_BLOCKS_DEFAULT = {
            "minecraft:dirt;minecraft:grass_block;minecraft:coarse_dirt;minecraft:podzol;minecraft:mycelium;minecraft:farmland;minecraft:dirt_path;minecraft:rooted_dirt"
    };

    public static final ModConfigSpec.BooleanValue TE_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<?>> TE_LIST;
    private static final String[] TE_LIST_DEFAULT = {"chiselsandbits"};

    public static final ModConfigSpec.IntValue COBBLE_WAND_DURABILITY;
    public static final ModConfigSpec.IntValue COBBLE_WAND_MAX_BLOCKS;
    public static final ModConfigSpec.IntValue BUILDER_WAND_DURABILITY;
    public static final ModConfigSpec.IntValue BUILDER_WAND_MAX_BLOCKS;
    public static final ModConfigSpec.IntValue ENGINEER_WAND_DURABILITY;
    public static final ModConfigSpec.IntValue ENGINEER_WAND_MAX_BLOCKS;
    public static final ModConfigSpec.IntValue ARCHITECT_WAND_DURABILITY;
    public static final ModConfigSpec.IntValue ARCHITECT_WAND_MAX_BLOCKS;

    public static final ModConfigSpec.IntValue POUCH_CAPACITY_1;
    public static final ModConfigSpec.IntValue POUCH_CAPACITY_2;
    public static final ModConfigSpec.IntValue POUCH_CAPACITY_3;
    public static final ModConfigSpec.IntValue POUCH_CAPACITY_4;

    public static final ModConfigSpec.BooleanValue RUNES_ENABLED;
    public static final ModConfigSpec.BooleanValue RUNES_WAND_ONLY;
    public static final ModConfigSpec.DoubleValue RUNE_MINOR_REPAIR_PERCENT;
    public static final ModConfigSpec.DoubleValue RUNE_MAJOR_REPAIR_PERCENT;
    public static final ModConfigSpec.IntValue RUNE_MINOR_XP_COST;
    public static final ModConfigSpec.IntValue RUNE_MAJOR_XP_COST;

    public static final ModConfigSpec.BooleanValue DURABILITY_DAMAGE_ENABLED;
    public static final ModConfigSpec.DoubleValue DURABILITY_DAMAGE_MULTIPLIER;

    static {
        final var builder = new ModConfigSpec.Builder();

        builder.comment("Construction Wand Reforged - Server Configuration",
                "This file contains all server-side settings for the mod.",
                "Changes require a server restart to take effect.");

        builder.push("general");
        builder.comment("Maximum placement range (0: unlimited). Affects all wands. Used for lag prevention on servers.");
        MAX_RANGE = builder.defineInRange("MaxRange", 100, 0, Integer.MAX_VALUE);
        builder.comment("Blocks to treat equally when placing (for placing grass on dirt, etc). Enter block IDs separated by ;");
        SIMILAR_BLOCKS = builder.defineList("SimilarBlocks", Arrays.asList(SIMILAR_BLOCKS_DEFAULT), obj -> true);
        builder.pop();

        builder.push("undo");
        builder.comment("Enable or disable the undo feature entirely.");
        ENABLE_UNDO = builder.define("EnableUndo", true);
        builder.comment("Maximum number of undo operations stored per player.");
        UNDO_HISTORY_SIZE = builder.defineInRange("UndoHistorySize", 5, 1, 50);
        builder.comment("Time in milliseconds before undo confirmation expires.");
        UNDO_TIMEOUT = builder.defineInRange("UndoTimeout", 3000, 1000, 30000);
        builder.pop();

        builder.push("tileentity");
        builder.comment("White/Blacklist for Tile Entities. Allow/Prevent blocks with TEs from being placed by wand.",
                "You can either add block ids like minecraft:chest or mod ids like minecraft");
        TE_LIST = builder.defineList("TEList", Arrays.asList(TE_LIST_DEFAULT), obj -> true);
        builder.comment("If TRUE, treat TEList as a whitelist (only listed allowed). If FALSE, treat as blacklist (listed are blocked).");
        TE_WHITELIST = builder.define("TEWhitelist", false);
        builder.pop();

        builder.push("wands");
        builder.comment("=== Cobble Wand ===");
        COBBLE_WAND_DURABILITY = builder.defineInRange("CobbleWandDurability", 256, 1, 100000);
        COBBLE_WAND_MAX_BLOCKS = builder.defineInRange("CobbleWandMaxBlocks", 16, 1, 1024);
        builder.comment("=== Builder Wand ===");
        BUILDER_WAND_DURABILITY = builder.defineInRange("BuilderWandDurability", 512, 1, 100000);
        BUILDER_WAND_MAX_BLOCKS = builder.defineInRange("BuilderWandMaxBlocks", 32, 1, 1024);
        builder.comment("=== Engineer Wand ===");
        ENGINEER_WAND_DURABILITY = builder.defineInRange("EngineerWandDurability", 1024, 1, 100000);
        ENGINEER_WAND_MAX_BLOCKS = builder.defineInRange("EngineerWandMaxBlocks", 64, 1, 1024);
        builder.comment("=== Architect Wand ===");
        ARCHITECT_WAND_DURABILITY = builder.defineInRange("ArchitectWandDurability", 2048, 1, 100000);
        ARCHITECT_WAND_MAX_BLOCKS = builder.defineInRange("ArchitectWandMaxBlocks", 128, 1, 1024);
        builder.pop();

        builder.push("pouches");
        builder.comment("Capacity of each pouch tier in total items (not stacks).");
        POUCH_CAPACITY_1 = builder.defineInRange("PouchCapacity1_Basic", 64, 1, 100000);
        POUCH_CAPACITY_2 = builder.defineInRange("PouchCapacity2_Iron", 256, 1, 100000);
        POUCH_CAPACITY_3 = builder.defineInRange("PouchCapacity3_Gold", 512, 1, 100000);
        POUCH_CAPACITY_4 = builder.defineInRange("PouchCapacity4_Diamond", 1024, 1, 100000);
        builder.pop();

        builder.push("runes");
        builder.comment("Enable or disable repair runes entirely.");
        RUNES_ENABLED = builder.define("RunesEnabled", true);
        builder.comment("If TRUE, repair runes can only be used on Construction Wands. If FALSE, they work on all repairable items.");
        RUNES_WAND_ONLY = builder.define("RunesWandOnly", true);
        builder.comment("Percentage of durability restored by Minor Repair Rune (0.0 to 1.0).");
        RUNE_MINOR_REPAIR_PERCENT = builder.defineInRange("RuneMinorRepairPercent", 0.25, 0.01, 1.0);
        builder.comment("Percentage of durability restored by Major Repair Rune (0.0 to 1.0).");
        RUNE_MAJOR_REPAIR_PERCENT = builder.defineInRange("RuneMajorRepairPercent", 1.0, 0.01, 1.0);
        builder.comment("XP level cost for using Minor Repair Rune in anvil.");
        RUNE_MINOR_XP_COST = builder.defineInRange("RuneMinorXPCost", 3, 0, 100);
        builder.comment("XP level cost for using Major Repair Rune in anvil.");
        RUNE_MAJOR_XP_COST = builder.defineInRange("RuneMajorXPCost", 10, 0, 100);
        builder.pop();

        builder.push("durability");
        builder.comment("Enable durability damage when using wands.");
        DURABILITY_DAMAGE_ENABLED = builder.define("DurabilityDamageEnabled", true);
        builder.comment("Multiplier for durability damage (1.0 = 1 damage per block placed, 0.5 = half damage, 2.0 = double).");
        DURABILITY_DAMAGE_MULTIPLIER = builder.defineInRange("DurabilityDamageMultiplier", 1.0, 0.0, 10.0);
        builder.pop();

        SPEC = builder.build();
    }
}
