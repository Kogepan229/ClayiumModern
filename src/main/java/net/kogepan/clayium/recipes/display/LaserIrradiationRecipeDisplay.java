package net.kogepan.clayium.recipes.display;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.Block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LaserIrradiationRecipeDisplay(Block inputBlock, Block outputBlock, long requiredEnergy)
        implements RecipeDisplay {

    public static final MapCodec<LaserIrradiationRecipeDisplay> MAP_CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input")
                                    .forGetter(LaserIrradiationRecipeDisplay::inputBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output")
                                    .forGetter(LaserIrradiationRecipeDisplay::outputBlock),
                            Codec.LONG.fieldOf("required_energy")
                                    .forGetter(LaserIrradiationRecipeDisplay::requiredEnergy))
                    .apply(instance, LaserIrradiationRecipeDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserIrradiationRecipeDisplay> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.registry(Registries.BLOCK),
                    LaserIrradiationRecipeDisplay::inputBlock,
                    ByteBufCodecs.registry(Registries.BLOCK),
                    LaserIrradiationRecipeDisplay::outputBlock,
                    ByteBufCodecs.VAR_LONG,
                    LaserIrradiationRecipeDisplay::requiredEnergy,
                    LaserIrradiationRecipeDisplay::new);

    public static final RecipeDisplay.Type<LaserIrradiationRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC,
            STREAM_CODEC);

    public SlotDisplay input() {
        return displayFor(this.inputBlock);
    }

    @Override
    public SlotDisplay result() {
        return displayFor(this.outputBlock);
    }

    private static SlotDisplay displayFor(Block block) {
        Item item = block.asItem();
        return item == Items.AIR ? SlotDisplay.Empty.INSTANCE : new SlotDisplay.ItemSlotDisplay(item);
    }

    @Override
    public SlotDisplay craftingStation() {
        return SlotDisplay.Empty.INSTANCE;
    }

    @Override
    public RecipeDisplay.Type<LaserIrradiationRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return this.input().isEnabled(enabledFeatures) && this.result().isEnabled(enabledFeatures);
    }
}
