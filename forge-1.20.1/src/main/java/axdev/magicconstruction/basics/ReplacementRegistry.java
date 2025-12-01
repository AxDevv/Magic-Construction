package axdev.magicconstruction.basics;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReplacementRegistry {
    private static final List<Set<Block>> similarBlockGroups = new ArrayList<>();

    public static void init() {
        similarBlockGroups.clear();

        List<? extends String> configList = ConfigServer.SIMILAR_BLOCKS.get();
        for (String entry : configList) {
            String[] blockIds = entry.split(";");
            Set<Block> group = new HashSet<>();

            for (String blockId : blockIds) {
                Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId.trim()));
                if (block != null) {
                    group.add(block);
                }
            }

            if (group.size() > 1) {
                similarBlockGroups.add(group);
            }
        }
    }

    public static boolean matchBlocks(Block b1, Block b2) {
        if (b1 == b2) return true;

        for (Set<Block> group : similarBlockGroups) {
            if (group.contains(b1) && group.contains(b2)) {
                return true;
            }
        }

        return false;
    }
}
