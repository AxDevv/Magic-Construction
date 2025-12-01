package axdev.magicconstruction.wand.undo;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.network.ModMessages;
import axdev.magicconstruction.network.PacketUndoBlocks;

import java.util.*;
import java.util.stream.Collectors;

public class UndoHistory {
    private static final long CONFIRM_TIMEOUT = 3000;

    private final HashMap<UUID, PlayerHistory> history;

    public UndoHistory() {
        history = new HashMap<>();
    }

    private PlayerHistory getHistory(Player player) {
        return history.computeIfAbsent(player.getUUID(), k -> new PlayerHistory());
    }

    public void add(Player player, Level world, List<ISnapshot> snapshots) {
        PlayerHistory ph = getHistory(player);
        ph.entries.add(new UndoEntry(snapshots, world));
        ph.pendingConfirm = false;
        ph.lastUndoRequest = 0;
    }

    public void tryUndo(Player player) {
        PlayerHistory ph = getHistory(player);
        if(ph.entries.isEmpty()) {
            player.displayClientMessage(
                Component.translatable("magicconstruction.message.undo_empty").withStyle(ChatFormatting.RED),
                true
            );
            return;
        }

        long now = System.currentTimeMillis();

        if(ph.pendingConfirm && (now - ph.lastUndoRequest) < CONFIRM_TIMEOUT) {
            executeUndo(player, ph);
        } else {
            ph.pendingConfirm = true;
            ph.lastUndoRequest = now;
            sendUndoPreview(player, ph);
            player.displayClientMessage(
                Component.translatable("magicconstruction.message.undo_confirm").withStyle(ChatFormatting.YELLOW),
                true
            );
        }
    }

    private void executeUndo(Player player, PlayerHistory ph) {
        UndoEntry entry = ph.entries.removeLast();
        ph.pendingConfirm = false;
        ph.lastUndoRequest = 0;

        Level world = entry.world;
        int undoneCount = 0;
        int skippedCount = 0;

        for(ISnapshot snapshot : entry.snapshots) {
            if(snapshot.canRestore(world, player)) {
                if(snapshot.restore(world, player)) {
                    undoneCount++;
                    if(!player.isCreative()) {
                        ItemStack stack = snapshot.getRequiredItems();
                        ItemStack remaining = stack;

                        ItemStack wand = axdev.magicconstruction.basics.WandUtil.holdingWand(player);
                        if(wand != null && wand.getItem() instanceof ItemWand && ItemWand.hasPouch(wand)) {
                            remaining = ItemWand.addToPouchContents(wand, stack);
                        }

                        if(!remaining.isEmpty()) {
                            if(!player.getInventory().add(remaining)) {
                                player.drop(remaining, false);
                            }
                        }
                    }
                }
            } else {
                skippedCount++;
            }
        }

        player.getInventory().setChanged();

        if(undoneCount > 0) {
            world.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F);
            if(skippedCount > 0) {
                player.displayClientMessage(
                    Component.translatable("magicconstruction.message.undo_partial", undoneCount, skippedCount).withStyle(ChatFormatting.YELLOW),
                    true
                );
            } else {
                player.displayClientMessage(
                    Component.translatable("magicconstruction.message.undo_success", undoneCount).withStyle(ChatFormatting.GREEN),
                    true
                );
            }
        } else {
            player.displayClientMessage(
                Component.translatable("magicconstruction.message.undo_failed").withStyle(ChatFormatting.RED),
                true
            );
        }

        clearUndoPreview(player);
    }

    private void sendUndoPreview(Player player, PlayerHistory ph) {
        if(ph.entries.isEmpty()) return;

        UndoEntry entry = ph.entries.getLast();
        if(!entry.world.equals(player.level())) return;

        Set<BlockPos> positions = entry.snapshots.stream()
            .map(ISnapshot::getPos)
            .collect(Collectors.toSet());

        if(player instanceof ServerPlayer sp) {
            ModMessages.sendToPlayer(new PacketUndoBlocks(positions), sp);
        }
    }

    private void clearUndoPreview(Player player) {
        if(player instanceof ServerPlayer sp) {
            ModMessages.sendToPlayer(new PacketUndoBlocks(Collections.emptySet()), sp);
        }
    }

    public void removePlayer(Player player) {
        history.remove(player.getUUID());
    }

    public Set<BlockPos> getLastUndoPositions(Player player) {
        PlayerHistory ph = getHistory(player);
        if(ph.entries.isEmpty()) return Collections.emptySet();

        UndoEntry entry = ph.entries.getLast();
        if(!entry.world.equals(player.level())) return Collections.emptySet();

        return entry.snapshots.stream()
            .map(ISnapshot::getPos)
            .collect(Collectors.toSet());
    }

    public boolean hasPendingConfirm(Player player) {
        PlayerHistory ph = getHistory(player);
        if(!ph.pendingConfirm) return false;

        long now = System.currentTimeMillis();
        if((now - ph.lastUndoRequest) >= CONFIRM_TIMEOUT) {
            ph.pendingConfirm = false;
            return false;
        }
        return true;
    }

    public void updateClient(Player player, boolean show) {
        if(player.level().isClientSide) return;

        PlayerHistory ph = getHistory(player);
        if(show && !ph.entries.isEmpty()) {
            sendUndoPreview(player, ph);
        } else {
            clearUndoPreview(player);
        }
    }

    public boolean isUndoActive(Player player) {
        return hasPendingConfirm(player);
    }

    public boolean undo(Player player, Level world, BlockPos pos) {
        return false;
    }

    private static class PlayerHistory {
        public final LinkedList<UndoEntry> entries = new LinkedList<>();
        public boolean pendingConfirm = false;
        public long lastUndoRequest = 0;
    }

    private static class UndoEntry {
        public final List<ISnapshot> snapshots;
        public final Level world;

        public UndoEntry(List<ISnapshot> snapshots, Level world) {
            this.snapshots = snapshots;
            this.world = world;
        }
    }
}
