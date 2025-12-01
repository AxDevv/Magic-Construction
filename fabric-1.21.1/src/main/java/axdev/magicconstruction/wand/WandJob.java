package axdev.magicconstruction.wand;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.wand.undo.ISnapshot;
import axdev.magicconstruction.wand.undo.PlaceSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WandJob {
    public final PlayerEntity player;
    public final World world;
    public final BlockHitResult rayTraceResult;
    public final ItemStack wand;
    public final ItemWand wandItem;

    private List<ISnapshot> placeSnapshots;

    public WandJob(PlayerEntity player, World world, BlockHitResult rayTraceResult, ItemStack wand) {
        this.player = player;
        this.world = world;
        this.rayTraceResult = rayTraceResult;
        this.placeSnapshots = new ArrayList<>();
        this.wand = wand;
        this.wandItem = (ItemWand) wand.getItem();
    }

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
                        wand.damage(1, player, EquipmentSlot.MAINHAND);
                    } else {
                        snapshot.forceRestore(world);
                    }
                }
            }
        }
        placeSnapshots = executed;

        if (!placeSnapshots.isEmpty()) {
            BlockSoundGroup sound = placeSnapshots.get(0).getBlockState().getSoundGroup();
            world.playSound(null, player.getBlockPos(), sound.getPlaceSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);
            MagicConstruction.undoHistory.add(player, world, placeSnapshots);
        }

        return !placeSnapshots.isEmpty();
    }

    private boolean consumeItem(BlockItem targetItem) {
        if (ItemWand.hasPouch(wand) && ItemWand.consumeFromPouch(wand, targetItem, 1, world)) {
            return true;
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() == targetItem && stack.getCount() > 0) {
                stack.decrement(1);
                return true;
            }
        }
        return false;
    }
}
