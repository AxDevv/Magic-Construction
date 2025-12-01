package axdev.magicconstruction.items.wand;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.containers.BlockPouchMenu;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.wand.WandJob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemWand extends Item
{
    private static final String TAG_POUCH = "block_pouch";
    private static final String TAG_POUCH_TIER = "pouch_tier";

    private static final int[] POUCH_CAPACITIES = {0, 128, 256, 512, 2048};

    private final int limit;
    private final Item repairItem;

    public ItemWand(Properties properties, int limit, Item repairItem) {
        super(properties);
        this.limit = limit;
        this.repairItem = repairItem;
    }

    public int getLimit() {
        return limit;
    }

    public static int getPouchTier(ItemStack wand) {
        CustomData customData = wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        return tag.getInt(TAG_POUCH_TIER);
    }

    public static void setPouchTier(ItemStack wand, int tier) {
        CustomData customData = wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putInt(TAG_POUCH_TIER, tier);
        wand.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getPouchCapacity(ItemStack wand) {
        int tier = getPouchTier(wand);
        if(tier < 0 || tier >= POUCH_CAPACITIES.length) return 0;
        return POUCH_CAPACITIES[tier];
    }

    public static boolean hasPouch(ItemStack wand) {
        return getPouchTier(wand) > 0;
    }

    @Nonnull
    @Override
    public InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();
        Level world = context.getLevel();

        if(world.isClientSide || player == null) return InteractionResult.FAIL;

        ItemStack stack = player.getItemInHand(hand);

        if(player.isCrouching()) {
            if(hasPouch(stack)) {
                openPouchGui(player, stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        WandJob job = getWandJob(player, world, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false), stack);
        return job.doIt() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(player.isCrouching()) {
            if(hasPouch(stack)) {
                if(!world.isClientSide) {
                    openPouchGui(player, stack);
                }
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.pass(stack);
        }

        if(!world.isClientSide) {
            BlockHitResult ledgeResult = axdev.magicconstruction.wand.LedgeDetector.detectLedge(player, world);
            if(ledgeResult != null) {
                WandJob job = getWandJob(player, world, ledgeResult, stack);
                if(job.doIt()) {
                    return InteractionResultHolder.success(stack);
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    private void openPouchGui(Player player, ItemStack wand) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("magicconstruction.gui.block_pouch");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory playerInv, Player player) {
                return new BlockPouchMenu(containerId, playerInv, wand);
            }
        });
    }

    public static WandJob getWandJob(Player player, Level world, @Nullable BlockHitResult rayTraceResult, ItemStack wand) {
        WandJob wandJob = new WandJob(player, world, rayTraceResult, wand);
        wandJob.getSnapshots();
        return wandJob;
    }

    @Override
    public boolean isCorrectToolForDrops(@Nonnull ItemStack stack, @Nonnull BlockState blockIn) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
        return repair.getItem() == repairItem;
    }

    public int remainingDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack itemstack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> lines, @Nonnull TooltipFlag extraInfo) {
        lines.add(Component.translatable("magicconstruction.tooltip.blocks", limit).withStyle(ChatFormatting.GRAY));

        if(hasPouch(itemstack)) {
            NonNullList<ItemStack> contents = getPouchContents(itemstack);
            int total = countPouchItems(contents);
            int capacity = getPouchCapacity(itemstack);
            lines.add(Component.translatable("magicconstruction.tooltip.pouch", total, capacity).withStyle(ChatFormatting.AQUA));

            if(!contents.isEmpty()) {
                int shown = 0;
                for(ItemStack stack : contents) {
                    if(shown >= 5) {
                        lines.add(Component.translatable("magicconstruction.tooltip.and_more", contents.size() - 5).withStyle(ChatFormatting.DARK_GRAY));
                        break;
                    }
                    lines.add(Component.literal("  " + stack.getCount() + "x ").withStyle(ChatFormatting.DARK_GRAY)
                            .append(stack.getHoverName().copy().withStyle(ChatFormatting.GRAY)));
                    shown++;
                }
            }
            lines.add(Component.translatable("magicconstruction.tooltip.shift_pouch").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static NonNullList<ItemStack> getPouchContents(ItemStack wand) {
        NonNullList<ItemStack> contents = NonNullList.create();
        CustomData customData = wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        if(tag.contains(TAG_POUCH)) {
            ListTag list = tag.getList(TAG_POUCH, 10);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag itemTag = list.getCompound(i);
                ItemStack stack = ItemStack.parseOptional(net.minecraft.core.HolderLookup.Provider.create(java.util.stream.Stream.empty()), itemTag);
                if(!stack.isEmpty()) {
                    contents.add(stack);
                }
            }
        }
        return contents;
    }

    public static void setPouchContents(ItemStack wand, NonNullList<ItemStack> contents) {
        CustomData customData = wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        ListTag list = new ListTag();
        for(ItemStack stack : contents) {
            if(!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putString("id", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                itemTag.putInt("count", stack.getCount());
                list.add(itemTag);
            }
        }
        tag.put(TAG_POUCH, list);
        wand.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static ItemStack addToPouchContents(ItemStack wand, ItemStack toAdd) {
        NonNullList<ItemStack> contents = getPouchContents(wand);
        int remaining = toAdd.getCount();
        int maxStack = toAdd.getMaxStackSize();
        int capacity = getPouchCapacity(wand);
        int currentTotal = countPouchItems(contents);
        int availableSpace = capacity - currentTotal;

        if(availableSpace <= 0) {
            return toAdd.copy();
        }

        int canAddTotal = Math.min(remaining, availableSpace);
        int toAddNow = canAddTotal;

        for(ItemStack stack : contents) {
            if(toAddNow <= 0) break;
            if(ItemStack.isSameItemSameComponents(stack, toAdd)) {
                int canAdd = maxStack - stack.getCount();
                if(canAdd > 0) {
                    int toTransfer = Math.min(canAdd, toAddNow);
                    stack.grow(toTransfer);
                    toAddNow -= toTransfer;
                }
            }
        }

        while(toAddNow > 0) {
            ItemStack newStack = toAdd.copy();
            int stackSize = Math.min(toAddNow, maxStack);
            newStack.setCount(stackSize);
            contents.add(newStack);
            toAddNow -= stackSize;
        }

        setPouchContents(wand, contents);

        int leftover = remaining - canAddTotal;
        if(leftover > 0) {
            ItemStack result = toAdd.copy();
            result.setCount(leftover);
            return result;
        }
        return ItemStack.EMPTY;
    }

    public static boolean consumeFromPouch(ItemStack wand, Item item, int amount) {
        NonNullList<ItemStack> contents = getPouchContents(wand);

        for(int i = 0; i < contents.size(); i++) {
            ItemStack stack = contents.get(i);
            if(stack.getItem() == item) {
                if(stack.getCount() >= amount) {
                    stack.shrink(amount);
                    if(stack.isEmpty()) {
                        contents.remove(i);
                    }
                    setPouchContents(wand, contents);
                    return true;
                }
            }
        }
        return false;
    }

    public static int countInPouch(ItemStack wand, Item item) {
        NonNullList<ItemStack> contents = getPouchContents(wand);
        for(ItemStack stack : contents) {
            if(stack.getItem() == item) {
                return stack.getCount();
            }
        }
        return 0;
    }

    private static int countPouchItems(NonNullList<ItemStack> contents) {
        int total = 0;
        for(ItemStack stack : contents) {
            total += stack.getCount();
        }
        return total;
    }
}
