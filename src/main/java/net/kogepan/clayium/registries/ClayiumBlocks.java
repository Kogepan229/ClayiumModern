package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blocks.ClayBufferBlock;

import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ClayiumBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Clayium.MODID);

    public static final Map<Integer, DeferredBlock<ClayBufferBlock>> CLAY_BUFFERS;

    static {
        Map<Integer, DeferredBlock<ClayBufferBlock>> buffers = new LinkedHashMap<>();
        for (int tier = 4; tier <= 13; tier++) {
            int registeredTier = tier;
            String name = "clay_buffer_" + tier;
            DeferredBlock<ClayBufferBlock> block = BLOCKS.registerBlock(
                    name,
                    properties -> new ClayBufferBlock(registeredTier, properties),
                    properties -> properties.strength(3.0F, 10.0F).sound(SoundType.METAL));
            ClayiumItems.ITEMS.registerSimpleBlockItem(block);
            buffers.put(tier, block);
        }
        CLAY_BUFFERS = Collections.unmodifiableMap(buffers);
    }

    private ClayiumBlocks() {}
}
