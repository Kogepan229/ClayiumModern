package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayBufferBlock;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.blocks.ClayOre;
import net.kogepan.clayium.blocks.ClayWorkTableBlock;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.function.Supplier;

public class ClayiumBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Clayium.MODID);

    private static <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> sup) {
        DeferredBlock<B> block = BLOCKS.register(name, sup);
        ClayiumItems.ITEMS.registerSimpleBlockItem(block);

        return block;
    }

    public static final DeferredBlock<ClayOre> CLAY_ORE = register("clay_ore", ClayOre::new);

    public static final DeferredBlock<ClayWorkTableBlock> CLAY_WORK_TABLE = register("clay_work_table",
            ClayWorkTableBlock::new);

    public static final DeferredBlock<ClayContainerBlock> TEST_CLAY_CONTAINER = register("test_clay_container",
            () -> new ClayContainerBlock(1));

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> BENDING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 9; i++) {
            int finalI = i;
            map.put(i, register("bending_machine_" + i, () -> new ClayContainerBlock(finalI)));
        }
        BENDING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayBufferBlock>> CLAY_BUFFERS;
    static {
        Int2ObjectMap<DeferredBlock<ClayBufferBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 4; i <= 13; i++) {
            int finalI = i;
            map.put(i, register("clay_buffer_" + i, () -> new ClayBufferBlock(finalI)));
        }
        CLAY_BUFFERS = Int2ObjectMaps.unmodifiable(map);
    }
}
