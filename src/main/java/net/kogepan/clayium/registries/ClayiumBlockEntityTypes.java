package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayWorkTableBlockEntity;
import net.kogepan.clayium.blockentities.TestClayContainerBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ClayiumBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, Clayium.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayWorkTableBlockEntity>> CLAY_WORK_TABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register("clay_work_table_entity",
                    () -> BlockEntityType.Builder.of(ClayWorkTableBlockEntity::new, ClayiumBlocks.CLAY_WORK_TABLE.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TestClayContainerBlockEntity>> TEST_CLAY_CONTAINER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register("test_clay_container_block_entity",
                    () -> {
                        List<Block> blocks = new ArrayList<>(ClayiumBlocks.BENDING_MACHINE_BLOCKS.values().stream()
                                .map(DeferredHolder::get).toList());
                        blocks.add(ClayiumBlocks.TEST_CLAY_CONTAINER.get());
                        return BlockEntityType.Builder
                                .of(TestClayContainerBlockEntity::new,
                                        blocks.toArray(Block[]::new))
                                .build(null);
                    });
}
