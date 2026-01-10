package net.kogepan.clayium.items.tools;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

public enum ClayToolTiers implements Tier {

    CLAY_PICKAXE(BlockTags.INCORRECT_FOR_STONE_TOOL, 500, 4.0f, 5),
    CLAY_STEEL_PICKAXE(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 8.0f * 6.0f, 10),
    CLAY_SHOVEL(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 500, 2.0f, 15),
    CLAY_STEEL_SHOVEL(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 8.0f * 6.0f, 10);

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final int enchantmentValue;

    private ClayToolTiers(TagKey<Block> incorrectBlockForDrops, int uses, float speed, int enchantmentValue) {
        this.incorrectBlocksForDrops = incorrectBlockForDrops;
        this.uses = uses;
        this.speed = speed;
        this.enchantmentValue = enchantmentValue;
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
        return Ingredient.EMPTY;
    }
}
