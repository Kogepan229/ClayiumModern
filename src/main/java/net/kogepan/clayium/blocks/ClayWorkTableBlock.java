package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.ClayWorkTableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClayWorkTableBlock extends Block implements EntityBlock {

    public ClayWorkTableBlock() {
        super(BlockBehaviour.Properties.of());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new ClayWorkTableBlockEntity(blockPos, blockState);
    }
}
