package net.kogepan.clayium.blocks.machine;

import net.kogepan.clayium.blockentities.machine.CreativeCESourceBlockEntity;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creative-only block that provides infinite highest-tier energized clay (Octuple Compressed
 * Energetic Clay) via its item handler. Used for testing and creative mode.
 * <p>
 * Based on ClayiumOriginal's blockCreativeCESource / TileCreativeEnergySource.
 */
public class CreativeCESourceBlock extends Block implements EntityBlock {

    public CreativeCESourceBlock() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f, 6.0f));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CreativeCESourceBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return type == ClayiumBlockEntityTypes.CREATIVE_CE_SOURCE_BLOCK_ENTITY.get() ? null : null;
    }
}
