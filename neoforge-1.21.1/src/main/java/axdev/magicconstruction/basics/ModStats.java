package axdev.magicconstruction.basics;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import axdev.magicconstruction.MagicConstruction;

public class ModStats
{
    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS = DeferredRegister.create(Registries.CUSTOM_STAT, MagicConstruction.MODID);

    public static final ResourceLocation USE_WAND = MagicConstruction.loc("use_wand");

    static {
        CUSTOM_STATS.register("use_wand", () -> USE_WAND);
    }

    public static void register(IEventBus modEventBus) {
        CUSTOM_STATS.register(modEventBus);
    }

    public static void init() {
        Stats.CUSTOM.get(USE_WAND, StatFormatter.DEFAULT);
    }
}
