package axdev.magicconstruction.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemRepairRune extends Item {
    private final float repairPercent;

    public ItemRepairRune(Settings settings, float repairPercent) {
        super(settings);
        this.repairPercent = repairPercent;
    }

    public float getRepairPercent() {
        return repairPercent;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        int percent = (int)(repairPercent * 100);
        tooltip.add(Text.translatable("magicconstruction.tooltip.repair_rune", percent).formatted(Formatting.GREEN));
    }
}
