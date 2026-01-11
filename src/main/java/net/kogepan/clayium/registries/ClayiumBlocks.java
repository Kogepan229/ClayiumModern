package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayOre;
import net.kogepan.clayium.blocks.ClayWorkTableBlock;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

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
}
