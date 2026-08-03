package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.EnergyRecipeLogic;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SimpleMachineBlockEntity extends WorkableClayContainerBlockEntity {

    protected SimpleMachineBlockEntity(
                                       BlockEntityType<?> type,
                                       BlockPos pos,
                                       BlockState blockState,
                                       SimpleMachineRecipeType<MachineRecipe> recipeType) {
        super(type, pos, blockState, recipeType, EnergyRecipeLogic::new);
    }

    protected SimpleMachineBlockEntity(
                                       BlockEntityType<?> type,
                                       BlockPos pos,
                                       BlockState blockState,
                                       SimpleMachineRecipeType<MachineRecipe> recipeType,
                                       AbstractRecipeLogicFactory recipeLogicFactory) {
        super(type, pos, blockState, recipeType, recipeLogicFactory);
    }
}
