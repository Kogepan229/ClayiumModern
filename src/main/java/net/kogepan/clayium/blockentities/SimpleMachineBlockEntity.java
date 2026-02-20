package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.AbstractRecipeLogic;
import net.kogepan.clayium.blockentities.trait.EnergyRecipeLogic;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class SimpleMachineBlockEntity extends WorkableClayContainerBlockEntity {

    public SimpleMachineBlockEntity(@NotNull BlockEntityType<?> type, @NotNull BlockPos pos,
                                    @NotNull BlockState blockState,
                                    @NotNull SimpleMachineRecipeType<MachineRecipe> recipeType) {
        this(type, pos, blockState, recipeType,
                (be) -> new EnergyRecipeLogic(be, recipeType, be.energyHolder));
    }

    /**
     * Protected constructor for subclasses that supply a custom recipe logic factory
     * (e.g. SmelterBlockEntity uses SmelterRecipeLogic).
     */
    protected SimpleMachineBlockEntity(@NotNull BlockEntityType<?> type, @NotNull BlockPos pos,
                                       @NotNull BlockState blockState,
                                       @NotNull SimpleMachineRecipeType<MachineRecipe> recipeType,
                                       @NotNull Function<WorkableClayContainerBlockEntity, AbstractRecipeLogic> recipeLogicFactory) {
        super(type, pos, blockState,
                getValidInputModes(recipeType.getMaxInputSize()),
                getValidOutputModes(recipeType.getMaxOutputSize()),
                recipeType.getMaxInputSize(),
                recipeType.getMaxOutputSize(),
                recipeLogicFactory,
                recipeType);
    }
}
