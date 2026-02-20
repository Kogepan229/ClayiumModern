package net.kogepan.clayium.datagen.recipes;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.recipes.recipes.LaserIrradiationRecipe;
import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

/**
 * Data generation for laser irradiation recipes (block conversion when irradiated by Clay Laser).
 * Ore required_energy uses Original formula: (hardness + 1.0) * 100.
 */
public class LaserIrradiationRecipeProvider {

    /** Stone ore hardness in 1.21 → (3 + 1) * 100 = 400 */
    private static final long REQUIRED_ENERGY_STONE_ORE = 400L;
    /** Deepslate ore hardness in 1.21 → (4.5 + 1) * 100 = 550 */
    private static final long REQUIRED_ENERGY_DEEPSLATE_ORE = 550L;
    /** Original: total >= 300000 converts vanilla sapling to Clay Tree Sapling */
    private static final long REQUIRED_ENERGY_CLAY_SAPLING = 300_000L;

    /**
     * Registers laser irradiation recipes. Ore -> block and sapling cycle (Original-style:
     * conversion when accumulated total >= required_energy per recipe, no per-tick energy range).
     */
    public static void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        // Ore -> compressed block (Original: threshold = (hardness + 1.0) * 100 per block)
        addOre(recipeOutput, "coal_ore", Blocks.COAL_ORE, Blocks.COAL_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_coal_ore", Blocks.DEEPSLATE_COAL_ORE, Blocks.COAL_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "iron_ore", Blocks.IRON_ORE, Blocks.IRON_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_iron_ore", Blocks.DEEPSLATE_IRON_ORE, Blocks.IRON_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "gold_ore", Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_gold_ore", Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "diamond_ore", Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_diamond_ore", Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.DIAMOND_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "redstone_ore", Blocks.REDSTONE_ORE, Blocks.REDSTONE_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_redstone_ore", Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.REDSTONE_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "lapis_ore", Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_lapis_ore", Blocks.DEEPSLATE_LAPIS_ORE, Blocks.LAPIS_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "emerald_ore", Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_emerald_ore", Blocks.DEEPSLATE_EMERALD_ORE, Blocks.EMERALD_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);
        addOre(recipeOutput, "copper_ore", Blocks.COPPER_ORE, Blocks.COPPER_BLOCK, REQUIRED_ENERGY_STONE_ORE);
        addOre(recipeOutput, "deepslate_copper_ore", Blocks.DEEPSLATE_COPPER_ORE, Blocks.COPPER_BLOCK,
                REQUIRED_ENERGY_DEEPSLATE_ORE);

        // Sapling cycle (Original: 300 <= total < 1000; we use required_energy=300 as threshold)
        addSapling(recipeOutput, "oak_sapling", Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING);
        addSapling(recipeOutput, "spruce_sapling", Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING);
        addSapling(recipeOutput, "birch_sapling", Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING);
        addSapling(recipeOutput, "jungle_sapling", Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING);
        addSapling(recipeOutput, "acacia_sapling", Blocks.ACACIA_SAPLING, Blocks.CHERRY_SAPLING);
        addSapling(recipeOutput, "cherry_sapling", Blocks.CHERRY_SAPLING, Blocks.DARK_OAK_SAPLING);
        addSapling(recipeOutput, "dark_oak_sapling", Blocks.DARK_OAK_SAPLING, Blocks.MANGROVE_PROPAGULE);
        addSapling(recipeOutput, "mangrove_propagule", Blocks.MANGROVE_PROPAGULE, Blocks.OAK_SAPLING);

        // Original: total >= 300000 → Clay Tree Sapling (any vanilla sapling can convert)
        addClaySapling(recipeOutput, "clay_sapling_from_oak_sapling", Blocks.OAK_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_spruce_sapling", Blocks.SPRUCE_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_birch_sapling", Blocks.BIRCH_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_jungle_sapling", Blocks.JUNGLE_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_acacia_sapling", Blocks.ACACIA_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_cherry_sapling", Blocks.CHERRY_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_dark_oak_sapling", Blocks.DARK_OAK_SAPLING);
        addClaySapling(recipeOutput, "clay_sapling_from_mangrove_propagule", Blocks.MANGROVE_PROPAGULE);
    }

    private static void addOre(RecipeOutput output, String name, Block input, Block outputBlock, long requiredEnergy) {
        output.accept(
                Clayium.id("laser_irradiation/" + name),
                new LaserIrradiationRecipe(input, outputBlock, requiredEnergy),
                null);
    }

    private static void addSapling(RecipeOutput output, String name, Block input, Block outputBlock) {
        // Original: 300 <= total for sapling cycle
        output.accept(
                Clayium.id("laser_irradiation/" + name),
                new LaserIrradiationRecipe(input, outputBlock, 300L),
                null);
    }

    private static void addClaySapling(RecipeOutput output, String name, Block input) {
        output.accept(
                Clayium.id("laser_irradiation/" + name),
                new LaserIrradiationRecipe(input, ClayiumBlocks.CLAY_SAPLING.get(), REQUIRED_ENERGY_CLAY_SAPLING),
                null);
    }
}
