package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ClayiumRecipeProvider extends RecipeProvider {

    public ClayiumRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ClayWorkTableRecipeProvider.buildRecipes(recipeOutput);
        BendingMachineRecipeProvider.buildRecipes(recipeOutput);
        WireDrawingMachineRecipeProvider.buildRecipes(recipeOutput);
        PipeDrawingMachineRecipeProvider.buildRecipes(recipeOutput);
        CuttingMachineRecipeProvider.buildRecipes(recipeOutput);
        LatheRecipeProvider.buildRecipes(recipeOutput);
        MillingMachineRecipeProvider.buildRecipes(recipeOutput);
        ClayCondenserRecipeProvider.buildRecipes(recipeOutput);
        GrinderRecipeProvider.buildRecipes(recipeOutput);
        DecomposerRecipeProvider.buildRecipes(recipeOutput);
        InscriberRecipeProvider.buildRecipes(recipeOutput);
        SmelterRecipeProvider.buildRecipes(recipeOutput);
        QuartzCrucibleRecipeProvider.buildRecipes(recipeOutput);
        LaserIrradiationRecipeProvider.buildRecipes(recipeOutput);

        // Laser Reflector
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.LASER_REFLECTOR.get())
                .pattern(" G ")
                .pattern("GLG")
                .pattern(" G ")
                .define('G', Items.GLASS_PANE)
                .define('L', ClayiumBlocks.CLAY_LASER_BLOCKS.get(7).get())
                .unlockedBy("has_clay_laser", has(ClayiumBlocks.CLAY_LASER_BLOCKS.get(7).get()))
                .save(recipeOutput, Clayium.id("laser_reflector"));

        // Quartz Crucible
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ClayiumBlocks.QUARTZ_CRUCIBLE.get())
                .pattern("Q Q")
                .pattern("Q Q")
                .pattern("QQQ")
                .define('Q', Items.QUARTZ)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(recipeOutput, Clayium.id("quartz_crucible"));
    }
}
