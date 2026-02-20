package net.kogepan.clayium.recipes.serializers;

import net.kogepan.clayium.recipes.recipes.LaserIrradiationRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public class LaserIrradiationRecipeSerializer implements RecipeSerializer<LaserIrradiationRecipe> {

    private static final com.mojang.serialization.Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC
            .flatXmap(
                    id -> BuiltInRegistries.BLOCK.getOptional(id)
                            .map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown block: " + id)),
                    block -> DataResult.success(BuiltInRegistries.BLOCK.getKey(block)));

    public static final MapCodec<LaserIrradiationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    BLOCK_CODEC.fieldOf("input").forGetter(LaserIrradiationRecipe::inputBlock),
                    BLOCK_CODEC.fieldOf("output").forGetter(LaserIrradiationRecipe::outputBlock),
                    com.mojang.serialization.Codec.LONG.fieldOf("energy_min")
                            .forGetter(LaserIrradiationRecipe::energyMin),
                    com.mojang.serialization.Codec.LONG.fieldOf("energy_max")
                            .forGetter(LaserIrradiationRecipe::energyMax),
                    com.mojang.serialization.Codec.LONG
                            .fieldOf("required_energy")
                            .forGetter(LaserIrradiationRecipe::requiredEnergy))
            .apply(instance, LaserIrradiationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserIrradiationRecipe> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.registry(Registries.BLOCK),
                    LaserIrradiationRecipe::inputBlock,
                    ByteBufCodecs.registry(Registries.BLOCK),
                    LaserIrradiationRecipe::outputBlock,
                    ByteBufCodecs.VAR_LONG,
                    LaserIrradiationRecipe::energyMin,
                    ByteBufCodecs.VAR_LONG,
                    LaserIrradiationRecipe::energyMax,
                    ByteBufCodecs.VAR_LONG,
                    LaserIrradiationRecipe::requiredEnergy,
                    LaserIrradiationRecipe::new);

    @Override
    @NotNull
    public MapCodec<LaserIrradiationRecipe> codec() {
        return CODEC;
    }

    @Override
    @NotNull
    public StreamCodec<RegistryFriendlyByteBuf, LaserIrradiationRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
