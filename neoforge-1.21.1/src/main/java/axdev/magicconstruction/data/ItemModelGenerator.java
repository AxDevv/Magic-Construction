package axdev.magicconstruction.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import axdev.magicconstruction.MagicConstruction;
import axdev.magicconstruction.items.ModItems;

import javax.annotation.Nonnull;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, MagicConstruction.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for(DeferredHolder<Item, ?> itemHolder : ModItems.ITEMS.getEntries()) {
            Item item = itemHolder.get();
            String name = BuiltInRegistries.ITEM.getKey(item).getPath();
            withExistingParent(name, "item/handheld").texture("layer0", "item/" + name);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return MagicConstruction.MODNAME + " item models";
    }
}
