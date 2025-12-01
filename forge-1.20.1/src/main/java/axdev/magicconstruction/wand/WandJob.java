package axdev.magicconstruction.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.basics.ModStats;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.wand.undo.ISnapshot;
import axdev.magicconstruction.wand.undo.PlaceSnapshot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WandJob {
    public final Player player;
    public final Level world;
    public final BlockHitResult rayTraceResult;
    public final ItemStack wand;
    public final ItemWand wandItem;

    private List<ISnapshot> placeSnapshots;

    public WandJob(Player player, Level world, BlockHitResult rayTraceResult, ItemStack wand) {
        this.player = player;
        this.world = world;
        this.rayTraceResult = rayTraceResult;
        this.placeSnapshots = new ArrayList<>();
        this.wand = wand;
        this.wandItem = (ItemWand) wand.getItem();
    }

    @Nullable
    private BlockItem getTargetItem() {
        Item tgitem = world.getBlockState(rayTraceResult.getBlockPos()).getBlock().asItem();
        if (!(tgitem instanceof BlockItem)) return null;
        return (BlockItem) tgitem;
    }

    public void getSnapshots() {
        if (rayTraceResult.getType() != HitResult.Type.BLOCK) return;

        int limit = Math.min(wandItem.remainingDurability(wand), wandItem.getLimit());
        BlockItem targetItem = getTargetItem();
        if (targetItem == null) return;

        placeSnapshots = PlaceAction.getSnapshots(world, player, rayTraceResult, wand, targetItem, limit);
    }

    public Set<BlockPos> getBlockPositions() {
        return placeSnapshots.stream().map(ISnapshot::getPos).collect(Collectors.toSet());
    }

    public int blockCount() {
        return placeSnapshots.size();
    }

    public boolean doIt() {
        ArrayList<ISnapshot> executed = new ArrayList<>();
        BlockItem targetItem = getTargetItem();

        for (ISnapshot snapshot : placeSnapshots) {
            if (wand.isEmpty() || wandItem.remainingDurability(wand) == 0) break;

            if (snapshot.execute(world, player, rayTraceResult)) {
                if (player.isCreative()) {
                    executed.add(snapshot);
                } else {
                    boolean consumed = consumeItem(targetItem);
                    if (consumed) {
                        executed.add(snapshot);
                        wand.hurtAndBreak(1, player, p -> {});
                    } else {
                        snapshot.forceRestore(world);
                    }
                }
                player.awardStat(ModStats.USE_WAND);
            }
        }
        placeSnapshots = executed;

        if (!placeSnapshots.isEmpty()) {
            SoundType sound = placeSnapshots.get(0).getBlockState().getSoundType();
            world.playSound(null, player.blockPosition(), sound.getPlaceSound(), SoundSource.BLOCKS, sound.volume, sound.pitch);
            MagicConstruction.instance.undoHistory.add(player, world, placeSnapshots);
        }

        return !placeSnapshots.isEmpty();
    }

    private boolean consumeItem(BlockItem targetItem) {
        if (ItemWand.hasPouch(wand) && ItemWand.consumeFromPouch(wand, targetItem, 1)) {
            return true;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == targetItem && stack.getCount() > 0) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }
}
