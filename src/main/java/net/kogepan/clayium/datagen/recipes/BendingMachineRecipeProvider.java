package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.registries.ClayiumItems;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BendingMachineRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), energy, duration, tier);

        output.accept(Clayium.id("bending_machine/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        create(recipeOutput, "clay_place", Blocks.CLAY.asItem().getDefaultInstance(), ClayiumItems.CLAY_PLATE.toStack(),
                CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "large_clay_place", ClayiumItems.CLAY_PLATE.toStack(4),
                ClayiumItems.LARGE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE * 2, 2, 0);
    }
}
