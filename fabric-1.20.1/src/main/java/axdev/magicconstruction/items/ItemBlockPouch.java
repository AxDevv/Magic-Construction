package axdev.magicconstruction.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockPouch extends Item {
    private final int capacity;
    private final int tier;

    public ItemBlockPouch(Settings settings, int capacity, int tier) {
        super(settings);
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
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("magicconstruction.tooltip.pouch_capacity", capacity).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("magicconstruction.tooltip.pouch_combine").formatted(Formatting.DARK_GRAY));
    }
}
