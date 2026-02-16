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

public class PipeDrawingMachineRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.PIPE_DRAWING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);

        output.accept(Clayium.id("pipe_drawing_machine/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_cylinder_to_pipe", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_PIPE.toStack(2), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "dense_clay_cylinder_to_pipe", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), ClayiumItems.DENSE_CLAY_PIPE.toStack(2), CEUtils.TEN_MICRO_CE, 6, 0);

        // spotless:on
    }
}
