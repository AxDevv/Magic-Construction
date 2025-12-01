package axdev.magicconstruction.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import axdev.magicconstruction.containers.BlockPouchMenu;
import axdev.magicconstruction.items.wand.ItemWand;

public class BlockPouchScreen extends AbstractContainerScreen<BlockPouchMenu> {
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

    public BlockPouchScreen(BlockPouchMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.pouchSize = menu.getPouchSize();
        this.rows = (int) Math.ceil(pouchSize / 9.0);
        this.imageWidth = 176;
        this.imageHeight = 18 + rows * 18 + 14 + 14 + 54 + 4 + 18 + 8 + 2;
        this.inventoryLabelY = 18 + rows * 18 + 14 + 4 + 2;
        this.pouchCapacity = ItemWand.getPouchCapacity(playerInv.player.getMainHandItem());
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        drawPanel(graphics, x, y, imageWidth, imageHeight);

        int pouchY = y + 17;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                if (index < pouchSize) {
                    int slotX = x + 7 + col * 18;
                    int slotY = pouchY + row * 18;
                    drawSlot(graphics, slotX, slotY);
                }
            }
        }

        int capacityBarY = pouchY + rows * 18 + 4;
        drawCapacityBar(graphics, x + 7, capacityBarY, 162);

        int invY = capacityBarY + 14 + 12 + 2;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotX = x + 7 + col * 18;
                int slotY = invY + row * 18;
                drawSlot(graphics, slotX, slotY);
            }
        }

        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            int slotX = x + 7 + col * 18;
            int slotY = hotbarY;
            drawSlot(graphics, slotX, slotY);
        }
    }

    private void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, BG_COLOR);
        graphics.fill(x, y, x + width - 1, y + 1, BORDER_WHITE);
        graphics.fill(x, y, x + 1, y + height - 1, BORDER_WHITE);
        graphics.fill(x + 1, y + 1, x + width - 2, y + 2, BORDER_WHITE);
        graphics.fill(x + 1, y + 1, x + 2, y + height - 2, BORDER_WHITE);
        graphics.fill(x + width - 2, y + 2, x + width - 1, y + height - 1, BORDER_DARK);
        graphics.fill(x + 2, y + height - 2, x + width - 1, y + height - 1, BORDER_DARK);
        graphics.fill(x + width - 1, y + 1, x + width, y + height, BORDER_DARKER);
        graphics.fill(x + 1, y + height - 1, x + width, y + height, BORDER_DARKER);
    }

    private void drawSlot(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, SLOT_BG);
        graphics.fill(x, y, x + 17, y + 1, SLOT_BORDER_DARK);
        graphics.fill(x, y, x + 1, y + 17, SLOT_BORDER_DARK);
        graphics.fill(x + 17, y + 1, x + 18, y + 18, SLOT_BORDER_LIGHT);
        graphics.fill(x + 1, y + 17, x + 18, y + 18, SLOT_BORDER_LIGHT);
    }

    private void drawCapacityBar(GuiGraphics graphics, int x, int y, int width) {
        currentItems = 0;
        NonNullList<ItemStack> contents = ItemWand.getPouchContents(minecraft.player.getMainHandItem());
        for (ItemStack stack : contents) {
            currentItems += stack.getCount();
        }

        graphics.fill(x, y, x + width, y + 10, SLOT_BORDER_DARK);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 9, SLOT_BG);

        float fillPercent = pouchCapacity > 0 ? (float) currentItems / pouchCapacity : 0;
        int fillWidth = (int) ((width - 4) * fillPercent);

        if (fillWidth > 0) {
            int barColor = fillPercent > 0.9f ? 0xFFE74C3C : (fillPercent > 0.7f ? 0xFFF39C12 : 0xFF55AA55);
            int barColorDark = fillPercent > 0.9f ? 0xFFC0392B : (fillPercent > 0.7f ? 0xFFD68910 : 0xFF3D7A3D);

            graphics.fill(x + 2, y + 2, x + 2 + fillWidth, y + 8, barColor);
            graphics.fill(x + 2, y + 6, x + 2 + fillWidth, y + 8, barColorDark);
        }

        String capacityText = currentItems + " / " + pouchCapacity;
        int textWidth = this.font.width(capacityText);
        graphics.drawString(this.font, capacityText, x + (width - textWidth) / 2, y + 1, 0xFFFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 6, TEXT_DARK, false);

        String tierText = getTierText();
        int tierTextX = this.imageWidth - this.font.width(tierText) - 7;
        graphics.drawString(this.font, tierText, tierTextX, 6, TEXT_DARK, false);

        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, TEXT_DARK, false);
    }

    private String getTierText() {
        int tier = ItemWand.getPouchTier(minecraft.player.getMainHandItem());
        return switch (tier) {
            case 1 -> "Basic";
            case 2 -> "Iron";
            case 3 -> "Gold";
            case 4 -> "Diamond";
            default -> "";
        };
    }
}
