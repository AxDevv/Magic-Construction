package axdev.magicconstruction.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemRepairRune extends Item {
    private final float repairPercent;

    public ItemRepairRune(Properties properties, float repairPercent) {
        super(properties);
        this.repairPercent = repairPercent;
    }

    public float getRepairPercent() {
        return repairPercent;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int percent = (int)(repairPercent * 100);
        tooltip.add(Component.translatable("magicconstruction.tooltip.repair_rune", percent));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
