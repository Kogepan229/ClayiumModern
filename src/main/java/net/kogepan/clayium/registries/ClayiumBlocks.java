package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayBufferBlock;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.blocks.ClayOre;
import net.kogepan.clayium.blocks.ClayWorkTableBlock;
import net.kogepan.clayium.blocks.machine.BendingMachineBlock;
import net.kogepan.clayium.blocks.machine.WaterwheelBlock;
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

    private static DeferredBlock<Block> registerCompressedClay(int tier) {
        DeferredBlock<Block> block = registerTiered("compressed_clay", tier,
                (t) -> new Block(BlockBehaviour.Properties.of().destroyTime(1)
                        .explosionResistance(1).sound(SoundType.GRAVEL).requiresCorrectToolForDrops()));
        COMPRESSED_CLAYS.put(tier, block);
        return block;
    }

    public static final DeferredBlock<ClayOre> CLAY_ORE = register("clay_ore", ClayOre::new);

    public static final Int2ObjectMap<DeferredBlock<Block>> COMPRESSED_CLAYS = new Int2ObjectOpenHashMap<>();
    static {
        for (int i = 0; i <= 12; i++) {
            registerCompressedClay(i);
        }
    }

    public static final DeferredBlock<ClayWorkTableBlock> CLAY_WORK_TABLE = register("clay_work_table",
            ClayWorkTableBlock::new);

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> BENDING_MACHINE_BLOCKS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 9; i++) {
            if (i == 8) continue;
            map.put(i, registerTiered("bending_machine", i, BendingMachineBlock::new));
        }
        BENDING_MACHINE_BLOCKS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayBufferBlock>> CLAY_BUFFERS;
    static {
        Int2ObjectMap<DeferredBlock<ClayBufferBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 4; i <= 13; i++) {
            map.put(i, registerTiered("clay_buffer", i, ClayBufferBlock::new));
        }
        CLAY_BUFFERS = Int2ObjectMaps.unmodifiable(map);
    }

    public static final Int2ObjectMap<DeferredBlock<ClayContainerBlock>> WATERWHEELS;
    static {
        Int2ObjectMap<DeferredBlock<ClayContainerBlock>> map = new Int2ObjectOpenHashMap<>();
        for (int i = 1; i <= 2; i++) {
            map.put(i, registerTiered("waterwheel", i, WaterwheelBlock::new));
        }
        WATERWHEELS = Int2ObjectMaps.unmodifiable(map);
    }
}
