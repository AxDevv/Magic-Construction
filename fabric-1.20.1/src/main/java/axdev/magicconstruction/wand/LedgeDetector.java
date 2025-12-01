package axdev.magicconstruction.wand;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class LedgeDetector {
    private static final double MAX_DISTANCE = 6.0;
    private static final double STEP = 0.25;
    private static final double LEDGE_SEARCH_RADIUS = 1.5;

    @Nullable
    public static BlockHitResult detectLedge(PlayerEntity player, World world) {
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F);

        for (double d = 1.0; d <= MAX_DISTANCE; d += STEP) {
            Vec3d checkPos = eyePos.add(lookVec.multiply(d));
            BlockPos blockPos = new BlockPos((int) Math.floor(checkPos.x), (int) Math.floor(checkPos.y), (int) Math.floor(checkPos.z));

            if (!world.isAir(blockPos)) {
                return null;
            }

            BlockHitResult ledge = findAdjacentLedge(world, checkPos, blockPos, lookVec);
            if (ledge != null) {
                return ledge;
            }
        }

        return null;
    }

    @Nullable
    private static BlockHitResult findAdjacentLedge(World world, Vec3d checkPos, BlockPos airPos, Vec3d lookVec) {
        Direction primaryDir = getPrimaryLookDirection(lookVec);
        Direction[] searchOrder = getSearchOrder(primaryDir, lookVec);

        for (Direction dir : searchOrder) {
            BlockPos adjacentPos = airPos.offset(dir);

            if (!world.isAir(adjacentPos)) {
                BlockPos placePos = airPos;
                Direction hitFace = dir.getOpposite();

                if (isValidLedgePlacement(world, placePos, adjacentPos)) {
                    Vec3d hitLocation = Vec3d.ofCenter(adjacentPos).add(
                            hitFace.getOffsetX() * 0.5,
                            hitFace.getOffsetY() * 0.5,
                            hitFace.getOffsetZ() * 0.5
                    );
                    return new BlockHitResult(hitLocation, hitFace, adjacentPos, false);
                }
            }
        }

        return null;
    }

    private static Direction getPrimaryLookDirection(Vec3d lookVec) {
        double absX = Math.abs(lookVec.x);
        double absY = Math.abs(lookVec.y);
        double absZ = Math.abs(lookVec.z);

        if (absY >= absX && absY >= absZ) {
            return lookVec.y > 0 ? Direction.UP : Direction.DOWN;
        } else if (absX >= absZ) {
            return lookVec.x > 0 ? Direction.EAST : Direction.WEST;
        } else {
            return lookVec.z > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private static Direction[] getSearchOrder(Direction primary, Vec3d lookVec) {
        Direction[] all = Direction.values();
        Direction[] result = new Direction[6];
        int idx = 0;

        for (Direction dir : all) {
            if (dir != primary && dir != primary.getOpposite()) {
                result[idx++] = dir;
            }
        }
        result[idx++] = primary;
        result[idx] = primary.getOpposite();

        return result;
    }

    private static boolean isValidLedgePlacement(World world, BlockPos placePos, BlockPos supportPos) {
        if (!world.isAir(placePos)) return false;
        return !world.isAir(supportPos);
    }
}
