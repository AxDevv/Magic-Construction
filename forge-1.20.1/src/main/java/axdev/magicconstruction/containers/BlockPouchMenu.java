package axdev.magicconstruction.containers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import axdev.magicconstruction.items.wand.ItemWand;

public class BlockPouchMenu extends AbstractContainerMenu {
    private final ItemStack wand;
    private final int pouchSize;
    private final SimpleContainer pouchContainer;

    public BlockPouchMenu(int containerId, Inventory playerInv, ItemStack wand) {
        super(ModMenuTypes.BLOCK_POUCH_MENU.get(), containerId);
        this.wand = wand;

        this.pouchSize = Math.min(36, ItemWand.getPouchCapacity(wand) / 64);

        this.pouchContainer = new SimpleContainer(pouchSize) {
            @Override
            public void setChanged() {
                super.setChanged();
                savePouchContents();
            }
        };

        loadPouchContents();

        int rows = (int) Math.ceil(pouchSize / 9.0);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                if (index < pouchSize) {
                    this.addSlot(new PouchSlot(pouchContainer, index, 8 + col * 18, 18 + row * 18, wand));
                }
            }
        }

        int invY = 18 + rows * 18 + 14 + 14 + 4;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, invY + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, invY + 58));
        }
    }

    private void loadPouchContents() {
        NonNullList<ItemStack> contents = ItemWand.getPouchContents(wand);
        for (int i = 0; i < contents.size() && i < pouchSize; i++) {
            pouchContainer.setItem(i, contents.get(i).copy());
        }
    }

    private void savePouchContents() {
        NonNullList<ItemStack> contents = NonNullList.create();
        for (int i = 0; i < pouchSize; i++) {
            ItemStack stack = pouchContainer.getItem(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }
        ItemWand.setPouchContents(wand, contents);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();

            if (slotStack == wand || ItemStack.isSameItem(slotStack, wand)) {
                return ItemStack.EMPTY;
            }

            itemstack = slotStack.copy();

            if (index < pouchSize) {
                if (!this.moveItemStackTo(slotStack, pouchSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, pouchSize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getMainHandItem() == wand || player.getOffhandItem() == wand;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        savePouchContents();
    }

    public int getPouchSize() {
        return pouchSize;
    }

    private static class PouchSlot extends Slot {
        private final ItemStack wand;

        public PouchSlot(Container container, int slot, int x, int y, ItemStack wand) {
            super(container, slot, x, y);
            this.wand = wand;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack != wand && !ItemStack.isSameItem(stack, wand);
        }
    }
}
