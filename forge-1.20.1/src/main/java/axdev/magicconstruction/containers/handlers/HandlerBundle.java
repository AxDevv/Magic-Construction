package axdev.magicconstruction.containers.handlers;

import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import axdev.magicconstruction.api.IContainerHandler;

public class HandlerBundle implements IContainerHandler {
    @Override
    public int countItems(ItemStack container, ItemStack item) {
        if (!(container.getItem() instanceof BundleItem)) return -1;
        return 0;
    }

    @Override
    public int consumeItems(ItemStack container, ItemStack item, int count) {
        if (!(container.getItem() instanceof BundleItem)) return -1;
        return 0;
    }
}
