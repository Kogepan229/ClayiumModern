package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.blockentities.trait.BaseMultiplierRecipeLogic;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class DecomposerBlockEntity extends SimpleMachineBlockEntity {

    public DecomposerBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.DECOMPOSER_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.DECOMPOSER_RECIPE_TYPE.get(),
                (be) -> new BaseMultiplierRecipeLogic(be, ClayiumRecipeTypes.DECOMPOSER_RECIPE_TYPE.get(),
                        be.getEnergyHolder()));
    }
}
