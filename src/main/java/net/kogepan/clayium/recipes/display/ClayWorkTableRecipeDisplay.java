package net.kogepan.clayium.recipes.display;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record ClayWorkTableRecipeDisplay(SlotDisplay input,
                                         SlotDisplay output,
                                         Optional<SlotDisplay> byproduct,
                                         int button,
                                         int cost)
        implements RecipeDisplay {

    public static final MapCodec<ClayWorkTableRecipeDisplay> MAP_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(
                            SlotDisplay.CODEC.fieldOf("input").forGetter(ClayWorkTableRecipeDisplay::input),
                            SlotDisplay.CODEC.fieldOf("output").forGetter(ClayWorkTableRecipeDisplay::output),
                            SlotDisplay.CODEC.optionalFieldOf("byproduct")
                                    .forGetter(ClayWorkTableRecipeDisplay::byproduct),
                            ExtraCodecs.intRange(0, 5).fieldOf("button").forGetter(ClayWorkTableRecipeDisplay::button),
                            ExtraCodecs.POSITIVE_INT.fieldOf("cost").forGetter(ClayWorkTableRecipeDisplay::cost))
                    .apply(instance, ClayWorkTableRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClayWorkTableRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC,
                    ClayWorkTableRecipeDisplay::input,
                    SlotDisplay.STREAM_CODEC,
                    ClayWorkTableRecipeDisplay::output,
                    ByteBufCodecs.optional(SlotDisplay.STREAM_CODEC),
                    ClayWorkTableRecipeDisplay::byproduct,
                    ByteBufCodecs.VAR_INT,
                    ClayWorkTableRecipeDisplay::button,
                    ByteBufCodecs.VAR_INT,
                    ClayWorkTableRecipeDisplay::cost,
                    ClayWorkTableRecipeDisplay::new);

    public static final RecipeDisplay.Type<ClayWorkTableRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC,
            STREAM_CODEC);

    @Override
    public SlotDisplay result() {
        return this.byproduct
                .<SlotDisplay>map(value -> new SlotDisplay.Composite(List.of(this.output, value)))
                .orElse(this.output);
    }

    @Override
    public SlotDisplay craftingStation() {
        return SlotDisplay.Empty.INSTANCE;
    }

    @Override
    public RecipeDisplay.Type<ClayWorkTableRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.input.isEnabled(enabledFeatures) && this.output.isEnabled(enabledFeatures) &&
                this.byproduct.map(value -> value.isEnabled(enabledFeatures)).orElse(true);
    }
}
