package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WireDrawingMachineRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.WIRE_DRAWING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);

        output.accept(Clayium.id("wire_drawing_machine/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_ball_to_stick", Items.CLAY_BALL.getDefaultInstance(), ClayiumItems.CLAY_STICK.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "clay_cylinder_to_stick", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_STICK.toStack(8), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "clay_pipe_to_stick", ClayiumItems.CLAY_PIPE.toStack(), ClayiumItems.CLAY_STICK.toStack(4), CEUtils.TEN_MICRO_CE, 2, 0);
        create(recipeOutput, "clay_small_disc_to_stick", ClayiumItems.SMALL_CLAY_DISC.toStack(), ClayiumItems.CLAY_STICK.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "dense_clay_cylinder_to_stick", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), ClayiumItems.DENSE_CLAY_STICK.toStack(8), CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "dense_clay_pipe_to_stick", ClayiumItems.DENSE_CLAY_PIPE.toStack(), ClayiumItems.DENSE_CLAY_STICK.toStack(4), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "dense_clay_small_disc_to_stick", ClayiumItems.DENSE_SMALL_CLAY_DISC.toStack(), ClayiumItems.DENSE_CLAY_STICK.toStack(), CEUtils.TEN_MICRO_CE, 2, 0);

        // spotless:on
    }
}
