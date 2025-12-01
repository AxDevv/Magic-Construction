package axdev.magicconstruction.wand;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.wand.undo.ISnapshot;
import axdev.magicconstruction.wand.undo.PlaceSnapshot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PlaceAction {
    public static List<ISnapshot> getSnapshots(World world, PlayerEntity player, BlockHitResult rayTraceResult,
                                               ItemStack wand, BlockItem targetItem, int limit) {
        LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();
        LinkedList<BlockPos> candidates = new LinkedList<>();
        HashSet<BlockPos> allCandidates = new HashSet<>();

        Direction placeDirection = rayTraceResult.getSide();
        BlockState targetBlock = world.getBlockState(rayTraceResult.getBlockPos());
        BlockPos startingPoint = rayTraceResult.getBlockPos().offset(placeDirection);

        candidates.add(startingPoint);

        int availableItems = countAvailableItems(player, wand, targetItem);

        while (!candidates.isEmpty() && placeSnapshots.size() < limit && placeSnapshots.size() < availableItems) {
            BlockPos currentCandidate = candidates.removeFirst();
            try {
                BlockPos supportingPoint = currentCandidate.offset(placeDirection.getOpposite());
                BlockState candidateSupportingBlock = world.getBlockState(supportingPoint);

                if (targetBlock.getBlock() == candidateSupportingBlock.getBlock() && allCandidates.add(currentCandidate)) {
                    if (!world.getBlockState(currentCandidate).isAir()) continue;

                    BlockHitResult blockRayTraceResult = new BlockHitResult(
                            Vec3d.ofCenter(currentCandidate), placeDirection, currentCandidate, false);

                    BlockState placementState = targetItem.getBlock().getDefaultState();
                    if (placementState == null) continue;

                    placeSnapshots.add(new PlaceSnapshot(placementState, currentCandidate, new ItemStack(targetItem)));
                    addNeighbors(candidates, currentCandidate, placeDirection);
                }
            } catch (Exception e) {
            }
        }
        return placeSnapshots;
    }

    private static int countAvailableItems(PlayerEntity player, ItemStack wand, BlockItem targetItem) {
        if (player.isCreative()) return Integer.MAX_VALUE;

        int count = 0;

        if (ItemWand.hasPouch(wand)) {
            count += ItemWand.countInPouch(wand, targetItem);
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == targetItem) {
                count += stack.getCount();
            }
        }

        return count;
    }

    private static void addNeighbors(LinkedList<BlockPos> candidates, BlockPos current, Direction placeDirection) {
        switch (placeDirection) {
            case DOWN:
            case UP:
                candidates.add(current.north());
                candidates.add(current.south());
                candidates.add(current.east());
                candidates.add(current.west());
                break;
            case NORTH:
            case SOUTH:
                candidates.add(current.east());
                candidates.add(current.west());
                candidates.add(current.up());
                candidates.add(current.down());
                break;
            case EAST:
            case WEST:
                candidates.add(current.north());
                candidates.add(current.south());
                candidates.add(current.up());
                candidates.add(current.down());
                break;
        }
    }
}
