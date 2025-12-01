package axdev.magicconstruction.basics;

import java.util.Arrays;
import java.util.List;

public class ConfigServer {
    public static int MAX_RANGE = 100;
    public static boolean ENABLE_UNDO = true;
    public static int UNDO_HISTORY_SIZE = 5;
    public static int UNDO_TIMEOUT = 3000;

    public static List<String> SIMILAR_BLOCKS = Arrays.asList(
            "minecraft:dirt;minecraft:grass_block;minecraft:coarse_dirt;minecraft:podzol;minecraft:mycelium;minecraft:farmland;minecraft:dirt_path;minecraft:rooted_dirt"
    );

    public static boolean TE_WHITELIST = false;
    public static List<String> TE_LIST = Arrays.asList("chiselsandbits");

    public static int COBBLE_WAND_DURABILITY = 256;
    public static int COBBLE_WAND_MAX_BLOCKS = 16;
    public static int BUILDER_WAND_DURABILITY = 512;
    public static int BUILDER_WAND_MAX_BLOCKS = 32;
    public static int ENGINEER_WAND_DURABILITY = 1024;
    public static int ENGINEER_WAND_MAX_BLOCKS = 64;
    public static int ARCHITECT_WAND_DURABILITY = 2048;
    public static int ARCHITECT_WAND_MAX_BLOCKS = 128;

    public static int POUCH_CAPACITY_1 = 64;
    public static int POUCH_CAPACITY_2 = 256;
    public static int POUCH_CAPACITY_3 = 512;
    public static int POUCH_CAPACITY_4 = 1024;

    public static boolean RUNES_ENABLED = true;
    public static boolean RUNES_WAND_ONLY = true;
    public static double RUNE_MINOR_REPAIR_PERCENT = 0.25;
    public static double RUNE_MAJOR_REPAIR_PERCENT = 1.0;
    public static int RUNE_MINOR_XP_COST = 3;
    public static int RUNE_MAJOR_XP_COST = 10;

    public static boolean DURABILITY_DAMAGE_ENABLED = true;
    public static double DURABILITY_DAMAGE_MULTIPLIER = 1.0;

    public static int getMaxRange() {
        return MAX_RANGE;
    }

    public static List<String> getSimilarBlocks() {
        return SIMILAR_BLOCKS;
    }

    public static List<String> getTeList() {
        return TE_LIST;
    }

    public static boolean isTeWhitelist() {
        return TE_WHITELIST;
    }
}
