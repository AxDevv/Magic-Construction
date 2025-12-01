package axdev.magicconstruction.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import axdev.magicconstruction.basics.ConfigServer;
import axdev.magicconstruction.items.ItemRepairRune;
import axdev.magicconstruction.items.wand.ItemWand;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    private Property levelCost;

    @Shadow
    private int repairItemUsage;

    public AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void onUpdateResult(CallbackInfo ci) {
        if (!ConfigServer.RUNES_ENABLED) return;

        ItemStack left = this.input.getStack(0);
        ItemStack right = this.input.getStack(1);

        if (right.getItem() instanceof ItemRepairRune rune) {
            boolean isWand = left.getItem() instanceof ItemWand;
            boolean canRepair = isWand || !ConfigServer.RUNES_WAND_ONLY;

            if (!canRepair || !left.isDamageable()) {
                return;
            }

            int maxDamage = left.getMaxDamage();
            int currentDamage = left.getDamage();

            if (currentDamage == 0) {
                return;
            }

            double repairPercent = rune.getRepairPercent() >= 1.0f
                    ? ConfigServer.RUNE_MAJOR_REPAIR_PERCENT
                    : ConfigServer.RUNE_MINOR_REPAIR_PERCENT;
            int xpCost = rune.getRepairPercent() >= 1.0f
                    ? ConfigServer.RUNE_MAJOR_XP_COST
                    : ConfigServer.RUNE_MINOR_XP_COST;

            int repairAmount = (int) (maxDamage * repairPercent);
            int newDamage = Math.max(0, currentDamage - repairAmount);

            ItemStack result = left.copy();
            result.setDamage(newDamage);

            this.output.setStack(0, result);
            this.levelCost.set(xpCost);
            this.repairItemUsage = 1;

            ci.cancel();
        }
    }
}
