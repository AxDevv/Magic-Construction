package axdev.magicconstruction.items.wand;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import axdev.magicconstruction.containers.BlockPouchScreenHandler;
import axdev.magicconstruction.wand.WandJob;

import java.util.List;

public class ItemWand extends Item {
    private static final String TAG_POUCH = "block_pouch";
    private static final String TAG_POUCH_TIER = "pouch_tier";
    private static final int[] POUCH_CAPACITIES = {0, 128, 256, 512, 2048};

    private final int limit;
    private final Item repairItem;

    public ItemWand(Settings settings, int limit, Item repairItem) {
        super(settings);
        this.limit = limit;
        this.repairItem = repairItem;
    }

    public int getLimit() {
        return limit;
    }

    private static NbtCompound getCustomData(ItemStack wand) {
        NbtComponent component = wand.get(DataComponentTypes.CUSTOM_DATA);
        if (component != null) {
            return component.copyNbt();
        }
        return new NbtCompound();
    }

    private static void setCustomData(ItemStack wand, NbtCompound tag) {
        wand.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
    }

    public static int getPouchTier(ItemStack wand) {
        NbtCompound tag = getCustomData(wand);
        return tag.getInt(TAG_POUCH_TIER);
    }

    public static void setPouchTier(ItemStack wand, int tier) {
        NbtCompound tag = getCustomData(wand);
        tag.putInt(TAG_POUCH_TIER, tier);
        setCustomData(wand, tag);
    }

    public static int getPouchCapacity(ItemStack wand) {
        int tier = getPouchTier(wand);
        if (tier < 0 || tier >= POUCH_CAPACITIES.length) return 0;
        return POUCH_CAPACITIES[tier];
    }

    public static boolean hasPouch(ItemStack wand) {
        return getPouchTier(wand) > 0;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();

        if (world.isClient || player == null) return ActionResult.FAIL;

        ItemStack stack = player.getStackInHand(hand);

        if (player.isSneaking()) {
            if (hasPouch(stack)) {
                openPouchGui(player, stack);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        WandJob job = getWandJob(player, world, new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), false), stack);
        return job.doIt() ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (player.isSneaking()) {
            if (hasPouch(stack)) {
                if (!world.isClient) {
                    openPouchGui(player, stack);
                }
                return TypedActionResult.success(stack);
            }
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            BlockHitResult ledgeHit = axdev.magicconstruction.wand.LedgeDetector.detectLedge(player, world);
            if (ledgeHit != null) {
                WandJob job = getWandJob(player, world, ledgeHit, stack);
                if (job.doIt()) {
                    return TypedActionResult.success(stack);
                }
            }
        }

        return TypedActionResult.pass(stack);
    }

    private void openPouchGui(PlayerEntity player, ItemStack wand) {
        player.openHandledScreen(new ExtendedScreenHandlerFactory<axdev.magicconstruction.containers.BlockPouchData>() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("magicconstruction.gui.block_pouch");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity p) {
                return new BlockPouchScreenHandler(syncId, playerInv, wand);
            }

            @Override
            public axdev.magicconstruction.containers.BlockPouchData getScreenOpeningData(ServerPlayerEntity player) {
                return new axdev.magicconstruction.containers.BlockPouchData(0);
            }
        });
    }

    public static WandJob getWandJob(PlayerEntity player, World world, BlockHitResult rayTraceResult, ItemStack wand) {
        WandJob wandJob = new WandJob(player, world, rayTraceResult, wand);
        wandJob.getSnapshots();
        return wandJob;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.getItem() == repairItem;
    }

    public int remainingDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamage();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("magicconstruction.tooltip.blocks", limit).formatted(Formatting.GRAY));

        if (hasPouch(stack)) {
            DefaultedList<ItemStack> contents = getPouchContents(stack, context);
            int total = countPouchItems(contents);
            int capacity = getPouchCapacity(stack);
            tooltip.add(Text.translatable("magicconstruction.tooltip.pouch", total, capacity).formatted(Formatting.AQUA));
        }
    }

    public static DefaultedList<ItemStack> getPouchContents(ItemStack wand, TooltipContext context) {
        DefaultedList<ItemStack> contents = DefaultedList.of();
        NbtCompound tag = getCustomData(wand);

        if (tag.contains(TAG_POUCH)) {
            NbtList list = tag.getList(TAG_POUCH, 10);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound itemTag = list.getCompound(i);
                if (context != null && context.getRegistryLookup() != null) {
                    RegistryOps<net.minecraft.nbt.NbtElement> ops = context.getRegistryLookup().getOps(NbtOps.INSTANCE);
                    ItemStack.CODEC.parse(ops, itemTag).result().ifPresent(stack -> {
                        if (!stack.isEmpty()) {
                            contents.add(stack);
                        }
                    });
                }
            }
        }
        return contents;
    }

    public static DefaultedList<ItemStack> getPouchContents(ItemStack wand, World world) {
        DefaultedList<ItemStack> contents = DefaultedList.of();
        NbtCompound tag = getCustomData(wand);

        if (tag.contains(TAG_POUCH)) {
            NbtList list = tag.getList(TAG_POUCH, 10);
            RegistryOps<net.minecraft.nbt.NbtElement> ops = world.getRegistryManager().getOps(NbtOps.INSTANCE);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound itemTag = list.getCompound(i);
                ItemStack.CODEC.parse(ops, itemTag).result().ifPresent(stack -> {
                    if (!stack.isEmpty()) {
                        contents.add(stack);
                    }
                });
            }
        }
        return contents;
    }

    public static void setPouchContents(ItemStack wand, DefaultedList<ItemStack> contents, World world) {
        NbtCompound tag = getCustomData(wand);
        NbtList list = new NbtList();
        RegistryOps<net.minecraft.nbt.NbtElement> ops = world.getRegistryManager().getOps(NbtOps.INSTANCE);
        for (ItemStack stack : contents) {
            if (!stack.isEmpty()) {
                ItemStack.CODEC.encodeStart(ops, stack).result().ifPresent(nbt -> {
                    if (nbt instanceof NbtCompound compound) {
                        list.add(compound);
                    }
                });
            }
        }
        tag.put(TAG_POUCH, list);
        setCustomData(wand, tag);
    }

    public static boolean consumeFromPouch(ItemStack wand, Item item, int amount, World world) {
        DefaultedList<ItemStack> contents = getPouchContents(wand, world);
        for (int i = 0; i < contents.size(); i++) {
            ItemStack stack = contents.get(i);
            if (stack.getItem() == item && stack.getCount() >= amount) {
                stack.decrement(amount);
                if (stack.isEmpty()) contents.remove(i);
                setPouchContents(wand, contents, world);
                return true;
            }
        }
        return false;
    }

    public static int countInPouch(ItemStack wand, Item item, World world) {
        DefaultedList<ItemStack> contents = getPouchContents(wand, world);
        for (ItemStack stack : contents) {
            if (stack.getItem() == item) return stack.getCount();
        }
        return 0;
    }

    private static int countPouchItems(DefaultedList<ItemStack> contents) {
        int total = 0;
        for (ItemStack stack : contents) total += stack.getCount();
        return total;
    }
}
