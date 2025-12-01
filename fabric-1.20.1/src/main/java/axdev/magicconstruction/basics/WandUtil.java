package axdev.magicconstruction.basics;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.items.wand.ItemWand;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WandUtil {
    public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
        return ItemStack.areItemsEqual(stackA, stackB) && ItemStack.canCombine(stackA, stackB);
    }

    public static boolean stackEquals(ItemStack stackA, Item item) {
        ItemStack stackB = new ItemStack(item);
        return stackEquals(stackA, stackB);
    }

    public static ItemStack holdingWand(PlayerEntity player) {
        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty() && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ItemWand) {
            return player.getStackInHand(Hand.MAIN_HAND);
        } else if (!player.getStackInHand(Hand.OFF_HAND).isEmpty() && player.getStackInHand(Hand.OFF_HAND).getItem() instanceof ItemWand) {
            return player.getStackInHand(Hand.OFF_HAND);
        }
        return null;
    }

    public static BlockPos posFromVec(Vec3d vec) {
        return new BlockPos(
                (int) Math.round(vec.x), (int) Math.round(vec.y), (int) Math.round(vec.z));
    }

    public static Vec3d entityPositionVec(Entity entity) {
        return new Vec3d(entity.getX(), entity.getY() + entity.getHeight() / 2, entity.getZ());
    }

    public static Vec3d blockPosVec(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<ItemStack> getHotbar(PlayerEntity player) {
        return player.getInventory().main.subList(0, 9);
    }

    public static List<ItemStack> getHotbarWithOffhand(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.getInventory().main.subList(0, 9));
        inventory.addAll(player.getInventory().offHand);
        return inventory;
    }

    public static List<ItemStack> getMainInv(PlayerEntity player) {
        return player.getInventory().main.subList(9, player.getInventory().main.size());
    }

    public static List<ItemStack> getFullInv(PlayerEntity player) {
        ArrayList<ItemStack> inventory = new ArrayList<>(player.getInventory().offHand);
        inventory.addAll(player.getInventory().main);
        return inventory;
    }

    public static int blockDistance(BlockPos p1, BlockPos p2) {
        return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getZ() - p2.getZ()));
    }

    public static boolean isTEAllowed(BlockState state) {
        if (!state.hasBlockEntity()) return true;

        Identifier name = Registries.BLOCK.getId(state.getBlock());
        if (name == null) return false;

        String fullId = name.toString();
        String modId = name.getNamespace();

        boolean inList = ConfigServer.getTeList().contains(fullId) || ConfigServer.getTeList().contains(modId);
        boolean isWhitelist = ConfigServer.isTeWhitelist();

        return isWhitelist == inList;
    }

    public static boolean placeBlock(World world, PlayerEntity player, BlockState block, BlockPos pos, @Nullable BlockItem item) {
        if (!world.setBlockState(pos, block)) {
            MagicConstruction.LOGGER.info("Block could not be placed");
            return false;
        }

        ItemStack stack;
        if (item == null) stack = new ItemStack(block.getBlock().asItem());
        else {
            stack = new ItemStack(item);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
        }

        block.getBlock().onPlaced(world, pos, block, player, stack);

        return true;
    }

    public static boolean removeBlock(World world, PlayerEntity player, @Nullable BlockState block, BlockPos pos) {
        BlockState currentBlock = world.getBlockState(pos);

        if (!world.canPlayerModifyAt(player, pos)) return false;

        if (!player.isCreative()) {
            if (currentBlock.getHardness(world, pos) <= -1 || world.getBlockEntity(pos) != null) return false;

            if (block != null)
                if (!ReplacementRegistry.matchBlocks(currentBlock.getBlock(), block.getBlock())) return false;
        }

        world.removeBlock(pos, false);
        return true;
    }

    public static int countItem(PlayerEntity player, Item item) {
        if (player.getInventory().main == null) return 0;
        if (player.isCreative()) return Integer.MAX_VALUE;

        int total = 0;
        List<ItemStack> inventory = WandUtil.getFullInv(player);

        for (ItemStack stack : inventory) {
            if (stack == null || stack.isEmpty()) continue;

            if (WandUtil.stackEquals(stack, item)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static boolean isPositionModifiable(World world, PlayerEntity player, BlockPos pos) {
        if (!world.isInBuildLimit(pos)) return false;

        if (!world.canPlayerModifyAt(player, pos)) return false;

        if (ConfigServer.getMaxRange() > 0 &&
                WandUtil.blockDistance(player.getBlockPos(), pos) > ConfigServer.getMaxRange()) return false;

        return true;
    }

    public static boolean isPositionPlaceable(World world, PlayerEntity player, BlockPos pos, boolean replace) {
        if (!isPositionModifiable(world, player, pos)) return false;

        if (world.isAir(pos)) return true;

        return replace && world.getBlockState(pos).canReplace(
                new net.minecraft.item.ItemPlacementContext(player, Hand.MAIN_HAND, ItemStack.EMPTY,
                        new net.minecraft.util.hit.BlockHitResult(new Vec3d(0, 0, 0), Direction.DOWN, pos, false)));
    }

    public static boolean isBlockRemovable(World world, PlayerEntity player, BlockPos pos) {
        if (!isPositionModifiable(world, player, pos)) return false;

        if (!player.isCreative()) {
            return !(world.getBlockState(pos).getHardness(world, pos) <= -1) && world.getBlockEntity(pos) == null;
        }
        return true;
    }

    public static boolean isBlockPermeable(World world, BlockPos pos) {
        return world.isAir(pos) || world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
    }

    public static boolean entitiesCollidingWithBlock(World world, BlockState blockState, BlockPos pos) {
        VoxelShape shape = blockState.getCollisionShape(world, pos);
        if (!shape.isEmpty()) {
            Box blockBB = shape.getBoundingBox().offset(pos);
            return !world.getEntitiesByClass(LivingEntity.class, blockBB, Predicate.not(Entity::isSpectator)).isEmpty();
        }
        return false;
    }

    public static Direction fromVector(Vec3d vector) {
        return Direction.getFacing(vector.x, vector.y, vector.z);
    }
}
