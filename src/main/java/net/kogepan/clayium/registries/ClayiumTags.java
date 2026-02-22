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

    public static final TagKey<Block> CLAY_TOOLS_BONUS_BLOCK = blockTag("clayium:clay_tools_bonus_block");
    public static final TagKey<Item> ITEM_FILTER_REMOVERS = itemTag("clayium:item_filter_removers");
}
