package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayBufferBlock;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.blocks.ClayOre;
import net.kogepan.clayium.blocks.ClayWorkTableBlock;
import net.kogepan.clayium.blocks.QuartzCrucibleBlock;
import net.kogepan.clayium.blocks.machine.AutoClayCondenserBlock;
import net.kogepan.clayium.blocks.machine.BendingMachineBlock;
import net.kogepan.clayium.blocks.machine.ClayCondenserBlock;
import net.kogepan.clayium.blocks.machine.ClayLaserBlock;
import net.kogepan.clayium.blocks.machine.CobblestoneGeneratorBlock;
import net.kogepan.clayium.blocks.machine.CuttingMachineBlock;
import net.kogepan.clayium.blocks.machine.GrinderBlock;
import net.kogepan.clayium.blocks.machine.LaserReflectorBlock;
import net.kogepan.clayium.blocks.machine.LatheBlock;
import net.kogepan.clayium.blocks.machine.MillingMachineBlock;
import net.kogepan.clayium.blocks.machine.PipeDrawingMachineBlock;
import net.kogepan.clayium.blocks.machine.WaterwheelBlock;
import net.kogepan.clayium.blocks.machine.WireDrawingMachineBlock;
import net.kogepan.clayium.items.blockitem.LaserReflectorBlockItem;
import net.kogepan.clayium.items.blockitem.QuartzCrucibleBlockItem;
import net.kogepan.clayium.items.blockitem.TieredBlockItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.function.Function;
import java.util.function.Supplier;

