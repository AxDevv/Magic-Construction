package axdev.magicconstruction.crafting;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import axdev.magicconstruction.MagicConstruction;

public class ModRecipes
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MagicConstruction.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WandPouchRecipe>> WAND_POUCH_SERIALIZER =
            RECIPE_SERIALIZERS.register("wand_pouch_combine", () -> new SimpleCraftingRecipeSerializer<>(WandPouchRecipe::new));
}
