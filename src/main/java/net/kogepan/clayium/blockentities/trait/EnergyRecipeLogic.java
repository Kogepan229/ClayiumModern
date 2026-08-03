package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

public final class EnergyRecipeLogic extends AbstractRecipeLogic {

    public EnergyRecipeLogic(
                             WorkableClayContainerBlockEntity blockEntity,
                             SimpleMachineRecipeType<MachineRecipe> recipeType) {
        super(blockEntity, recipeType);
    }
}
