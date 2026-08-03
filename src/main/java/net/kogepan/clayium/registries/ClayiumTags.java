package net.kogepan.clayium.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ClayiumTags {

    private static TagKey<Block> blockTag(String name) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.parse(name));
    }

    private static TagKey<Item> itemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(name));
    }

    public static final TagKey<Block> CLAY_ORES = blockTag("clayium:clay_ores");
    public static final TagKey<Block> CLAY_SHOVEL_MINEABLE_ORES = blockTag("clayium:clay_shovel_mineable_ores");
    public static final TagKey<Block> CLAY_SHOVEL_FAST_BLOCKS = blockTag("clayium:clay_shovel_fast_blocks");
    public static final TagKey<Item> ITEM_FILTER_REMOVERS = itemTag("clayium:item_filter_removers");
    public static final TagKey<Item> LITHIUM_INGOTS = itemTag("c:ingots/lithium");
}
