package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ClayiumTags {

    public static final TagKey<Block> CLAY_TOOLS_BONUS_BLOCK = blockTag("clay_tools_bonus_block");
    public static final TagKey<Item> ITEM_FILTER_REMOVERS = itemTag("item_filter_removers");
    public static final TagKey<Item> LITHIUM_INGOTS = ItemTags.create(
            Identifier.fromNamespaceAndPath("c", "ingots/lithium"));

    private ClayiumTags() {}

    private static TagKey<Block> blockTag(String path) {
        return BlockTags.create(Clayium.id(path));
    }

    private static TagKey<Item> itemTag(String path) {
        return ItemTags.create(Clayium.id(path));
    }
}
