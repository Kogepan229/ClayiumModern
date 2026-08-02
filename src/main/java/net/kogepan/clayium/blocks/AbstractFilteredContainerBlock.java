package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.AbstractFilteredContainerBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractFilteredContainerBlock extends ClayContainerBlock {

    protected AbstractFilteredContainerBlock() {
        super(6, BlockBehaviour.Properties.of()
                .destroyTime(2.0F)
                .explosionResistance(5.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
    }

    @Override
    @NotNull
    protected List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (!(blockEntity instanceof AbstractFilteredContainerBlockEntity container)) {
            return drops;
        }

        for (ItemStack drop : drops) {
            if (drop.is(this.asItem())) {
                container.saveToItem(drop, builder.getLevel().registryAccess());
            }
        }
        return drops;
    }
}
