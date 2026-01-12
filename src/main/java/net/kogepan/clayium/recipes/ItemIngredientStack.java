package net.kogepan.clayium.recipes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class ItemIngredientStack {

    public static final Codec<ItemIngredientStack> CODEC = RecordCodecBuilder.create(
            builder -> builder
                    .group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.ingredient),
                            ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", 1).forGetter(i -> i.amount))
                    .apply(builder, ItemIngredientStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemIngredientStack> STREAM_CODEC = StreamCodec.of(
            (buf, s) -> s.to(buf),
            ItemIngredientStack::of);

    @Getter
    private final Ingredient ingredient;
    @Getter
    private final int amount;

    public ItemIngredientStack(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public static ItemIngredientStack of(Ingredient ingredient, int amount) {
        return new ItemIngredientStack(ingredient, amount);
    }

    public static ItemIngredientStack of(ItemStack ingredient) {
        return new ItemIngredientStack(Ingredient.of(ingredient), ingredient.getCount());
    }

    public static ItemIngredientStack of(RegistryFriendlyByteBuf buff) {
        return new ItemIngredientStack(Ingredient.CONTENTS_STREAM_CODEC.decode(buff), buff.readInt());
    }

    public void to(RegistryFriendlyByteBuf buff) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buff, this.ingredient);
        buff.writeInt(this.amount);
    }

    public boolean test(@Nullable ItemStack stack) {
        return this.ingredient.test(stack);
    }
}
