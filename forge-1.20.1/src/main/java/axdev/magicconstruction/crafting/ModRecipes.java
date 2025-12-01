package axdev.magicconstruction.crafting;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import axdev.magicconstruction.MagicConstruction;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MagicConstruction.MODID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<WandPouchRecipe>> WAND_POUCH_SERIALIZER =
            RECIPE_SERIALIZERS.register("wand_pouch_combine", () -> new SimpleCraftingRecipeSerializer<>(WandPouchRecipe::new));
}
