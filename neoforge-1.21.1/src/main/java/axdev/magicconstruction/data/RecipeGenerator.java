package axdev.magicconstruction.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import axdev.magicconstruction.items.ModItems;

import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COBBLE_WAND.get())
                .define('C', Items.COBBLESTONE)
                .define('S', Items.STICK)
                .define('F', Items.FLINT)
                .pattern(" CF")
                .pattern(" SC")
                .pattern("S  ")
                .unlockedBy("has_item", has(Items.COBBLESTONE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BUILDER_WAND.get())
                .define('W', ModItems.COBBLE_WAND.get())
                .define('I', Items.IRON_INGOT)
                .define('B', Items.IRON_BLOCK)
                .define('R', Items.REDSTONE)
                .pattern("RIB")
                .pattern("IWI")
                .pattern("BIR")
                .unlockedBy("has_item", has(ModItems.COBBLE_WAND.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ENGINEER_WAND.get())
                .define('W', ModItems.BUILDER_WAND.get())
                .define('D', Items.DIAMOND)
                .define('B', Items.DIAMOND_BLOCK)
                .define('E', Items.EMERALD)
                .define('O', Items.OBSIDIAN)
                .pattern("EDB")
                .pattern("DWD")
                .pattern("BOE")
                .unlockedBy("has_item", has(ModItems.BUILDER_WAND.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ARCHITECT_WAND.get())
                .define('W', ModItems.ENGINEER_WAND.get())
                .define('N', Items.NETHERITE_INGOT)
                .define('S', Items.NETHER_STAR)
                .define('B', Items.NETHERITE_BLOCK)
                .define('E', Items.END_CRYSTAL)
                .pattern("ENB")
                .pattern("NWN")
                .pattern("BSE")
                .unlockedBy("has_item", has(ModItems.ENGINEER_WAND.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BLOCK_POUCH_1.get())
                .define('L', Items.LEATHER)
                .define('S', Items.STRING)
                .define('W', Items.WHITE_WOOL)
                .pattern("SLS")
                .pattern("LWL")
                .pattern("LLL")
                .unlockedBy("has_item", has(Items.LEATHER))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BLOCK_POUCH_2.get())
                .define('P', ModItems.BLOCK_POUCH_1.get())
                .define('I', Items.IRON_INGOT)
                .define('C', Items.CHAIN)
                .define('B', Items.IRON_BLOCK)
                .pattern("CIC")
                .pattern("BPB")
                .pattern("IBI")
                .unlockedBy("has_item", has(ModItems.BLOCK_POUCH_1.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BLOCK_POUCH_3.get())
                .define('P', ModItems.BLOCK_POUCH_2.get())
                .define('G', Items.GOLD_INGOT)
                .define('B', Items.GOLD_BLOCK)
                .define('E', Items.ENDER_PEARL)
                .define('L', Items.LAPIS_BLOCK)
                .pattern("EGE")
                .pattern("BPB")
                .pattern("LGL")
                .unlockedBy("has_item", has(ModItems.BLOCK_POUCH_2.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BLOCK_POUCH_4.get())
                .define('P', ModItems.BLOCK_POUCH_3.get())
                .define('D', Items.DIAMOND)
                .define('B', Items.DIAMOND_BLOCK)
                .define('S', Items.SHULKER_SHELL)
                .define('E', Items.ENDER_EYE)
                .pattern("SDS")
                .pattern("BPB")
                .pattern("EDE")
                .unlockedBy("has_item", has(ModItems.BLOCK_POUCH_3.get()))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REPAIR_RUNE_MINOR.get())
                .define('L', Items.LAPIS_LAZULI)
                .define('R', Items.REDSTONE)
                .define('G', Items.GOLD_INGOT)
                .define('A', Items.AMETHYST_SHARD)
                .define('E', Items.EXPERIENCE_BOTTLE)
                .pattern("LAL")
                .pattern("RER")
                .pattern("GRG")
                .unlockedBy("has_item", has(Items.EXPERIENCE_BOTTLE))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REPAIR_RUNE_MAJOR.get())
                .define('M', ModItems.REPAIR_RUNE_MINOR.get())
                .define('D', Items.DIAMOND)
                .define('N', Items.NETHERITE_SCRAP)
                .define('S', Items.NETHER_STAR)
                .define('E', Items.EMERALD_BLOCK)
                .pattern("DND")
                .pattern("MSM")
                .pattern("ENE")
                .unlockedBy("has_item", has(ModItems.REPAIR_RUNE_MINOR.get()))
                .save(output);
    }
}
