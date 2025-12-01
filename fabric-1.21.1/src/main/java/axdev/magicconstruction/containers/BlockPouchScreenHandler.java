package axdev.magicconstruction.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import axdev.magicconstruction.items.wand.ItemWand;

public class BlockPouchScreenHandler extends ScreenHandler {
    private final ItemStack wand;
    private final int pouchSize;
    private final SimpleInventory pouchContainer;
    private final PlayerEntity player;

    public BlockPouchScreenHandler(int syncId, PlayerInventory playerInv, BlockPouchData data) {
        this(syncId, playerInv, playerInv.player.getMainHandStack());
    }

    public BlockPouchScreenHandler(int syncId, PlayerInventory playerInv, ItemStack wand) {
        super(ModScreenHandlers.BLOCK_POUCH, syncId);
        this.wand = wand;
        this.player = playerInv.player;

        this.pouchSize = Math.min(36, ItemWand.getPouchCapacity(wand) / 64);

        this.pouchContainer = new SimpleInventory(pouchSize) {
            @Override
            public void markDirty() {
                super.markDirty();
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
        DefaultedList<ItemStack> contents = ItemWand.getPouchContents(wand, player.getWorld());
        for (int i = 0; i < contents.size() && i < pouchSize; i++) {
            pouchContainer.setStack(i, contents.get(i).copy());
        }
    }

    private void savePouchContents() {
        DefaultedList<ItemStack> contents = DefaultedList.of();
        for (int i = 0; i < pouchSize; i++) {
            ItemStack stack = pouchContainer.getStack(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }
        ItemWand.setPouchContents(wand, contents, player.getWorld());
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();

            if (slotStack == wand || ItemStack.areItemsAndComponentsEqual(slotStack, wand)) {
                return ItemStack.EMPTY;
            }

            itemstack = slotStack.copy();

            if (index < pouchSize) {
                if (!this.insertItem(slotStack, pouchSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(slotStack, 0, pouchSize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getMainHandStack() == wand || player.getOffHandStack() == wand;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        savePouchContents();
    }

    public int getPouchSize() {
        return pouchSize;
    }

    private static class PouchSlot extends Slot {
        private final ItemStack wand;

        public PouchSlot(SimpleInventory inventory, int index, int x, int y, ItemStack wand) {
            super(inventory, index, x, y);
            this.wand = wand;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack != wand && !ItemStack.areItemsAndComponentsEqual(stack, wand);
        }
    }
}
