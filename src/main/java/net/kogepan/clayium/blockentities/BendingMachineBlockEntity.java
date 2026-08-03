package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class BendingMachineBlockEntity extends SimpleMachineBlockEntity {

    public BendingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(
                ClayiumBlockEntityTypes.BENDING_MACHINE_BLOCK_ENTITY.get(),
                pos,
                state,
                ClayiumRecipeTypes.BENDING_MACHINE_RECIPE_TYPE.get());
    }
}
