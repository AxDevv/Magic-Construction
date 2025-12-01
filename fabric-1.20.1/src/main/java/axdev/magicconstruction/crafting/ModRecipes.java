package axdev.magicconstruction.crafting;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import axdev.magicconstruction.MagicConstruction;

public class ModRecipes {
    public static final RecipeSerializer<WandPouchRecipe> WAND_POUCH_SERIALIZER =
            new SpecialRecipeSerializer<>(WandPouchRecipe::new);

    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, MagicConstruction.id("wand_pouch_combine"), WAND_POUCH_SERIALIZER);
    }
}
