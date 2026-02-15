package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class CuttingMachineBlockEntity extends SimpleMachineBlockEntity {

    public CuttingMachineBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CUTTING_MACHINE_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.CUTTING_MACHINE_RECIPE_TYPE.get());
    }
}
