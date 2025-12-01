package axdev.magicconstruction.basics;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfigServer {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue MAX_RANGE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_UNDO;
    public static final ForgeConfigSpec.IntValue UNDO_HISTORY_SIZE;
    public static final ForgeConfigSpec.IntValue UNDO_TIMEOUT;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SIMILAR_BLOCKS;
    private static final String[] SIMILAR_BLOCKS_DEFAULT = {
            "minecraft:dirt;minecraft:grass_block;minecraft:coarse_dirt;minecraft:podzol;minecraft:mycelium;minecraft:farmland;minecraft:dirt_path;minecraft:rooted_dirt"
    };

    public static final ForgeConfigSpec.BooleanValue TE_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TE_LIST;
    private static final String[] TE_LIST_DEFAULT = {"chiselsandbits"};

    public static final ForgeConfigSpec.BooleanValue RUNES_ENABLED;
    public static final ForgeConfigSpec.BooleanValue RUNES_WAND_ONLY;
    public static final ForgeConfigSpec.DoubleValue RUNE_MINOR_REPAIR_PERCENT;
    public static final ForgeConfigSpec.DoubleValue RUNE_MAJOR_REPAIR_PERCENT;
    public static final ForgeConfigSpec.IntValue RUNE_MINOR_XP_COST;
    public static final ForgeConfigSpec.IntValue RUNE_MAJOR_XP_COST;

    static {
        final var builder = new ForgeConfigSpec.Builder();

        builder.comment("Magic Construction - Server Configuration");

        builder.push("general");
        MAX_RANGE = builder.defineInRange("MaxRange", 100, 0, Integer.MAX_VALUE);
        SIMILAR_BLOCKS = builder.defineListAllowEmpty(Arrays.asList("SimilarBlocks"), () -> Arrays.asList(SIMILAR_BLOCKS_DEFAULT), obj -> true);
        builder.pop();

        builder.push("undo");
        ENABLE_UNDO = builder.define("EnableUndo", true);
        UNDO_HISTORY_SIZE = builder.defineInRange("UndoHistorySize", 5, 1, 50);
        UNDO_TIMEOUT = builder.defineInRange("UndoTimeout", 3000, 1000, 30000);
        builder.pop();

        builder.push("tileentity");
        TE_LIST = builder.defineListAllowEmpty(Arrays.asList("TEList"), () -> Arrays.asList(TE_LIST_DEFAULT), obj -> true);
        TE_WHITELIST = builder.define("TEWhitelist", false);
        builder.pop();

        builder.push("runes");
        RUNES_ENABLED = builder.define("RunesEnabled", true);
        RUNES_WAND_ONLY = builder.define("RunesWandOnly", true);
        RUNE_MINOR_REPAIR_PERCENT = builder.defineInRange("RuneMinorRepairPercent", 0.25, 0.01, 1.0);
        RUNE_MAJOR_REPAIR_PERCENT = builder.defineInRange("RuneMajorRepairPercent", 1.0, 0.01, 1.0);
        RUNE_MINOR_XP_COST = builder.defineInRange("RuneMinorXPCost", 3, 0, 100);
        RUNE_MAJOR_XP_COST = builder.defineInRange("RuneMajorXPCost", 10, 0, 100);
        builder.pop();

        SPEC = builder.build();
    }
}
