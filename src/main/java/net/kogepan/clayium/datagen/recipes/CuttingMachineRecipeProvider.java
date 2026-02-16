package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CuttingMachineRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CUTTING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);

        output.accept(Clayium.id("cutting_machine/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_large_ball_to_disc", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_DISC.toStack(), CEUtils.TEN_MICRO_CE, 2, 0);
        create(recipeOutput, "clay_cylinder_to_small_disc", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.SMALL_CLAY_DISC.toStack(8), CEUtils.TEN_MICRO_CE, 2, 0);
        create(recipeOutput, "clay_large_plate_to_disc", ClayiumItems.LARGE_CLAY_PLATE.toStack(), ClayiumItems.CLAY_DISC.toStack(2), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_plate_to_small_disc", ClayiumItems.CLAY_PLATE.toStack(), ClayiumItems.SMALL_CLAY_DISC.toStack(4), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_stick_to_short_stick", ClayiumItems.CLAY_STICK.toStack(), ClayiumItems.SHORT_CLAY_STICK.toStack(2), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "dense_clay_cylinder_to_small_disc", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), ClayiumItems.DENSE_SMALL_CLAY_DISC.toStack(8), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "dense_clay_large_plate_to_disc", ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(), ClayiumItems.DENSE_CLAY_DISC.toStack(2), CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "dense_clay_plate_to_small_disc", ClayiumItems.DENSE_CLAY_PLATE.toStack(), ClayiumItems.DENSE_SMALL_CLAY_DISC.toStack(4), CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "dense_clay_stick_to_short_stick", ClayiumItems.DENSE_CLAY_STICK.toStack(), ClayiumItems.DENSE_SHORT_CLAY_STICK.toStack(2), CEUtils.TEN_MICRO_CE, 2, 0);

        // spotless:on
    }
}
