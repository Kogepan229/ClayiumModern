package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

public class EnergyRecipeLogic extends AbstractRecipeLogic {

    @NotNull
    private final ClayEnergyHolder energyHolder;

    public EnergyRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity, RecipeType<MachineRecipe> recipeType,
                             @NotNull ClayEnergyHolder energyHolder) {
        super(blockEntity, recipeType);
        this.energyHolder = energyHolder;
    }

    @Override
    protected boolean drawEnergy(long amount, boolean simulate) {
        return this.energyHolder.drawEnergy(amount, simulate);
    }
}
