package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayOre;
import net.kogepan.clayium.blocks.ClayWorkTableBlock;
import net.kogepan.clayium.blocks.TestClayContainerBlock;

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

    public static final DeferredBlock<TestClayContainerBlock> TEST_CLAY_CONTAINER = register("test_clay_container",
            TestClayContainerBlock::new);

    public static final Int2ObjectMap<DeferredBlock<TestClayContainerBlock>> BENDING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<TestClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 9; i++) {
            map.put(i, register("bending_machine_" + i, TestClayContainerBlock::new));
        }
        BENDING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }
}
