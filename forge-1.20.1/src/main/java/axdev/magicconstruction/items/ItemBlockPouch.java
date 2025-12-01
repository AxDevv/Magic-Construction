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

public class ItemBlockPouch extends Item {
    private final int capacity;
    private final int tier;

    public ItemBlockPouch(Properties properties, int capacity, int tier) {
        super(properties);
        this.capacity = capacity;
        this.tier = tier;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTier() {
        return tier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("magicconstruction.tooltip.pouch_capacity", capacity).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("magicconstruction.tooltip.pouch_combine").withStyle(ChatFormatting.DARK_GRAY));
    }
}
