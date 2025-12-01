package axdev.magicconstruction.api;

import net.minecraft.world.item.ItemStack;

public interface IContainerHandler {
    int countItems(ItemStack container, ItemStack item);
    int consumeItems(ItemStack container, ItemStack item, int count);
}
