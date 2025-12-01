package axdev.magicconstruction.containers.handlers;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import axdev.magicconstruction.api.IContainerHandler;

public class HandlerShulkerbox implements IContainerHandler {
    @Override
    public int countItems(ItemStack container, ItemStack item) {
        if (!(container.getItem() instanceof BlockItem blockItem)) return -1;
        if (!(blockItem.getBlock() instanceof ShulkerBoxBlock)) return -1;

        NonNullList<ItemStack> contents = getContents(container);
        int count = 0;
        for (ItemStack stack : contents) {
            if (ItemStack.isSameItemSameTags(stack, item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public int consumeItems(ItemStack container, ItemStack item, int count) {
        if (!(container.getItem() instanceof BlockItem blockItem)) return -1;
        if (!(blockItem.getBlock() instanceof ShulkerBoxBlock)) return -1;

        NonNullList<ItemStack> contents = getContents(container);
        int remaining = count;

        for (ItemStack stack : contents) {
            if (remaining <= 0) break;
            if (ItemStack.isSameItemSameTags(stack, item)) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }

        setContents(container, contents);
        return count - remaining;
    }

    private NonNullList<ItemStack> getContents(ItemStack shulker) {
        NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);
        CompoundTag tag = shulker.getTagElement("BlockEntityTag");
        if (tag != null) {
            ContainerHelper.loadAllItems(tag, contents);
        }
        return contents;
    }

    private void setContents(ItemStack shulker, NonNullList<ItemStack> contents) {
        CompoundTag tag = shulker.getOrCreateTagElement("BlockEntityTag");
        ContainerHelper.saveAllItems(tag, contents);
    }
}
