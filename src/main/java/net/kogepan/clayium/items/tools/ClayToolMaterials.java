package net.kogepan.clayium.items.tools;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

public final class ClayToolMaterials {

    public static final ToolMaterial CLAY_PICKAXE = new ToolMaterial(
            BlockTags.INCORRECT_FOR_STONE_TOOL,
            500,
            4.0F,
            0.0F,
            5,
            ItemTags.STONE_TOOL_MATERIALS);

    public static final ToolMaterial CLAY_SHOVEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            500,
            2.0F,
            0.0F,
            15,
            ItemTags.WOODEN_TOOL_MATERIALS);

    public static final ToolMaterial CLAY_STEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            10_000,
            48.0F,
            0.0F,
            10,
            ItemTags.DIAMOND_TOOL_MATERIALS);

    private ClayToolMaterials() {}
}