public class ClayiumBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Clayium.MODID);

    private static <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> sup) {
        DeferredBlock<B> block = BLOCKS.register(name, sup);
        ClayiumItems.ITEMS.registerSimpleBlockItem(block);

        return block;
    }

    private static <B extends Block> DeferredBlock<B> registerTiered(String name, final int tier,
                                                                     Function<Integer, ? extends B> func) {
        assert tier >= 0;
        name = name + "_" + tier;

        DeferredBlock<B> block = BLOCKS.register(name, () -> func.apply(tier));
        ClayiumItems.ITEMS.register(name, (r) -> new TieredBlockItem(block.get(), new Item.Properties(), tier));

        return block;
    }

    private static void registerCompressedClay(int tier) {
        DeferredBlock<Block> block = registerTiered("compressed_clay", tier,
                (t) -> new Block(BlockBehaviour.Properties.of().destroyTime(1)
                        .explosionResistance(1).sound(SoundType.GRAVEL).requiresCorrectToolForDrops()));
        COMPRESSED_CLAYS.put(tier, block);
    }

    public static final DeferredBlock<ClayOre> CLAY_ORE = register("clay_ore", ClayOre::new);
    public static final DeferredBlock<Block> DENSE_CLAY_ORE = register("dense_clay_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .destroyTime(3.0f)
                    .explosionResistance(5.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> LARGE_DENSE_CLAY_ORE = register("large_dense_clay_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .destroyTime(3.0f)
                    .explosionResistance(5.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final Int2ObjectMap<DeferredBlock<Block>> COMPRESSED_CLAYS = new Int2ObjectOpenHashMap<>();
    static {
        for (int i = 0; i <= 12; i++) {
            registerCompressedClay(i);
        }
    }

    public static final DeferredBlock<ClayWorkTableBlock> CLAY_WORK_TABLE = register("clay_work_table",
            ClayWorkTableBlock::new);

    public static final DeferredBlock<QuartzCrucibleBlock> QUARTZ_CRUCIBLE = BLOCKS.register("quartz_crucible",
            QuartzCrucibleBlock::new);
    static {
        ClayiumItems.ITEMS.register("quartz_crucible",
                () -> new QuartzCrucibleBlockItem(QUARTZ_CRUCIBLE.get(), new Item.Properties()));
    }

    public static final DeferredBlock<Block> RAW_CLAY_MACHINE_HULL = BLOCKS.register("raw_clay_machine_hull",
            () -> new Block(BlockBehaviour.Properties.of()
                    .destroyTime(2.0f)
                    .explosionResistance(6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));
    static {
        ClayiumItems.ITEMS.register("raw_clay_machine_hull",
                () -> new TieredBlockItem(RAW_CLAY_MACHINE_HULL.get(), new Item.Properties(), 1));
    }

    public static final Int2ObjectMap<DeferredBlock<Block>> MACHINE_HULLS;
    static {
        Int2ObjectMap<DeferredBlock<Block>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 13; i++) {
            map.put(i, registerTiered("machine_hull", i,
                    (t) -> new Block(BlockBehaviour.Properties.of()
                            .destroyTime(3.0f)
                            .explosionResistance(10.0f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops())));
        }
        MACHINE_HULLS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final DeferredBlock<Block> AZ91D_ALLOY_HULL = BLOCKS.register("az91d_alloy_hull",
            () -> new Block(BlockBehaviour.Properties.of()
                    .destroyTime(3.0f)
                    .explosionResistance(10.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ZK60A_ALLOY_HULL = BLOCKS.register("zk60a_alloy_hull",
            () -> new Block(BlockBehaviour.Properties.of()
                    .destroyTime(3.0f)
                    .explosionResistance(10.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
    static {
        ClayiumItems.ITEMS.register("az91d_alloy_hull",
                () -> new TieredBlockItem(AZ91D_ALLOY_HULL.get(), new Item.Properties(), 6));
        ClayiumItems.ITEMS.register("zk60a_alloy_hull",
                () -> new TieredBlockItem(ZK60A_ALLOY_HULL.get(), new Item.Properties(), 6));
    }

    public static final Int2ObjectMap<DeferredBlock<ClayBufferBlock>> CLAY_BUFFERS;
    static {
        Int2ObjectMap<DeferredBlock<ClayBufferBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 4; i <= 13; i++) {
            map.put(i, registerTiered("clay_buffer", i, ClayBufferBlock::new));
        }
        CLAY_BUFFERS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> BENDING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 9; i++) {
            if (i == 8) continue;
            map.put(i, registerTiered("bending_machine", i, BendingMachineBlock::new));
        }
        BENDING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> WIRE_DRAWING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 4; i++) {
            map.put(i, registerTiered("wire_drawing_machine", i, WireDrawingMachineBlock::new));
        }
        WIRE_DRAWING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> PIPE_DRAWING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 4; i++) {
            map.put(i, registerTiered("pipe_drawing_machine", i, PipeDrawingMachineBlock::new));
        }
        PIPE_DRAWING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> CUTTING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 4; i++) {
            map.put(i, registerTiered("cutting_machine", i, CuttingMachineBlock::new));
        }
        CUTTING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> LATHE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 4; i++) {
            map.put(i, registerTiered("lathe", i, LatheBlock::new));
        }
        LATHE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> MILLING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i : new int[] { 1, 3, 4 }) {
            map.put(i, registerTiered("milling_machine", i, MillingMachineBlock::new));
        }
        MILLING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> WATERWHEELS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 2; i++) {
            map.put(i, registerTiered("waterwheel", i, WaterwheelBlock::new));
        }
        WATERWHEELS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> COBBLESTONE_GENERATORS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 7; i++) {
            map.put(i, registerTiered("cobblestone_generator", i, CobblestoneGeneratorBlock::new));
        }
        COBBLESTONE_GENERATORS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> CLAY_CONDENSER_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i : new int[] { 2, 3, 4, 5, 10 }) {
            map.put(i, registerTiered("clay_condenser", i, ClayCondenserBlock::new));
        }
        CLAY_CONDENSER_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> GRINDER_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i : new int[] { 2, 3, 4, 5, 6, 10 }) {
            map.put(i, registerTiered("grinder", i, GrinderBlock::new));
        }
        GRINDER_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> AUTO_CLAY_CONDENSER_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i : new int[] { 5, 7 }) {
            map.put(i, registerTiered("auto_clay_condenser", i, AutoClayCondenserBlock::new));
        }
        AUTO_CLAY_CONDENSER_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> CLAY_LASER_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 7; i <= 10; i++) {
            map.put(i, registerTiered("clay_laser", i, ClayLaserBlock::new));
        }
        CLAY_LASER_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final DeferredBlock<LaserReflectorBlock> LASER_REFLECTOR = BLOCKS.register("laser_reflector",
            LaserReflectorBlock::new);
    static {
        ClayiumItems.ITEMS.register("laser_reflector",
                () -> new LaserReflectorBlockItem(LASER_REFLECTOR.get(), new Item.Properties()));
    }
}
