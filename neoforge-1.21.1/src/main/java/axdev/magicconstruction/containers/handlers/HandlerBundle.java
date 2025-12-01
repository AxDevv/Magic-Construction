package axdev.magicconstruction.containers.handlers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import axdev.magicconstruction.api.IContainerHandler;
import axdev.magicconstruction.basics.WandUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerBundle implements IContainerHandler
{
    @Override
    public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        return inventoryStack != null && inventoryStack.getCount() == 1 && inventoryStack.getItem() == Items.BUNDLE;
    }

    @Override
    public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
        BundleContents contents = inventoryStack.get(DataComponents.BUNDLE_CONTENTS);
        if(contents == null) return 0;

        int count = 0;
        for(ItemStack stack : contents.items()) {
            if(WandUtil.stackEquals(stack, itemStack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
        BundleContents contents = inventoryStack.get(DataComponents.BUNDLE_CONTENTS);
        if(contents == null) return count;

        AtomicInteger remaining = new AtomicInteger(count);
        List<ItemStack> newItems = new ArrayList<>();

        for(ItemStack stack : contents.items()) {
            if(WandUtil.stackEquals(stack, itemStack) && remaining.get() > 0) {
                int toTake = Math.min(remaining.get(), stack.getCount());
                remaining.addAndGet(-toTake);
                if(stack.getCount() > toTake) {
                    ItemStack remaining_stack = stack.copy();
                    remaining_stack.setCount(stack.getCount() - toTake);
                    newItems.add(remaining_stack);
                }
            } else {
                newItems.add(stack.copy());
            }
        }

        BundleContents.Mutable mutable = new BundleContents.Mutable(BundleContents.EMPTY);
        for(ItemStack stack : newItems) {
            mutable.tryInsert(stack);
        }
        inventoryStack.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());

        return remaining.get();
    }
}
