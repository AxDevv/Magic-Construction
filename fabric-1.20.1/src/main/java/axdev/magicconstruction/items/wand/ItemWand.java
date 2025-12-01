package axdev.magicconstruction.items.wand;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
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

    public static int getPouchTier(ItemStack wand) {
        NbtCompound tag = wand.getOrCreateNbt();
        return tag.getInt(TAG_POUCH_TIER);
    }

    public static void setPouchTier(ItemStack wand, int tier) {
        NbtCompound tag = wand.getOrCreateNbt();
        tag.putInt(TAG_POUCH_TIER, tier);
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
        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.translatable("magicconstruction.gui.block_pouch");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInv, PlayerEntity p) {
                return new BlockPouchScreenHandler(syncId, playerInv, wand);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
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
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("magicconstruction.tooltip.blocks", limit).formatted(Formatting.GRAY));

        if (hasPouch(stack)) {
            DefaultedList<ItemStack> contents = getPouchContents(stack);
            int total = countPouchItems(contents);
            int capacity = getPouchCapacity(stack);
            tooltip.add(Text.translatable("magicconstruction.tooltip.pouch", total, capacity).formatted(Formatting.AQUA));
        }
    }

    public static DefaultedList<ItemStack> getPouchContents(ItemStack wand) {
        DefaultedList<ItemStack> contents = DefaultedList.of();
        NbtCompound tag = wand.getOrCreateNbt();

        if (tag.contains(TAG_POUCH)) {
            NbtList list = tag.getList(TAG_POUCH, 10);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound itemTag = list.getCompound(i);
                ItemStack stack = ItemStack.fromNbt(itemTag);
                if (!stack.isEmpty()) {
                    contents.add(stack);
                }
            }
        }
        return contents;
    }

    public static void setPouchContents(ItemStack wand, DefaultedList<ItemStack> contents) {
        NbtCompound tag = wand.getOrCreateNbt();
        NbtList list = new NbtList();
        for (ItemStack stack : contents) {
            if (!stack.isEmpty()) {
                NbtCompound itemTag = new NbtCompound();
                stack.writeNbt(itemTag);
                list.add(itemTag);
            }
        }
        tag.put(TAG_POUCH, list);
    }

    public static boolean consumeFromPouch(ItemStack wand, Item item, int amount) {
        DefaultedList<ItemStack> contents = getPouchContents(wand);
        for (int i = 0; i < contents.size(); i++) {
            ItemStack stack = contents.get(i);
            if (stack.getItem() == item && stack.getCount() >= amount) {
                stack.decrement(amount);
                if (stack.isEmpty()) contents.remove(i);
                setPouchContents(wand, contents);
                return true;
            }
        }
        return false;
    }

    public static int countInPouch(ItemStack wand, Item item) {
        DefaultedList<ItemStack> contents = getPouchContents(wand);
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
