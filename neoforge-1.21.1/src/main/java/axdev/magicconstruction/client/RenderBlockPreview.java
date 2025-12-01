package axdev.magicconstruction.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.wand.LedgeDetector;
import axdev.magicconstruction.wand.WandJob;

import java.util.Set;

public class RenderBlockPreview
{
    private static final long UNDO_PREVIEW_TIMEOUT = 3000;

    private WandJob wandJob;
    private WandJob ledgeWandJob;
    private BlockHitResult cachedLedgeResult;
    public Set<BlockPos> undoBlocks;
    private long undoBlocksTimestamp;

    @SubscribeEvent
    public void renderBlockHighlight(RenderHighlightEvent.Block event) {
        if(event.getTarget().getType() != HitResult.Type.BLOCK) return;

        BlockHitResult rtr = event.getTarget();
        Entity entity = event.getCamera().getEntity();
        if(!(entity instanceof Player player)) return;
        Set<BlockPos> blocks;
        float colorR = 0.2F, colorG = 0.6F, colorB = 1.0F;

        ItemStack wand = WandUtil.holdingWand(player);
        if(wand == null) return;

        if(!player.isCrouching()) {
            if(wandJob == null || !compareRTR(wandJob.rayTraceResult, rtr) || !(wandJob.wand.equals(wand))
                || wandJob.blockCount() < 2) {
                wandJob = ItemWand.getWandJob(player, player.level(), rtr, wand);
            }
            blocks = wandJob.getBlockPositions();
        }
        else {
            if(undoBlocks != null && !undoBlocks.isEmpty() && !isUndoPreviewExpired()) {
                blocks = undoBlocks;
                colorR = 1;
                colorG = 0;
                colorB = 0;
            } else {
                clearUndoBlocks();
                return;
            }
        }

        if(blocks == null || blocks.isEmpty()) return;

        renderBlocks(event.getPoseStack(), event.getMultiBufferSource(), event.getDeltaTracker(),
                     player, blocks, colorR, colorG, colorB);

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void renderLedgePreview(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;

        Player player = mc.player;
        ItemStack wand = WandUtil.holdingWand(player);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        if(undoBlocks != null && !undoBlocks.isEmpty()) {
            if(isUndoPreviewExpired()) {
                clearUndoBlocks();
            } else {
                renderBlocks(poseStack, buffer, event.getPartialTick(), player, undoBlocks, 1F, 0F, 0F);
                buffer.endBatch();
                return;
            }
        }

        if(wand == null) return;
        if(mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) return;

        BlockHitResult ledgeResult = LedgeDetector.detectLedge(player, player.level());
        if(ledgeResult == null) {
            ledgeWandJob = null;
            cachedLedgeResult = null;
            return;
        }

        if(ledgeWandJob == null || cachedLedgeResult == null ||
           !compareRTR(cachedLedgeResult, ledgeResult) || !ledgeWandJob.wand.equals(wand)) {
            ledgeWandJob = ItemWand.getWandJob(player, player.level(), ledgeResult, wand);
            cachedLedgeResult = ledgeResult;
        }

        Set<BlockPos> blocks = ledgeWandJob.getBlockPositions();
        if(blocks == null || blocks.isEmpty()) return;

        renderBlocks(poseStack, buffer, event.getPartialTick(), player, blocks, 0.2F, 0.6F, 1.0F);

        buffer.endBatch();
    }

    private void renderBlocks(PoseStack ms, MultiBufferSource buffer, DeltaTracker deltaTracker,
                              Player player, Set<BlockPos> blocks, float colorR, float colorG, float colorB) {
        VertexConsumer lineBuilder = buffer.getBuffer(RenderType.LINES);

        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        double d0 = player.xOld + (player.getX() - player.xOld) * partialTicks;
        double d1 = player.yOld + player.getEyeHeight() + (player.getY() - player.yOld) * partialTicks;
        double d2 = player.zOld + (player.getZ() - player.zOld) * partialTicks;

        for(BlockPos block : blocks) {
            AABB aabb = new AABB(block).move(-d0, -d1, -d2);
            LevelRenderer.renderLineBox(ms, lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
        }
    }

    public BlockHitResult getLedgeResult(Player player) {
        if(Minecraft.getInstance().hitResult != null &&
           Minecraft.getInstance().hitResult.getType() == HitResult.Type.BLOCK) {
            return null;
        }
        return LedgeDetector.detectLedge(player, player.level());
    }

    public void reset() {
        wandJob = null;
        ledgeWandJob = null;
        cachedLedgeResult = null;
    }

    private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
        if(rtr1 == null || rtr2 == null) return false;
        return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getDirection().equals(rtr2.getDirection());
    }

    public void setUndoBlocks(Set<BlockPos> blocks) {
        this.undoBlocks = blocks;
        this.undoBlocksTimestamp = System.currentTimeMillis();
    }

    private boolean isUndoPreviewExpired() {
        return System.currentTimeMillis() - undoBlocksTimestamp >= UNDO_PREVIEW_TIMEOUT;
    }

    private void clearUndoBlocks() {
        undoBlocks = null;
        undoBlocksTimestamp = 0;
    }
}
