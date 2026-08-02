package net.kogepan.clayium.recipes;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.stream.Stream;

public record SizedIngredientSlotDisplay(SlotDisplay contents, int amount) implements SlotDisplay {

    public static final MapCodec<SizedIngredientSlotDisplay> MAP_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(
                            SlotDisplay.CODEC.fieldOf("contents").forGetter(SizedIngredientSlotDisplay::contents),
                            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(SizedIngredientSlotDisplay::amount))
                    .apply(instance, SizedIngredientSlotDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SizedIngredientSlotDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC,
                    SizedIngredientSlotDisplay::contents,
                    ByteBufCodecs.VAR_INT,
                    SizedIngredientSlotDisplay::amount,
                    SizedIngredientSlotDisplay::new);

    public static final SlotDisplay.Type<SizedIngredientSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC,
            STREAM_CODEC);

    public SizedIngredientSlotDisplay {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ingredient display amount must be positive");
        }
    }

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
        if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            return this.contents.resolveForStacks(context).stream().map(stack -> stacks.forStack(withAmount(stack)));
        }
        return this.contents.resolve(context, factory);
    }

    private ItemStack withAmount(ItemStack stack) {
        ItemStack result = stack.copy();
        result.setCount(this.amount);
        return result;
    }

    @Override
    public SlotDisplay.Type<SizedIngredientSlotDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.contents.isEnabled(enabledFeatures);
    }
}
