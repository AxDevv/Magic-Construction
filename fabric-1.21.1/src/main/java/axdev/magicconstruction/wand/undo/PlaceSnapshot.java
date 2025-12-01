package axdev.magicconstruction.wand.undo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceSnapshot implements ISnapshot {
    private final BlockState blockState;
    private final BlockPos pos;
    private final ItemStack requiredItems;

    public PlaceSnapshot(BlockState blockState, BlockPos pos, ItemStack requiredItems) {
        this.blockState = blockState;
        this.pos = pos;
        this.requiredItems = requiredItems;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    public ItemStack getRequiredItems() {
        return requiredItems;
    }

    @Override
    public boolean execute(World world, PlayerEntity player, BlockHitResult rayTraceResult) {
        return world.setBlockState(pos, blockState, 3);
    }

    @Override
    public boolean canRestore(World world) {
        BlockState currentState = world.getBlockState(pos);
        return currentState.getBlock() == blockState.getBlock();
    }

    @Override
    public ItemStack restore(World world) {
        if (!canRestore(world)) return ItemStack.EMPTY;
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        return requiredItems.copy();
    }

    @Override
    public void forceRestore(World world) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
    }
}
