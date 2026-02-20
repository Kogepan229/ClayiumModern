package net.kogepan.clayium.laser;

import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.recipes.LaserIrradiationRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

/**
 * Block irradiation handler that looks up laser irradiation recipes and performs block
 * transformation when the target block matches a recipe and accumulated total energy meets
 * the recipe's threshold (Original-style: total only, no per-tick energy range).
 */
public final class LaserIrradiationRecipeHandler implements LaserBlockIrradiationHandler {

    private static final int MIN_IRRADIATION_TICKS = 10;

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

        for (var holder : level.getRecipeManager()
                .getAllRecipesFor(ClayiumRecipeTypes.LASER_IRRADIATION_RECIPE_TYPE.get())) {
            LaserIrradiationRecipe recipe = holder.value();
            if (!recipe.matches(targetState)) {
                continue;
            }
            if (totalEnergyIrradiated < recipe.requiredEnergy()) {
                continue;
            }

            level.destroyBlock(targetPos, false);
            level.setBlock(targetPos, recipe.outputState(), Block.UPDATE_ALL);
            onBlockConverted.run();
            return;
        }
    }
}
