package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.ClayWorkTableRecipe;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ClayWorkTableRecipeProvider extends RecipeProvider {

    public ClayWorkTableRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, int button,
                               int cost) {
        create(output, name, ingredient, result, ItemStack.EMPTY, button, cost);
    }

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               ItemStack byproduct, int button,
                               int cost) {
        ClayWorkTableRecipe recipe = new ClayWorkTableRecipe(ItemIngredientStack.of(ingredient), result, byproduct,
                button, cost);
        output.accept(Clayium.id("clay_work_table/" + name), recipe, null);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        create(output, "clay_stick", Items.CLAY_BALL.getDefaultInstance(),
                ClayiumItems.CLAY_STICK.get().getDefaultInstance(), 0, 4);
    }
}
