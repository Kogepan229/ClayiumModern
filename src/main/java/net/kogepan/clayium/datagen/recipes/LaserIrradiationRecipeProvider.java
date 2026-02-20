package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.LaserIrradiationRecipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

/**
 * Data generation for laser irradiation recipes (block conversion when irradiated by Clay Laser).
 */
public class LaserIrradiationRecipeProvider {

    /**
     * Registers laser irradiation recipes. Ore -> block conversions (ClayiumOriginal-style, data-driven)
     * and sapling cycle (Unofficial-style).
     */
    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // Ore -> compressed block (Original-style, data-driven)
        addOre(recipeOutput, "coal_ore", Blocks.COAL_ORE, Blocks.COAL_BLOCK);
        addOre(recipeOutput, "deepslate_coal_ore", Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_BLOCK);
        addOre(recipeOutput, "iron_ore", Blocks.IRON_ORE, Blocks.IRON_BLOCK);
        addOre(recipeOutput, "deepslate_iron_ore", Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_BLOCK);
        addOre(recipeOutput, "gold_ore", Blocks.GOLD_ORE, Blocks.GOLD_BLOCK);
        addOre(recipeOutput, "deepslate_gold_ore", Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_BLOCK);
        addOre(recipeOutput, "diamond_ore", Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK);
        addOre(recipeOutput, "deepslate_diamond_ore", Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DIAMOND_BLOCK);
        addOre(recipeOutput, "redstone_ore", Blocks.REDSTONE_ORE, Blocks.REDSTONE_BLOCK);
        addOre(recipeOutput, "deepslate_redstone_ore", Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.REDSTONE_BLOCK);
        addOre(recipeOutput, "lapis_ore", Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK);
        addOre(recipeOutput, "deepslate_lapis_ore", Blocks.DEEPSLATE_LAPIS_ORE, Blocks.LAPIS_BLOCK);
        addOre(recipeOutput, "emerald_ore", Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK);
        addOre(recipeOutput, "deepslate_emerald_ore", Blocks.DEEPSLATE_EMERALD_ORE, Blocks.EMERALD_BLOCK);
        addOre(recipeOutput, "copper_ore", Blocks.COPPER_ORE, Blocks.COPPER_BLOCK);
        addOre(recipeOutput, "deepslate_copper_ore", Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_BLOCK);

        // Sapling cycle (Unofficial-style: energy in range 300..1000 to cycle)
        addSaplingCycle(recipeOutput, "oak_sapling", Blocks.OAK_SAPLING);
        addSaplingCycle(recipeOutput, "spruce_sapling", Blocks.SPRUCE_SAPLING);
        addSaplingCycle(recipeOutput, "birch_sapling", Blocks.BIRCH_SAPLING);
        addSaplingCycle(recipeOutput, "jungle_sapling", Blocks.JUNGLE_SAPLING);
        addSaplingCycle(recipeOutput, "acacia_sapling", Blocks.ACACIA_SAPLING);
        addSaplingCycle(recipeOutput, "cherry_sapling", Blocks.CHERRY_SAPLING);
        addSaplingCycle(recipeOutput, "dark_oak_sapling", Blocks.DARK_OAK_SAPLING);
        addSaplingCycle(recipeOutput, "mangrove_propagule", Blocks.MANGROVE_PROPAGULE);
    }

    private static void addOre(RecipeOutput output, String name, Block input, Block outputBlock) {
        // Any laser tier: energy per tick in [0, 1e4], require 100 total, transformation after 10+ ticks
        output.accept(
                Clayium.id("laser_irradiation/" + name),
                new LaserIrradiationRecipe(input, outputBlock, 0L, 10_000L, 100L),
                null);
    }

    private static void addSaplingCycle(RecipeOutput output, String name, Block sapling) {
        // Sapling -> same sapling. Original: 300 <= total < 1000 for cycle; we use required_energy=300
        // and tick energy in [300, 1000] so mid-tier laser triggers. (We do not enforce total < 1000.)
        output.accept(
                Clayium.id("laser_irradiation/" + name + "_cycle"),
                new LaserIrradiationRecipe(sapling, sapling, 300L, 1000L, 300L),
                null);
    }
}
