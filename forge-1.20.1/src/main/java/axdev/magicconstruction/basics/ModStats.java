package axdev.magicconstruction.basics;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import axdev.magicconstruction.MagicConstruction;

public class ModStats {
    public static ResourceLocation USE_WAND = new ResourceLocation(MagicConstruction.MODID, "use_wand");

    public static void init() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, USE_WAND, USE_WAND);
        Stats.CUSTOM.get(USE_WAND, StatFormatter.DEFAULT);
    }
}
