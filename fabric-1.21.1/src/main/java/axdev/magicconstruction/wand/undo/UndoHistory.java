package axdev.magicconstruction.wand.undo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.network.PacketUndoBlocks;

import java.util.*;
import java.util.stream.Collectors;

public class UndoHistory {
    private static final long CONFIRM_TIMEOUT = 3000;

    private final Map<UUID, PlayerHistory> history = new HashMap<>();

    private PlayerHistory getHistory(PlayerEntity player) {
        return history.computeIfAbsent(player.getUuid(), k -> new PlayerHistory());
    }

    public void add(PlayerEntity player, World world, List<ISnapshot> snapshots) {
        PlayerHistory ph = getHistory(player);
        ph.entries.add(new UndoEntry(world, snapshots));
        ph.pendingConfirm = false;
        ph.lastUndoRequest = 0;

        while (ph.entries.size() > 5) {
            ph.entries.remove(0);
        }
    }

    public void tryUndo(PlayerEntity player, World world, ItemStack wand) {
        PlayerHistory ph = getHistory(player);

        if (ph.entries.isEmpty()) {
            player.sendMessage(Text.translatable("magicconstruction.message.undo_empty").formatted(Formatting.RED), true);
            return;
        }

        long now = System.currentTimeMillis();

        if (ph.pendingConfirm && (now - ph.lastUndoRequest) < CONFIRM_TIMEOUT) {
            executeUndo(player, world, ph);
        } else {
            ph.pendingConfirm = true;
            ph.lastUndoRequest = now;
            sendUndoPreview(player, ph);
            player.sendMessage(Text.translatable("magicconstruction.message.undo_confirm").formatted(Formatting.YELLOW), true);
        }
    }

    private void executeUndo(PlayerEntity player, World world, PlayerHistory ph) {
        UndoEntry entry = null;
        for (int i = ph.entries.size() - 1; i >= 0; i--) {
            if (ph.entries.get(i).world.getRegistryKey().equals(world.getRegistryKey())) {
                entry = ph.entries.remove(i);
                break;
            }
        }

        ph.pendingConfirm = false;
        ph.lastUndoRequest = 0;

        if (entry == null) {
            player.sendMessage(Text.translatable("magicconstruction.message.undo_empty").formatted(Formatting.RED), true);
            return;
        }

        int undoneCount = 0;
        int skippedCount = 0;

        for (ISnapshot snapshot : entry.snapshots) {
            if (snapshot.canRestore(world)) {
                ItemStack returned = snapshot.restore(world);
                if (!returned.isEmpty()) {
                    if (!player.getInventory().insertStack(returned)) {
                        player.dropItem(returned, false);
                    }
                    undoneCount++;
                }
            } else {
                skippedCount++;
            }
        }

        if (undoneCount > 0) {
            if (skippedCount > 0) {
                player.sendMessage(Text.translatable("magicconstruction.message.undo_partial", undoneCount, skippedCount).formatted(Formatting.YELLOW), true);
            } else {
                player.sendMessage(Text.translatable("magicconstruction.message.undo_success", undoneCount).formatted(Formatting.GREEN), true);
            }
        } else {
            player.sendMessage(Text.translatable("magicconstruction.message.undo_failed").formatted(Formatting.RED), true);
        }

        clearUndoPreview(player);
    }

    private void sendUndoPreview(PlayerEntity player, PlayerHistory ph) {
        if (ph.entries.isEmpty()) return;

        UndoEntry entry = ph.entries.get(ph.entries.size() - 1);
        if (!entry.world.getRegistryKey().equals(player.getWorld().getRegistryKey())) return;

        Set<BlockPos> positions = entry.snapshots.stream()
                .map(ISnapshot::getPos)
                .collect(Collectors.toSet());

        if (player instanceof ServerPlayerEntity sp) {
            PacketUndoBlocks.send(sp, positions);
        }
    }

    private void clearUndoPreview(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity sp) {
            PacketUndoBlocks.send(sp, Collections.emptySet());
        }
    }

    public boolean hasPendingConfirm(PlayerEntity player) {
        PlayerHistory ph = getHistory(player);
        if (!ph.pendingConfirm) return false;

        long now = System.currentTimeMillis();
        if ((now - ph.lastUndoRequest) >= CONFIRM_TIMEOUT) {
            ph.pendingConfirm = false;
            return false;
        }
        return true;
    }

    private static class PlayerHistory {
        public final LinkedList<UndoEntry> entries = new LinkedList<>();
        public boolean pendingConfirm = false;
        public long lastUndoRequest = 0;
    }

    private static class UndoEntry {
        public final World world;
        public final List<ISnapshot> snapshots;

        UndoEntry(World world, List<ISnapshot> snapshots) {
            this.world = world;
            this.snapshots = snapshots;
        }
    }
}
