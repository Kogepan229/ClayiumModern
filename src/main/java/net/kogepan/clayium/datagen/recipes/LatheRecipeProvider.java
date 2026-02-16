package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LatheRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.LATHE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);

        output.accept(Clayium.id("lathe/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_ball_to_short_stick", Items.CLAY_BALL.getDefaultInstance(), ClayiumItems.SHORT_CLAY_STICK.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "clay_large_ball_to_cylinder", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_CYLINDER.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "clay_cylinder_to_needle", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_NEEDLE.toStack(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_needle_to_stick", ClayiumItems.CLAY_NEEDLE.toStack(), ClayiumItems.CLAY_STICK.toStack(6), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_disc_to_ring", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.CLAY_RING.toStack(), CEUtils.TEN_MICRO_CE, 2, 0);
        create(recipeOutput, "clay_small_disc_to_small_ring", ClayiumItems.SMALL_CLAY_DISC.toStack(), ClayiumItems.SMALL_CLAY_RING.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "dense_clay_block_to_cylinder", new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(0), 2), ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "dense_clay_cylinder_to_needle", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), ClayiumItems.DENSE_CLAY_NEEDLE.toStack(), CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "dense_clay_needle_to_stick", ClayiumItems.DENSE_CLAY_NEEDLE.toStack(), ClayiumItems.DENSE_CLAY_STICK.toStack(6), CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "dense_clay_disc_to_ring", ClayiumItems.DENSE_CLAY_DISC.toStack(), ClayiumItems.DENSE_CLAY_RING.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "dense_clay_small_disc_to_small_ring", ClayiumItems.DENSE_SMALL_CLAY_DISC.toStack(), ClayiumItems.DENSE_SMALL_CLAY_RING.toStack(), CEUtils.TEN_MICRO_CE, 2, 0);

        // spotless:on
    }
}
