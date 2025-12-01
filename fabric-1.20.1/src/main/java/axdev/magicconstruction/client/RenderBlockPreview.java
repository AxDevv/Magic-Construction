package axdev.magicconstruction.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import axdev.magicconstruction.basics.WandUtil;
import axdev.magicconstruction.items.wand.ItemWand;
import axdev.magicconstruction.wand.LedgeDetector;
import axdev.magicconstruction.wand.WandJob;

import java.util.Set;

public class RenderBlockPreview {
    private static final long UNDO_PREVIEW_TIMEOUT = 3000;

    private static WandJob wandJob;
    private static WandJob ledgeWandJob;
    private static BlockHitResult cachedRayTrace;
    private static BlockHitResult cachedLedgeResult;
    private static Set<BlockPos> undoBlocks;
    private static long undoBlocksTimestamp;

    public static void register() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, hitResult) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return true;

            PlayerEntity player = mc.player;
            ItemStack wand = WandUtil.holdingWand(player);
            if (wand == null) return true;

            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                renderBlockHighlight(context, player, wand, blockHit);
                return false;
            }

            return true;
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;

            PlayerEntity player = mc.player;
            ItemStack wand = WandUtil.holdingWand(player);

            if (undoBlocks != null && !undoBlocks.isEmpty()) {
                if (isUndoPreviewExpired()) {
                    clearUndoBlocks();
                } else {
                    renderBlocks(context, player, undoBlocks, 1F, 0F, 0F);
                    return;
                }
            }

            if (wand == null) return;
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) return;

            BlockHitResult ledgeResult = LedgeDetector.detectLedge(player, player.getWorld());
            if (ledgeResult == null) {
                ledgeWandJob = null;
                cachedLedgeResult = null;
                return;
            }

            if (ledgeWandJob == null || cachedLedgeResult == null ||
                    !compareRTR(cachedLedgeResult, ledgeResult) || !ledgeWandJob.wand.equals(wand)) {
                ledgeWandJob = ItemWand.getWandJob(player, player.getWorld(), ledgeResult, wand);
                cachedLedgeResult = ledgeResult;
            }

            Set<BlockPos> blocks = ledgeWandJob.getBlockPositions();
            if (blocks == null || blocks.isEmpty()) return;

            renderBlocks(context, player, blocks, 0.2F, 0.6F, 1.0F);
        });
    }

    private static void renderBlockHighlight(WorldRenderContext context, PlayerEntity player, ItemStack wand, BlockHitResult rtr) {
        Set<BlockPos> blocks;
        float colorR = 0.2F, colorG = 0.6F, colorB = 1.0F;

        if (!player.isSneaking()) {
            if (wandJob == null || !compareRTR(cachedRayTrace, rtr) || !(wandJob.wand.equals(wand))
                    || wandJob.getBlockPositions().size() < 2) {
                wandJob = ItemWand.getWandJob(player, player.getWorld(), rtr, wand);
                cachedRayTrace = rtr;
            }
            blocks = wandJob.getBlockPositions();
        } else {
            if (undoBlocks != null && !undoBlocks.isEmpty() && !isUndoPreviewExpired()) {
                blocks = undoBlocks;
                colorR = 1;
                colorG = 0;
                colorB = 0;
            } else {
                clearUndoBlocks();
                return;
            }
        }

        if (blocks == null || blocks.isEmpty()) return;

        renderBlocks(context, player, blocks, colorR, colorG, colorB);
    }

    private static void renderBlocks(WorldRenderContext context, PlayerEntity player, Set<BlockPos> blocks,
                                     float colorR, float colorG, float colorB) {
        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        Vec3d camPos = context.camera().getPos();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(2.0F);

        matrices.push();

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        for (BlockPos block : blocks) {
            Box aabb = new Box(block).offset(-camPos.x, -camPos.y, -camPos.z);
            drawBox(buffer, matrices, aabb, colorR, colorG, colorB, 0.4F);
        }

        tessellator.draw();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }

    private static void drawBox(BufferBuilder buffer, MatrixStack matrices, Box box,
                                float red, float green, float blue, float alpha) {
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        buffer.vertex(matrices.peek().getPositionMatrix(), minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, minY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, maxY, minZ).color(red, green, blue, alpha).next();

        buffer.vertex(matrices.peek().getPositionMatrix(), minX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, maxY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, maxY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, minY, minZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, minY, maxZ).color(red, green, blue, alpha).next();

        buffer.vertex(matrices.peek().getPositionMatrix(), minX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), maxX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, minY, maxZ).color(red, green, blue, alpha).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), minX, maxY, maxZ).color(red, green, blue, alpha).next();
    }

    public static BlockHitResult getLedgeResult(PlayerEntity player) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            return null;
        }
        return LedgeDetector.detectLedge(player, player.getWorld());
    }

    public static void reset() {
        wandJob = null;
        ledgeWandJob = null;
        cachedRayTrace = null;
        cachedLedgeResult = null;
    }

    private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
        if (rtr1 == null || rtr2 == null) return false;
        return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getSide().equals(rtr2.getSide());
    }

    public static void setUndoBlocks(Set<BlockPos> blocks) {
        undoBlocks = blocks;
        undoBlocksTimestamp = System.currentTimeMillis();
    }

    private static boolean isUndoPreviewExpired() {
        return System.currentTimeMillis() - undoBlocksTimestamp >= UNDO_PREVIEW_TIMEOUT;
    }

    private static void clearUndoBlocks() {
        undoBlocks = null;
        undoBlocksTimestamp = 0;
    }
}
