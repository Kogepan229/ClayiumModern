package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blocks.QuartzCrucibleBlock;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class QuartzCrucibleBlockEntity extends BlockEntity {

    private int ingotQuantity = 0;
    @Getter
    private int progress = 0;

    public QuartzCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.QUARTZ_CRUCIBLE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            QuartzCrucibleBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }
        if (blockEntity.ingotQuantity <= 0) {
            return;
        }
        if (blockEntity.progress >= QuartzCrucibleBlock.TICKS_PER_INGOT * blockEntity.ingotQuantity) {
            return;
        }
        blockEntity.progress++;
        blockEntity.setChanged();
    }

    public void addIngot() {
        ingotQuantity++;
        setChanged();
    }

    public void reset() {
        ingotQuantity = 0;
        progress = 0;
        setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("ingotQuantity", ingotQuantity);
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        ingotQuantity = tag.getInt("ingotQuantity");
        progress = tag.getInt("progress");
    }
}
