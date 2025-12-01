package axdev.magicconstruction.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import axdev.magicconstruction.items.ItemBlockPouch;
import axdev.magicconstruction.items.wand.ItemWand;

public class WandPouchRecipe extends CustomRecipe
{
    public WandPouchRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack wandStack = ItemStack.EMPTY;
        ItemStack pouchStack = ItemStack.EMPTY;
        int itemCount = 0;

        for(int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if(stack.isEmpty()) continue;

            itemCount++;
            if(stack.getItem() instanceof ItemWand) {
                if(!wandStack.isEmpty()) return false;
                wandStack = stack;
            }
            else if(stack.getItem() instanceof ItemBlockPouch) {
                if(!pouchStack.isEmpty()) return false;
                pouchStack = stack;
            }
            else {
                return false;
            }
        }

        if(wandStack.isEmpty() || pouchStack.isEmpty() || itemCount != 2) return false;

        return !ItemWand.hasPouch(wandStack);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack wandStack = ItemStack.EMPTY;
        ItemStack pouchStack = ItemStack.EMPTY;

        for(int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if(stack.isEmpty()) continue;

            if(stack.getItem() instanceof ItemWand) {
                wandStack = stack;
            }
            else if(stack.getItem() instanceof ItemBlockPouch) {
                pouchStack = stack;
            }
        }

        if(wandStack.isEmpty() || pouchStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = wandStack.copy();
        ItemBlockPouch pouch = (ItemBlockPouch) pouchStack.getItem();
        ItemWand.setPouchTier(result, pouch.getTier());

        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WAND_POUCH_SERIALIZER.get();
    }
}
