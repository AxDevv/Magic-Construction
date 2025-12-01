package axdev.magicconstruction.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import axdev.magicconstruction.basics.ReplacementRegistry;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.wand.undo.ISnapshot;
import axdev.magicconstruction.wand.undo.PlaceSnapshot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PlaceAction {
    public static List<ISnapshot> getSnapshots(Level world, Player player, BlockHitResult rayTraceResult,
                                               ItemStack wand, BlockItem targetItem, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();
        LinkedList<BlockPos> candidates = new LinkedList<>();
        HashSet<BlockPos> allCandidates = new HashSet<>();

        Direction placeDirection = rayTraceResult.getDirection();
        BlockState targetBlock = world.getBlockState(rayTraceResult.getBlockPos());
        BlockPos startingPoint = rayTraceResult.getBlockPos().offset(placeDirection.getNormal());

        candidates.add(startingPoint);

        int availableItems = countAvailableItems(player, wand, targetItem);

        while (!candidates.isEmpty() && placeSnapshots.size() < limit && placeSnapshots.size() < availableItems) {
            BlockPos currentCandidate = candidates.removeFirst();
            try {
                BlockPos supportingPoint = currentCandidate.offset(placeDirection.getOpposite().getNormal());
                BlockState candidateSupportingBlock = world.getBlockState(supportingPoint);

                if (matchBlocks(targetBlock.getBlock(), candidateSupportingBlock.getBlock()) &&
                        allCandidates.add(currentCandidate)) {

                    if (!WandUtil.isPositionPlaceable(world, player, currentCandidate, true)) continue;

                    PlaceSnapshot snapshot = createSnapshot(world, player, rayTraceResult, currentCandidate, targetItem, candidateSupportingBlock);
                    if (snapshot == null) continue;
                    placeSnapshots.add(snapshot);

                    addNeighbors(candidates, currentCandidate, placeDirection);
                }
            } catch (Exception e) {
            }
        }
        return placeSnapshots;
    }

    private static int countAvailableItems(Player player, ItemStack wand, BlockItem targetItem) {
        if (player.isCreative()) return Integer.MAX_VALUE;

        int count = 0;

        if (ItemWand.hasPouch(wand)) {
            count += ItemWand.countInPouch(wand, targetItem);
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == targetItem) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static boolean matchBlocks(net.minecraft.world.level.block.Block b1, net.minecraft.world.level.block.Block b2) {
        return b1 == b2 || ReplacementRegistry.matchBlocks(b1, b2);
    }

    private static PlaceSnapshot createSnapshot(Level world, Player player, BlockHitResult rayTraceResult,
                                                BlockPos pos, BlockItem item, BlockState supportingBlock) {
        BlockHitResult blockRayTraceResult = new BlockHitResult(
                Vec3.atCenterOf(pos), rayTraceResult.getDirection(), pos, false);

        UseOnContext useContext = new UseOnContext(player, player.getUsedItemHand(), blockRayTraceResult);
        BlockPlaceContext context = new BlockPlaceContext(useContext);

        BlockState placementState = item.getBlock().getStateForPlacement(context);
        if (placementState == null) return null;

        if (!WandUtil.isTEAllowed(placementState)) return null;

        return new PlaceSnapshot(placementState, pos, new ItemStack(item));
    }

    private static void addNeighbors(LinkedList<BlockPos> candidates, BlockPos current, Direction placeDirection) {
        switch (placeDirection) {
            case DOWN:
            case UP:
                candidates.add(current.offset(Direction.NORTH.getNormal()));
                candidates.add(current.offset(Direction.SOUTH.getNormal()));
                candidates.add(current.offset(Direction.EAST.getNormal()));
                candidates.add(current.offset(Direction.WEST.getNormal()));
                break;
            case NORTH:
            case SOUTH:
                candidates.add(current.offset(Direction.EAST.getNormal()));
                candidates.add(current.offset(Direction.WEST.getNormal()));
                candidates.add(current.offset(Direction.UP.getNormal()));
                candidates.add(current.offset(Direction.DOWN.getNormal()));
                break;
            case EAST:
            case WEST:
                candidates.add(current.offset(Direction.NORTH.getNormal()));
                candidates.add(current.offset(Direction.SOUTH.getNormal()));
                candidates.add(current.offset(Direction.UP.getNormal()));
                candidates.add(current.offset(Direction.DOWN.getNormal()));
                break;
        }
    }
}
