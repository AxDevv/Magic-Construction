package axdev.magicconstruction.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemBlockPouch extends Item
{
    private static final int[] CAPACITIES = {0, 128, 256, 512, 2048};

    private final int tier;

    public ItemBlockPouch(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    public int getCapacity() {
        if(tier < 0 || tier >= CAPACITIES.length) return 0;
        return CAPACITIES[tier];
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack itemstack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag extraInfo) {
        lines.add(Component.translatable("magicconstruction.tooltip.pouch_capacity", getCapacity()).withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable("magicconstruction.tooltip.pouch_combine").withStyle(ChatFormatting.DARK_GRAY));
    }
}
