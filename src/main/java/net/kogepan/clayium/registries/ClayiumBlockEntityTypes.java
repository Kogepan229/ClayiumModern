package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayBufferBlockEntity;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.ClayWorkTableBlockEntity;
import net.kogepan.clayium.blockentities.QuartzCrucibleBlockEntity;
import net.kogepan.clayium.blockentities.machine.AutoClayCondenserBlockEntity;
import net.kogepan.clayium.blockentities.machine.BendingMachineBlockEntity;
import net.kogepan.clayium.blockentities.machine.ClayCondenserBlockEntity;
import net.kogepan.clayium.blockentities.machine.CobblestoneGeneratorBlockEntity;
import net.kogepan.clayium.blockentities.machine.CuttingMachineBlockEntity;
import net.kogepan.clayium.blockentities.machine.GrinderBlockEntity;
import net.kogepan.clayium.blockentities.machine.LatheBlockEntity;
import net.kogepan.clayium.blockentities.machine.MillingMachineBlockEntity;
import net.kogepan.clayium.blockentities.machine.PipeDrawingMachineBlockEntity;
import net.kogepan.clayium.blockentities.machine.WaterwheelBlockEntity;
import net.kogepan.clayium.blockentities.machine.WireDrawingMachineBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ClayiumBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
            .create(Registries.BLOCK_ENTITY_TYPE, Clayium.MODID);
    public static final Set<DeferredHolder<BlockEntityType<?>, ?>> CLAY_CONTAINER_BLOCK_ENTITY_TYPES = new HashSet<>();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayWorkTableBlockEntity>> CLAY_WORK_TABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register("clay_work_table_entity",
                    () -> BlockEntityType.Builder.of(ClayWorkTableBlockEntity::new, ClayiumBlocks.CLAY_WORK_TABLE.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuartzCrucibleBlockEntity>> QUARTZ_CRUCIBLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES
            .register("quartz_crucible_block_entity",
                    () -> BlockEntityType.Builder
                            .of(QuartzCrucibleBlockEntity::new, ClayiumBlocks.QUARTZ_CRUCIBLE.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BendingMachineBlockEntity>> BENDING_MACHINE_BLOCK_ENTITY = registerClayContainer(
            "bending_machine_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.BENDING_MACHINE_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(BendingMachineBlockEntity::new,
                                blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayBufferBlockEntity>> CLAY_BUFFER_BLOCK_ENTITY = registerClayContainer(
            "clay_buffer_block_entity",
            () -> BlockEntityType.Builder
                    .of(ClayBufferBlockEntity::new, ClayiumBlocks.CLAY_BUFFERS.values().stream()
                            .map(DeferredHolder::get).toArray(Block[]::new))
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterwheelBlockEntity>> WATERWHEEL_BLOCK_ENTITY = registerClayContainer(
            "waterwheel_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.WATERWHEELS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(WaterwheelBlockEntity::new,
                                blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CobblestoneGeneratorBlockEntity>> COBBLESTONE_GENERATOR_BLOCK_ENTITY = registerClayContainer(
            "cobblestone_generator_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.COBBLESTONE_GENERATORS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(CobblestoneGeneratorBlockEntity::new,
                                blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WireDrawingMachineBlockEntity>> WIRE_DRAWING_MACHINE_BLOCK_ENTITY = registerClayContainer(
            "wire_drawing_machine_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.WIRE_DRAWING_MACHINE_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(WireDrawingMachineBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeDrawingMachineBlockEntity>> PIPE_DRAWING_MACHINE_BLOCK_ENTITY = registerClayContainer(
            "pipe_drawing_machine_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.PIPE_DRAWING_MACHINE_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(PipeDrawingMachineBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CuttingMachineBlockEntity>> CUTTING_MACHINE_BLOCK_ENTITY = registerClayContainer(
            "cutting_machine_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.CUTTING_MACHINE_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(CuttingMachineBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LatheBlockEntity>> LATHE_BLOCK_ENTITY = registerClayContainer(
            "lathe_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.LATHE_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(LatheBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillingMachineBlockEntity>> MILLING_MACHINE_BLOCK_ENTITY = registerClayContainer(
            "milling_machine_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.MILLING_MACHINE_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(MillingMachineBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClayCondenserBlockEntity>> CLAY_CONDENSER_BLOCK_ENTITY = registerClayContainer(
            "clay_condenser_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.CLAY_CONDENSER_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(ClayCondenserBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoClayCondenserBlockEntity>> AUTO_CLAY_CONDENSER_BLOCK_ENTITY = registerClayContainer(
            "auto_clay_condenser_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.AUTO_CLAY_CONDENSER_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(AutoClayCondenserBlockEntity::new,
                                blocks.toArray(Block[]::new))
                        .build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrinderBlockEntity>> GRINDER_BLOCK_ENTITY = registerClayContainer(
            "grinder_block_entity",
            () -> {
                List<Block> blocks = new ArrayList<>(ClayiumBlocks.GRINDER_BLOCKS.values().stream()
                        .map(DeferredHolder::get).toList());
                return BlockEntityType.Builder
                        .of(GrinderBlockEntity::new, blocks.toArray(Block[]::new))
                        .build(null);
            });

    private static <
            T extends ClayContainerBlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerClayContainer(
                                                                                                                             String name,
                                                                                                                             Supplier<BlockEntityType<T>> supplier) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = BLOCK_ENTITY_TYPES.register(name, supplier);
        CLAY_CONTAINER_BLOCK_ENTITY_TYPES.add(holder);
        return holder;
    }
}
