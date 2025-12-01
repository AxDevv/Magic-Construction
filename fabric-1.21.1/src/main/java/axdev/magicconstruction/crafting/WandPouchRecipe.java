package axdev.magicconstruction.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import axdev.magicconstruction.items.ItemBlockPouch;
import axdev.magicconstruction.items.wand.ItemWand;

public class WandPouchRecipe extends SpecialCraftingRecipe {
    public WandPouchRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        ItemStack wandStack = ItemStack.EMPTY;
        ItemStack pouchStack = ItemStack.EMPTY;
        int itemCount = 0;

        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            itemCount++;
            if (stack.getItem() instanceof ItemWand) {
                if (!wandStack.isEmpty()) return false;
                wandStack = stack;
            } else if (stack.getItem() instanceof ItemBlockPouch) {
                if (!pouchStack.isEmpty()) return false;
                pouchStack = stack;
            } else {
                return false;
            }
        }

        if (wandStack.isEmpty() || pouchStack.isEmpty() || itemCount != 2) return false;

        return !ItemWand.hasPouch(wandStack);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        ItemStack wandStack = ItemStack.EMPTY;
        ItemStack pouchStack = ItemStack.EMPTY;

        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ItemWand) {
                wandStack = stack;
            } else if (stack.getItem() instanceof ItemBlockPouch) {
                pouchStack = stack;
            }
        }

        if (wandStack.isEmpty() || pouchStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = wandStack.copy();
        ItemBlockPouch pouch = (ItemBlockPouch) pouchStack.getItem();
        ItemWand.setPouchTier(result, pouch.getTier());

        return result;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput input) {
        return DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.WAND_POUCH_SERIALIZER;
    }
}
