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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClayCondenserRecipeProvider {

    private static void create(RecipeOutput output, String name, ItemStack ingredient, ItemStack result, long energy,
                               long duration, int tier) {
        create(output, name, ItemIngredientStack.of(ingredient), result, energy, duration, tier);
    }

    private static void create(RecipeOutput output, String name, ItemIngredientStack ingredient, ItemStack result,
                               long energy, long duration, int tier) {
        MachineRecipe machineRecipe = new MachineRecipe(ClayiumRecipeTypes.CLAY_CONDENSER_RECIPE_TYPE.get(),
                List.of(ingredient), List.of(result), duration, energy, tier);
        output.accept(Clayium.id("clay_condenser/" + name), machineRecipe, null);
    }

    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // spotless:off

        // Tier 0 - Shard to block (4 shards -> 1 block)
        create(recipeOutput, "compressed_clay_shard_to_block",
                ClayiumItems.COMPRESSED_CLAY_SHARD.toStack(4),
                ClayiumBlocks.COMPRESSED_CLAYS.get(1).asItem().getDefaultInstance(),
                CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "industrial_clay_shard_to_block",
                ClayiumItems.INDUSTRIAL_CLAY_SHARD.toStack(4),
                ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem().getDefaultInstance(),
                CEUtils.TEN_MICRO_CE, 6, 0);
        create(recipeOutput, "advanced_industrial_clay_shard_to_block",
                ClayiumItems.ADVANCED_INDUSTRIAL_CLAY_SHARD.toStack(4),
                ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem().getDefaultInstance(),
                CEUtils.TEN_MICRO_CE, 9, 0);
        create(recipeOutput, "clay_dust_to_block", ClayiumItems.CLAY_DUST.toStack(),
                Blocks.CLAY.asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 3, 0);
        create(recipeOutput, "dense_clay_dust_to_block", ClayiumItems.DENSE_CLAY_DUST.toStack(),
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 6, 0);

        // Tier 0 - Block condensation
        create(recipeOutput, "clay_to_compressed_0", new ItemStack(Blocks.CLAY, 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "compressed_0_to_1",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(0).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(1).asItem().getDefaultInstance(), CEUtils.TEN_MICRO_CE, 4, 0);
        create(recipeOutput, "compressed_1_to_2",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(1).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem().getDefaultInstance(),
                CEUtils.TEN_MICRO_CE * 10, 4, 0);
        create(recipeOutput, "compressed_2_to_3",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(2).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem().getDefaultInstance(),
                CEUtils.ONE_MILLI_CE, 4, 0);

        // Tier 4
        create(recipeOutput, "compressed_3_to_4",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(3).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(4).asItem().getDefaultInstance(),
                CEUtils.ONE_MILLI_CE, 16, 4);
        create(recipeOutput, "compressed_4_to_5",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(4).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(5).asItem().getDefaultInstance(),
                CEUtils.ONE_MILLI_CE * 10, 16, 4);
        create(recipeOutput, "compressed_5_to_6",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(5).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(6).asItem().getDefaultInstance(),
                CEUtils.ONE_MILLI_CE * 100, 13, 4);

        // Tier 5
        create(recipeOutput, "compressed_6_to_7",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(6).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(7).asItem().getDefaultInstance(),
                CEUtils.ceToLong(1), 10, 5);
        create(recipeOutput, "compressed_7_to_8",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(7).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(8).asItem().getDefaultInstance(),
                CEUtils.ceToLong(10), 8, 5);
        create(recipeOutput, "compressed_8_to_9",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(8).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(9).asItem().getDefaultInstance(),
                CEUtils.ceToLong(100), 6, 5);
        create(recipeOutput, "compressed_9_to_10",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(9).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(10).asItem().getDefaultInstance(),
                CEUtils.ceToLong(1000), 4, 5);
        create(recipeOutput, "compressed_10_to_11",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(10).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(11).asItem().getDefaultInstance(),
                CEUtils.ceToLong(10000), 3, 5);
        create(recipeOutput, "compressed_11_to_12",
                new ItemStack(ClayiumBlocks.COMPRESSED_CLAYS.get(11).asItem(), 9),
                ClayiumBlocks.COMPRESSED_CLAYS.get(12).asItem().getDefaultInstance(),
                CEUtils.ceToLong(10000), 25, 5);

        // Tier 5
        create(recipeOutput, "coal_blocks_to_diamond", ItemIngredientStack.of(Ingredient.of(Tags.Items.STORAGE_BLOCKS_COAL), 8), new ItemStack(Items.DIAMOND), CEUtils.ONE_MILLI_CE * 10, 100, 5);

        // Tier 10
        create(recipeOutput, "antimatter_dust_to_matter", ClayiumItems.ANTIMATTER_DUST.toStack(), ClayiumItems.ANTIMATTER_MATTER.toStack(), CEUtils.ceToLong(25), 80, 10);
        create(recipeOutput, "pure_antimatter_dust_to_matter", ClayiumItems.PURE_ANTIMATTER_DUST.toStack(), ClayiumItems.PURE_ANTIMATTER_MATTER.toStack(), CEUtils.ceToLong(250), 80, 10);
        create(recipeOutput, "octuple_pure_antimatter_dust_to_matter", ClayiumItems.OCTUPLE_PURE_ANTIMATTER_DUST.toStack(), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_8.toStack(), CEUtils.ceToLong(25_000), 80, 10);
        create(recipeOutput, "pure_antimatter_to_compressed_1", ClayiumItems.PURE_ANTIMATTER_MATTER.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_1.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_1_to_2", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_1.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_2.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_2_to_3", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_2.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_3.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_3_to_4", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_3.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_4.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_4_to_5", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_4.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_5.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_5_to_6", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_5.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_6.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_6_to_7", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_6.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_7.toStack(), CEUtils.ceToLong(100), 6, 10);
        create(recipeOutput, "compressed_pure_antimatter_7_to_8", ClayiumItems.COMPRESSED_PURE_ANTIMATTER_7.toStack(9), ClayiumItems.COMPRESSED_PURE_ANTIMATTER_8.toStack(), CEUtils.ceToLong(100), 6, 10);

        // spotless:on
    }
}
