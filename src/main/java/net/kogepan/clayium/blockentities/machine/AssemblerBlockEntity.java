package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.SimpleMachineBlockEntity;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public class AssemblerBlockEntity extends SimpleMachineBlockEntity {

    public AssemblerBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.ASSEMBLER_BLOCK_ENTITY.get(), pos, blockState,
                ClayiumRecipeTypes.ASSEMBLER_RECIPE_TYPE.get());
    }
}
