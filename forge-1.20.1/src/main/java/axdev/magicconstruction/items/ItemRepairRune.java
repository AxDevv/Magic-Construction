package axdev.magicconstruction.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
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
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        int percent = (int)(repairPercent * 100);
        tooltip.add(Component.translatable("magicconstruction.tooltip.repair_rune", percent).withStyle(ChatFormatting.GREEN));
    }
}
