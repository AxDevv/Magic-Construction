package axdev.magicconstruction.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class LedgeDetector {
    private static final double MAX_DISTANCE = 6.0;
    private static final double STEP = 0.25;
    private static final double LEDGE_SEARCH_RADIUS = 1.5;

    @Nullable
    public static BlockHitResult detectLedge(Player player, Level world) {
        Vec3 eyePos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getLookAngle();

        for(double d = 1.0; d <= MAX_DISTANCE; d += STEP) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            BlockPos blockPos = BlockPos.containing(checkPos);

            if(!world.isEmptyBlock(blockPos)) {
                return null;
            }

            BlockHitResult ledge = findAdjacentLedge(world, checkPos, blockPos, lookVec);
            if(ledge != null) {
                return ledge;
            }
        }

        return null;
    }

    @Nullable
    private static BlockHitResult findAdjacentLedge(Level world, Vec3 checkPos, BlockPos airPos, Vec3 lookVec) {
        Direction primaryDir = getPrimaryLookDirection(lookVec);
        Direction[] searchOrder = getSearchOrder(primaryDir, lookVec);

        for(Direction dir : searchOrder) {
            BlockPos adjacentPos = airPos.relative(dir);

            if(!world.isEmptyBlock(adjacentPos)) {
                BlockPos placePos = airPos;
                Direction hitFace = dir.getOpposite();

                if(isValidLedgePlacement(world, placePos, adjacentPos)) {
                    Vec3 hitLocation = Vec3.atCenterOf(adjacentPos).add(
                        hitFace.getStepX() * 0.5,
                        hitFace.getStepY() * 0.5,
                        hitFace.getStepZ() * 0.5
                    );
                    return new BlockHitResult(hitLocation, hitFace, adjacentPos, false);
                }
            }
        }

        return null;
    }

    private static Direction getPrimaryLookDirection(Vec3 lookVec) {
        double absX = Math.abs(lookVec.x);
        double absY = Math.abs(lookVec.y);
        double absZ = Math.abs(lookVec.z);

        if(absY >= absX && absY >= absZ) {
            return lookVec.y > 0 ? Direction.UP : Direction.DOWN;
        } else if(absX >= absZ) {
            return lookVec.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return lookVec.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private static Direction[] getSearchOrder(Direction primary, Vec3 lookVec) {
        Direction[] all = Direction.values();
        Direction[] result = new Direction[6];
        int idx = 0;

        for(Direction dir : all) {
            if(dir != primary && dir != primary.getOpposite()) {
                result[idx++] = dir;
            }
        }
        result[idx++] = primary;
        result[idx] = primary.getOpposite();

        return result;
    }

    private static boolean isValidLedgePlacement(Level world, BlockPos placePos, BlockPos supportPos) {
        if(!world.isEmptyBlock(placePos)) return false;
        return !world.isEmptyBlock(supportPos);
    }
}
