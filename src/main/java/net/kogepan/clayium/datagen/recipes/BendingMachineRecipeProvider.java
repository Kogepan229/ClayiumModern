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
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BendingMachineRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);

        output.accept(Clayium.id("bending_machine/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_place", Blocks.CLAY.asItem().getDefaultInstance(), ClayiumItems.CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "raw_clay_slicer", ClayiumItems.CLAY_DISC.toStack(), ClayiumItems.RAW_CLAY_SLICER.toStack(), CEUtils.TEN_MICRO_CE, 1, 0);
        create(recipeOutput, "clay_disc", ClayiumItems.LARGE_CLAY_BALL.toStack(), ClayiumItems.CLAY_DISC.toStack(), CEUtils.TEN_MICRO_CE, 2, 0);
        create(recipeOutput, "small_clay_disc", Items.CLAY.getDefaultInstance(), ClayiumItems.SMALL_CLAY_DISC.toStack(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "dense_clay_plate", ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(), ClayiumItems.DENSE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "clay_blade", ClayiumItems.CLAY_CYLINDER.toStack(), ClayiumItems.CLAY_BLADE.toStack(2), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "large_clay_place", ClayiumItems.CLAY_PLATE.toStack(4), ClayiumItems.LARGE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "dense_clay_blade", ClayiumItems.DENSE_CLAY_CYLINDER.toStack(), ClayiumItems.DENSE_CLAY_BLADE.toStack(2), CEUtils.TEN_MICRO_CE, 8, 0);
        create(recipeOutput, "dense_large_clay_place", ClayiumItems.DENSE_CLAY_PLATE.toStack(4), ClayiumItems.DENSE_LARGE_CLAY_PLATE.toStack(), CEUtils.TEN_MICRO_CE, 8, 0);

        // spotless:on
    }
}
