package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.QuartzCrucibleRecipe;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.NotNull;

public class QuartzCrucibleRecipeProvider {

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        recipeOutput.accept(Clayium.id("quartz_crucible/impure_silicon"),
                new QuartzCrucibleRecipe(
                        Ingredient.of(ClayiumItems.IMPURE_SILICON_INGOT.get()),
                        Ingredient.of(Items.STRING),
                        new ItemStack(ClayiumItems.SILICON_INGOT.get(), 1),
                        600),
                null);
    }
}
