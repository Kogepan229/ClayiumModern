package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class AlloySmelterBlockEntity extends SimpleMachineBlockEntity {

    public AlloySmelterBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.ALLOY_SMELTER_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.ALLOY_SMELTER_RECIPE_TYPE.get());
    }
}
