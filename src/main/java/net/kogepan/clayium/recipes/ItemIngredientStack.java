package net.kogepan.clayium.recipes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemIngredientStack(Ingredient ingredient, int amount) {

    public static final Codec<ItemIngredientStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemIngredientStack::ingredient),
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", 1).forGetter(ItemIngredientStack::amount))
            .apply(instance, ItemIngredientStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemIngredientStack> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            ItemIngredientStack::ingredient,
            ByteBufCodecs.VAR_INT,
            ItemIngredientStack::amount,
            ItemIngredientStack::new);

    public ItemIngredientStack {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ingredient amount must be positive");
        }
    }

    public static ItemIngredientStack of(Ingredient ingredient, int amount) {
        return new ItemIngredientStack(ingredient, amount);
    }

    public static ItemIngredientStack of(ItemStack ingredient) {
        if (ingredient.isEmpty()) {
            throw new IllegalArgumentException("Ingredient stack must be non-empty");
        }
        return new ItemIngredientStack(Ingredient.of(ingredient.getItem()), ingredient.getCount());
    }

    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack);
    }

    public SlotDisplay display() {
        SlotDisplay contents = this.ingredient.display();
        return this.amount == 1 ? contents : new SizedIngredientSlotDisplay(contents, this.amount);
    }
}
