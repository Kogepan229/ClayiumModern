package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/** Recipe logic for a formed Clay Reactor with laser-driven processing speed. */
public class ClayReactorRecipeLogic extends MultiblockRecipeLogic {

    private final LongSupplier laserEnergySupplier;

    public ClayReactorRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity,
                                  @NotNull RecipeType<MachineRecipe> recipeType,
                                  @NotNull ClayEnergyHolder energyHolder,
                                  @NotNull BooleanSupplier structureFormedSupplier,
                                  @NotNull IntSupplier structureTierSupplier,
                                  @NotNull LongSupplier laserEnergySupplier) {
        super(blockEntity, recipeType, energyHolder, structureFormedSupplier, structureTierSupplier);
        this.laserEnergySupplier = laserEnergySupplier;
    }

    @Override
    protected long getProgressPerTick() {
        long laserEnergy = Math.max(0L, this.laserEnergySupplier.getAsLong());
        return laserEnergy == Long.MAX_VALUE ? Long.MAX_VALUE : laserEnergy + 1L;
    }

    @Override
    protected long getProgressPerTick(int virtualTick) {
        return virtualTick == 0 ? getProgressPerTick() : 1L;
    }

    @Override
    protected long getInitialProgress() {
        return 0L;
    }
}
