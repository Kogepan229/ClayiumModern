package net.kogepan.clayium.items.tools;

import net.kogepan.clayium.registries.ClayiumTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ClayToolTiers implements Tier {

    CLAY_PICKAXE(BlockTags.INCORRECT_FOR_STONE_TOOL, 500, 4.0f, 5, Ingredient.of(Blocks.COBBLESTONE)),
    CLAY_STEEL_PICKAXE(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 8.0f * 6.0f, 10, Ingredient.EMPTY),
    CLAY_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 500, 2.0f, 15, Ingredient.of(ItemTags.PLANKS)) {

        @Override
        public Tool createToolProperties(TagKey<Block> blocks) {
            return new Tool(List.of(
                    // This exception must precede the wooden-tier restriction so clay shovels can harvest clay ores.
                    Tool.Rule.minesAndDrops(ClayiumTags.CLAY_SHOVEL_MINEABLE_ORES, 12.0f),
                    Tool.Rule.deniesDrops(this.getIncorrectBlocksForDrops()),
                    Tool.Rule.overrideSpeed(ClayiumTags.CLAY_SHOVEL_FAST_BLOCKS, 32.0f),
                    Tool.Rule.minesAndDrops(blocks, this.getSpeed())), 1.0f, 1);
        }
    },
    CLAY_STEEL_SHOVEL(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 8.0f * 6.0f, 10, Ingredient.EMPTY);

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final int enchantmentValue;
    private final Ingredient repairIngredient;

    private ClayToolTiers(TagKey<Block> incorrectBlockForDrops, int uses, float speed, int enchantmentValue,
                          Ingredient repairIngredient) {
        this.incorrectBlocksForDrops = incorrectBlockForDrops;
        this.uses = uses;
        this.speed = speed;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return 0;
    }

    @Override
    @NotNull
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocksForDrops;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    @NotNull
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
}
