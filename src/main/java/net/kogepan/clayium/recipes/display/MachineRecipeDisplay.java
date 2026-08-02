package net.kogepan.clayium.recipes.display;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record MachineRecipeDisplay(Identifier recipeType,
                                   List<SlotDisplay> inputs,
                                   List<SlotDisplay> outputs,
                                   long duration,
                                   long cePerTick,
                                   int recipeTier)
        implements RecipeDisplay {

    public static final MapCodec<MachineRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Identifier.CODEC.fieldOf("recipe_type").forGetter(MachineRecipeDisplay::recipeType),
                    SlotDisplay.CODEC.listOf().fieldOf("inputs").forGetter(MachineRecipeDisplay::inputs),
                    SlotDisplay.CODEC.listOf().fieldOf("outputs").forGetter(MachineRecipeDisplay::outputs),
                    Codec.LONG.fieldOf("duration").forGetter(MachineRecipeDisplay::duration),
                    Codec.LONG.fieldOf("ce_per_tick").forGetter(MachineRecipeDisplay::cePerTick),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("recipe_tier").forGetter(MachineRecipeDisplay::recipeTier))
            .apply(instance, MachineRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MachineRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    Identifier.STREAM_CODEC,
                    MachineRecipeDisplay::recipeType,
                    SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    MachineRecipeDisplay::inputs,
                    SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    MachineRecipeDisplay::outputs,
                    ByteBufCodecs.VAR_LONG,
                    MachineRecipeDisplay::duration,
                    ByteBufCodecs.VAR_LONG,
                    MachineRecipeDisplay::cePerTick,
                    ByteBufCodecs.VAR_INT,
                    MachineRecipeDisplay::recipeTier,
                    MachineRecipeDisplay::new);

    public static final RecipeDisplay.Type<MachineRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC,
            STREAM_CODEC);

    public MachineRecipeDisplay {
        inputs = List.copyOf(inputs);
        outputs = List.copyOf(outputs);
    }

    @Override
    public SlotDisplay result() {
        return switch (this.outputs.size()) {
            case 0 -> SlotDisplay.Empty.INSTANCE;
            case 1 -> this.outputs.getFirst();
            default -> new SlotDisplay.Composite(this.outputs);
        };
    }

    @Override
    public SlotDisplay craftingStation() {
        return SlotDisplay.Empty.INSTANCE;
    }

    @Override
    public RecipeDisplay.Type<MachineRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.inputs.stream().allMatch(value -> value.isEnabled(enabledFeatures)) &&
                this.outputs.stream().allMatch(value -> value.isEnabled(enabledFeatures));
    }
}
