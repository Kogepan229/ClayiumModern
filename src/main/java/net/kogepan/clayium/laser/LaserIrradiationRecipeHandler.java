package net.kogepan.clayium.laser;

import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.LaserIrradiationRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block irradiation handler that looks up laser irradiation recipes and performs block
 * transformation when the target block matches a recipe and accumulated total energy meets
 * the recipe's threshold (Original-style: total only, no per-tick energy range).
 * <p>
 * Cache is built with recipes grouped by input block and sorted by {@code required_energy}
 * (descending). At lookup we use the <em>first</em> recipe that has matching input and
 * sufficient energy (required_energy <= total), which is thus the highest-threshold match
 * (e.g. mangrove_propagule with 300000 total uses clay_sapling (300000) instead of the
 * sapling-cycle (300)).
 * <p>
 * Cache is rebuilt when the level's {@link RecipeManager} instance changes (e.g. datapack reload).
 */
public final class LaserIrradiationRecipeHandler implements LaserBlockIrradiationHandler {

    private static final int MIN_IRRADIATION_TICKS = 10;

    /** Input block -> recipes sorted by required_energy descending. Rebuilt when RecipeManager changes. */
    private static RecipeManager cachedManager;
    private static Map<Block, List<LaserIrradiationRecipe>> cacheByInput;

    @Override
    public void irradiate(
                          @NotNull Level level,
                          @NotNull BlockPos targetPos,
                          @NotNull BlockState targetState,
                          long tickEnergy,
                          long totalEnergyIrradiated,
                          int irradiationTicks,
                          @NotNull Runnable onBlockConverted) {
        if (level.isClientSide()) {
            return;
        }

        if (irradiationTicks <= MIN_IRRADIATION_TICKS) {
            return;
        }

        LaserIrradiationRecipe best = findBestRecipe(level.getRecipeManager(), targetState, totalEnergyIrradiated);
        if (best != null) {
            level.destroyBlock(targetPos, false);
            level.setBlock(targetPos, best.outputState(), Block.UPDATE_ALL);
            onBlockConverted.run();
        }
    }

    /**
     * Returns the first recipe that matches the input block and has required_energy <= totalEnergy.
     * Cache is sorted by required_energy descending, so this is the highest-threshold match.
     */
    @Nullable
    private static LaserIrradiationRecipe findBestRecipe(
                                                         @NotNull RecipeManager recipeManager,
                                                         @NotNull BlockState targetState,
                                                         long totalEnergy) {
        Map<Block, List<LaserIrradiationRecipe>> byInput = getOrBuildCache(recipeManager);
        List<LaserIrradiationRecipe> recipes = byInput.get(targetState.getBlock());
        if (recipes == null || recipes.isEmpty()) {
            return null;
        }
        for (LaserIrradiationRecipe recipe : recipes) {
            if (recipe.requiredEnergy() <= totalEnergy) {
                return recipe;
            }
        }
        return null;
    }

    private static Map<Block, List<LaserIrradiationRecipe>> getOrBuildCache(@NotNull RecipeManager recipeManager) {
        if (recipeManager == cachedManager && cacheByInput != null) {
            return cacheByInput;
        }
        cacheByInput = buildCache(recipeManager);
        cachedManager = recipeManager;
        return cacheByInput;
    }

    private static Map<Block, List<LaserIrradiationRecipe>> buildCache(@NotNull RecipeManager recipeManager) {
        Map<Block, List<LaserIrradiationRecipe>> byInput = new ConcurrentHashMap<>();
        List<LaserIrradiationRecipe> all = recipeManager
                .getAllRecipesFor(ClayiumRecipeTypes.LASER_IRRADIATION_RECIPE_TYPE.get())
                .stream()
                .map(holder -> holder.value())
                .toList();
        for (LaserIrradiationRecipe recipe : all) {
            byInput
                    .computeIfAbsent(recipe.inputBlock(), b -> new ArrayList<>())
                    .add(recipe);
        }
        // Sort by required_energy (descending) so lookup can use the first with sufficient energy
        for (List<LaserIrradiationRecipe> list : byInput.values()) {
            list.sort(Comparator.comparingLong(LaserIrradiationRecipe::requiredEnergy).reversed());
        }
        return byInput;
    }
}
