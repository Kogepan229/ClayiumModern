package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayBufferBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.stream.Collectors;

public final class ClayiumBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, Clayium.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayBufferBlockEntity>> CLAY_BUFFER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register(
                    "clay_buffer_block_entity",
                    () -> new BlockEntityType<>(
                            ClayBufferBlockEntity::new,
                            ClayiumBlocks.CLAY_BUFFERS.values().stream()
                                    .map(holder -> (Block) holder.get())
                                    .collect(Collectors.toUnmodifiableSet())));

    private ClayiumBlockEntityTypes() {}
}
