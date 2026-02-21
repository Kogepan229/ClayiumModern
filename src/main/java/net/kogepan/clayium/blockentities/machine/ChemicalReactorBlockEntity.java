package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class ChemicalReactorBlockEntity extends SimpleMachineBlockEntity {

    public ChemicalReactorBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CHEMICAL_REACTOR_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.CHEMICAL_REACTOR_RECIPE_TYPE.get());
    }
}
