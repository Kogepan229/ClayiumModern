package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class MillingMachineBlockEntity extends SimpleMachineBlockEntity {

    public MillingMachineBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.MILLING_MACHINE_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.MILLING_MACHINE_RECIPE_TYPE.get());
    }
}
