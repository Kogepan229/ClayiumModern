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

public class DecomposerRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.DECOMPOSER_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("decomposer/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Clay block -> 4 clay ball (Original: 1L, 3L)
        create(recipeOutput, "clay_to_balls", Blocks.CLAY.asItem().getDefaultInstance(),
                new ItemStack(Items.CLAY_BALL, 4), CEUtils.TEN_MICRO_CE, 3, 0);

        // Compressed clay decompression chain (Original: 1L, 3/3/10/20)
        create(recipeOutput, "compressed_clay_0_to_clay",
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(),
                new ItemStack(Blocks.CLAY.asItem(), 9), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "compressed_clay_1_to_0",
                ClayiumBlocks.COMPRESSED_CLAYS.get(1).asItem().getDefaultInstance(),
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance().copyWithCount(9),
                CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "compressed_clay_2_to_1",
                ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem().getDefaultInstance(),
                ClayiumBlocks.COMPRESSED_CLAYS.get(1).asItem().getDefaultInstance().copyWithCount(9),
                CEUtils.TEN_MICRO_CE, 10, 0);
        create(recipeOutput, "compressed_clay_3_to_2",
                ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem().getDefaultInstance(),
                ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem().getDefaultInstance().copyWithCount(9),
                CEUtils.TEN_MICRO_CE, 20, 0);

        // IND clay dust -> 3 Energized Clay Dust (Original: 1L, divideByProgressionRateI(60))
        create(recipeOutput, "industrial_clay_dust_to_energized",
                ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack(),
                ClayiumItems.ENERGIZED_CLAY_DUST.toStack(3), CEUtils.TEN_MICRO_CE, 60, 0);

        // ADVIND clay dust -> 28 Energized Clay Dust (Original: tier 4, 1000L, 60)
        create(recipeOutput, "advanced_industrial_clay_dust_to_energized",
                ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(),
                ClayiumItems.ENERGIZED_CLAY_DUST.toStack(28), CEUtils.ONE_MILLI_CE * 10, 60, 4);

        // spotless:on
    }
}
