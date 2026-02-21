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
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CentrifugeRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("centrifuge/" + name), machineRecipe, null);
    }

    private static void create(RecipeOutput output, String name, ItemStack ingredient, List<ItemStack> results,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get(),
                List.of(ItemIngredientStack.of(ingredient)), results, duration, energy, tier);
        output.accept(Clayium.id("centrifuge/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0
        create(recipeOutput, "clay_dust_to_dense",
                ClayiumItems.CLAY_DUST.toStack(9), ClayiumItems.DENSE_CLAY_DUST.toStack(),
                CEUtils.TEN_MICRO_CE * 4, 20, 0);
        create(recipeOutput, "dense_clay_dust_to_clay_calcareous",
                ClayiumItems.DENSE_CLAY_DUST.toStack(2),
                List.of(ClayiumItems.CLAY_DUST.toStack(9), ClayiumItems.CALCAREOUS_CLAY_DUST.toStack()),
                CEUtils.TEN_MICRO_CE * 4, 20, 0);
        create(recipeOutput, "industrial_clay_dust",
                ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack(2),
                List.of(
                        ClayiumItems.ENERGIZED_CLAY_DUST.toStack(12),
                        ClayiumItems.CLAY_DUST.toStack(8),
                        ClayiumItems.DENSE_CLAY_DUST.toStack(8),
                        ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack()),
                CEUtils.TEN_MICRO_CE * 4, 20, 0);
        // Tier 4
        create(recipeOutput, "advanced_industrial_clay_dust",
                ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(2),
                List.of(
                        ClayiumItems.ENERGIZED_CLAY_DUST.toStack(64),
                        ClayiumItems.CLAY_DUST.toStack(64),
                        ClayiumItems.DENSE_CLAY_DUST.toStack(64),
                        ClayiumItems.INDUSTRIAL_CLAY_DUST.toStack(12)),
                CEUtils.milliCeToLong(100), 12, 4);
        create(recipeOutput, "gravel_1",
                new ItemStack(Blocks.GRAVEL), ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(),
                CEUtils.TEN_MICRO_CE, 2, 4);
        create(recipeOutput, "gravel_4",
                new ItemStack(Blocks.GRAVEL, 4), new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem(), 4),
                CEUtils.TEN_MICRO_CE * 2, 2, 4);
        // Tier 5
        create(recipeOutput, "gravel_16",
                new ItemStack(Blocks.GRAVEL, 16), new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem(), 16),
                CEUtils.TEN_MICRO_CE * 4, 2, 5);
        // Tier 6
        create(recipeOutput, "gravel_64",
                new ItemStack(Blocks.GRAVEL, 64), new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem(), 64),
                CEUtils.TEN_MICRO_CE * 8, 2, 6);
        create(recipeOutput, "clay_log",
                ClayiumBlocks.CLAY_LOG.get().asItem().getDefaultInstance(),
                List.of(
                        ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_DUST.toStack(16),
                        ClayiumItems.MANGANESE_DUST.toStack(5),
                        ClayiumItems.LITHIUM_DUST.toStack(3),
                        ClayiumItems.ZIRCONIUM_DUST.toStack()),
                CEUtils.milliCeToLong(100), 400, 6);

        // spotless:on
    }
}
