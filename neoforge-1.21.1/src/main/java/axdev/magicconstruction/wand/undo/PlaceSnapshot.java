package axdev.magicconstruction.wand.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.wand.WandItemUseContext;

import javax.annotation.Nullable;

public class PlaceSnapshot implements ISnapshot
{
    private BlockState block;
    private final BlockPos pos;
    private final ItemStack requiredItem;

    public PlaceSnapshot(BlockState block, BlockPos pos, ItemStack requiredItem) {
        this.block = block;
        this.pos = pos;
        this.requiredItem = requiredItem;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public BlockState getBlockState() {
        return block;
    }

    @Override
    public ItemStack getRequiredItems() {
        return requiredItem;
    }

    @Override
    public boolean execute(Level world, Player player, BlockHitResult rayTraceResult) {
        BlockItem item = (BlockItem) requiredItem.getItem();
        BlockPlaceContext ctx = new WandItemUseContext(world, player, rayTraceResult, pos, item);

        BlockState newState = item.getBlock().getStateForPlacement(ctx);
        if(newState == null || !newState.canSurvive(world, pos)) {
            newState = block;
        }

        if(!WandUtil.isTEAllowed(newState)) return false;
        if(WandUtil.entitiesCollidingWithBlock(world, newState, pos)) return false;

        block = newState;
        return WandUtil.placeBlock(world, player, block, pos, item);
    }

    @Override
    public boolean canRestore(Level world, Player player) {
        BlockState currentState = world.getBlockState(pos);
        if(currentState.isAir()) return false;
        if(currentState.getBlock() != block.getBlock()) return false;
        return true;
    }

    @Override
    public boolean restore(Level world, Player player) {
        BlockState currentState = world.getBlockState(pos);
        if(currentState.isAir()) return false;
        if(currentState.getBlock() != block.getBlock()) return false;
        return WandUtil.removeBlock(world, player, block, pos);
    }

    @Override
    public void forceRestore(Level world) {
        world.removeBlock(pos, false);
    }
}
