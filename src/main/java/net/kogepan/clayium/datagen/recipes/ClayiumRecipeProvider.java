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
