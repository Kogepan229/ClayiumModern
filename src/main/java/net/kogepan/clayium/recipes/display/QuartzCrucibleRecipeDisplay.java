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

public record QuartzCrucibleRecipeDisplay(SlotDisplay input,
                                          SlotDisplay catalyst,
                                          SlotDisplay output,
                                          int duration)
        implements RecipeDisplay {

    public static final MapCodec<QuartzCrucibleRecipeDisplay> MAP_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(
                            SlotDisplay.CODEC.fieldOf("input").forGetter(QuartzCrucibleRecipeDisplay::input),
                            SlotDisplay.CODEC.fieldOf("catalyst").forGetter(QuartzCrucibleRecipeDisplay::catalyst),
                            SlotDisplay.CODEC.fieldOf("output").forGetter(QuartzCrucibleRecipeDisplay::output),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("duration")
                                    .forGetter(QuartzCrucibleRecipeDisplay::duration))
                    .apply(instance, QuartzCrucibleRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuartzCrucibleRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    SlotDisplay.STREAM_CODEC,
                    QuartzCrucibleRecipeDisplay::input,
                    SlotDisplay.STREAM_CODEC,
                    QuartzCrucibleRecipeDisplay::catalyst,
                    SlotDisplay.STREAM_CODEC,
                    QuartzCrucibleRecipeDisplay::output,
                    ByteBufCodecs.VAR_INT,
                    QuartzCrucibleRecipeDisplay::duration,
                    QuartzCrucibleRecipeDisplay::new);

    public static final RecipeDisplay.Type<QuartzCrucibleRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC,
            STREAM_CODEC);

    @Override
    public SlotDisplay result() {
        return this.output;
    }

    @Override
    public SlotDisplay craftingStation() {
        return SlotDisplay.Empty.INSTANCE;
    }

    @Override
    public RecipeDisplay.Type<QuartzCrucibleRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.input.isEnabled(enabledFeatures) && this.catalyst.isEnabled(enabledFeatures) &&
                this.output.isEnabled(enabledFeatures);
    }
}
