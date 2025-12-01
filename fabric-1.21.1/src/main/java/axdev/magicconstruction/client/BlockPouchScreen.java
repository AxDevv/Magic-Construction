package axdev.magicconstruction.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import axdev.magicconstruction.containers.BlockPouchScreenHandler;
import axdev.magicconstruction.items.wand.ItemWand;

public class BlockPouchScreen extends HandledScreen<BlockPouchScreenHandler> {
    private final int rows;
    private final int pouchCapacity;
    private final int pouchSize;
    private int currentItems = 0;

    private static final int BG_COLOR = 0xFFC6C6C6;
    private static final int BORDER_WHITE = 0xFFFFFFFF;
    private static final int BORDER_DARK = 0xFF555555;
    private static final int BORDER_DARKER = 0xFF373737;
    private static final int SLOT_BG = 0xFF8B8B8B;
    private static final int SLOT_BORDER_DARK = 0xFF373737;
    private static final int SLOT_BORDER_LIGHT = 0xFFFFFFFF;
    private static final int TEXT_DARK = 0xFF404040;

    public BlockPouchScreen(BlockPouchScreenHandler handler, PlayerInventory playerInv, Text title) {
        super(handler, playerInv, title);
        this.pouchSize = handler.getPouchSize();
        this.rows = (int) Math.ceil(pouchSize / 9.0);
        this.backgroundWidth = 176;
        this.backgroundHeight = 18 + rows * 18 + 14 + 14 + 54 + 4 + 18 + 8 + 2;
        this.playerInventoryTitleY = 18 + rows * 18 + 14 + 4 + 2;
        this.pouchCapacity = ItemWand.getPouchCapacity(playerInv.player.getMainHandStack());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        drawPanel(context, x, y, backgroundWidth, backgroundHeight);

        int pouchY = y + 17;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                if (index < pouchSize) {
                    int slotX = x + 7 + col * 18;
                    int slotY = pouchY + row * 18;
                    drawSlot(context, slotX, slotY);
                }
            }
        }

        int capacityBarY = pouchY + rows * 18 + 4;
        drawCapacityBar(context, x + 7, capacityBarY, 162);

        int invY = capacityBarY + 14 + 12 + 2;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotX = x + 7 + col * 18;
                int slotY = invY + row * 18;
                drawSlot(context, slotX, slotY);
            }
        }

        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            int slotX = x + 7 + col * 18;
            int slotY = hotbarY;
            drawSlot(context, slotX, slotY);
        }
    }

    private void drawPanel(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y, x + width, y + height, BG_COLOR);
        context.fill(x, y, x + width - 1, y + 1, BORDER_WHITE);
        context.fill(x, y, x + 1, y + height - 1, BORDER_WHITE);
        context.fill(x + 1, y + 1, x + width - 2, y + 2, BORDER_WHITE);
        context.fill(x + 1, y + 1, x + 2, y + height - 2, BORDER_WHITE);
        context.fill(x + width - 2, y + 2, x + width - 1, y + height - 1, BORDER_DARK);
        context.fill(x + 2, y + height - 2, x + width - 1, y + height - 1, BORDER_DARK);
        context.fill(x + width - 1, y + 1, x + width, y + height, BORDER_DARKER);
        context.fill(x + 1, y + height - 1, x + width, y + height, BORDER_DARKER);
    }

    private void drawSlot(DrawContext context, int x, int y) {
        context.fill(x, y, x + 18, y + 18, SLOT_BG);
        context.fill(x, y, x + 17, y + 1, SLOT_BORDER_DARK);
        context.fill(x, y, x + 1, y + 17, SLOT_BORDER_DARK);
        context.fill(x + 17, y + 1, x + 18, y + 18, SLOT_BORDER_LIGHT);
        context.fill(x + 1, y + 17, x + 18, y + 18, SLOT_BORDER_LIGHT);
    }

    private void drawCapacityBar(DrawContext context, int x, int y, int width) {
        currentItems = 0;
        DefaultedList<ItemStack> contents = ItemWand.getPouchContents(client.player.getMainHandStack(), client.player.getWorld());
        for (ItemStack stack : contents) {
            currentItems += stack.getCount();
        }

        context.fill(x, y, x + width, y + 10, SLOT_BORDER_DARK);
        context.fill(x + 1, y + 1, x + width - 1, y + 9, SLOT_BG);

        float fillPercent = pouchCapacity > 0 ? (float) currentItems / pouchCapacity : 0;
        int fillWidth = (int) ((width - 4) * fillPercent);

        if (fillWidth > 0) {
            int barColor = fillPercent > 0.9f ? 0xFFE74C3C : (fillPercent > 0.7f ? 0xFFF39C12 : 0xFF55AA55);
            int barColorDark = fillPercent > 0.9f ? 0xFFC0392B : (fillPercent > 0.7f ? 0xFFD68910 : 0xFF3D7A3D);

            context.fill(x + 2, y + 2, x + 2 + fillWidth, y + 8, barColor);
            context.fill(x + 2, y + 6, x + 2 + fillWidth, y + 8, barColorDark);
        }

        String capacityText = currentItems + " / " + pouchCapacity;
        int textWidth = this.textRenderer.getWidth(capacityText);
        context.drawText(this.textRenderer, capacityText, x + (width - textWidth) / 2, y + 1, 0xFFFFFFFF, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, 6, TEXT_DARK, false);

        String tierText = getTierText();
        int tierTextX = this.backgroundWidth - this.textRenderer.getWidth(tierText) - 7;
        context.drawText(this.textRenderer, tierText, tierTextX, 6, TEXT_DARK, false);

        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, TEXT_DARK, false);
    }

    private String getTierText() {
        int tier = ItemWand.getPouchTier(client.player.getMainHandStack());
        return switch (tier) {
            case 1 -> "Basic";
            case 2 -> "Iron";
            case 3 -> "Gold";
            case 4 -> "Diamond";
            default -> "";
        };
    }
}
