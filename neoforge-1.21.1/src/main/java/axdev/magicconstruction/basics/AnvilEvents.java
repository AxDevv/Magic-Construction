package axdev.magicconstruction.basics;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.basics.ConfigServer;
import axdev.magicconstruction.items.ItemRepairRune;
import axdev.magicconstruction.items.wand.ItemWand;

@EventBusSubscriber(modid = MagicConstruction.MODID)
public class AnvilEvents {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if(!ConfigServer.RUNES_ENABLED.get()) return;

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if(right.getItem() instanceof ItemRepairRune rune) {
            boolean isWand = left.getItem() instanceof ItemWand;
            boolean canRepair = isWand || !ConfigServer.RUNES_WAND_ONLY.get();

            if(!canRepair || !left.isDamageableItem()) {
                return;
            }
            int maxDamage = left.getMaxDamage();
            int currentDamage = left.getDamageValue();

            if(currentDamage == 0) {
                return;
            }

            double repairPercent = rune.getRepairPercent() >= 1.0f
                    ? ConfigServer.RUNE_MAJOR_REPAIR_PERCENT.get()
                    : ConfigServer.RUNE_MINOR_REPAIR_PERCENT.get();
            int xpCost = rune.getRepairPercent() >= 1.0f
                    ? ConfigServer.RUNE_MAJOR_XP_COST.get()
                    : ConfigServer.RUNE_MINOR_XP_COST.get();

            int repairAmount = (int)(maxDamage * repairPercent);
            int newDamage = Math.max(0, currentDamage - repairAmount);

            ItemStack result = left.copy();
            result.setDamageValue(newDamage);

            event.setOutput(result);
            event.setCost(xpCost);
            event.setMaterialCost(1);
        }
    }
}
