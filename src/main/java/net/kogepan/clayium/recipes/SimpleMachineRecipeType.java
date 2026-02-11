package net.kogepan.clayium.recipes;

import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

public class SimpleMachineRecipeType<T extends Recipe<?>> implements RecipeType<T> {

    @Getter
    final ResourceLocation id;
    @Getter
    final int maxInputSize;
    @Getter
    final int maxOutputSize;

    private SimpleMachineRecipeType(ResourceLocation id, final int maxInputSize, final int maxOutputSize) {
        this.id = id;
        this.maxInputSize = maxInputSize;
        this.maxOutputSize = maxOutputSize;
    }

    public static <T extends Recipe<?>> SimpleMachineRecipeType<T> create(ResourceLocation name, final int maxInputSize,
                                                                          final int maxOutputSize) {
        final String toString = name.toString();
        return new SimpleMachineRecipeType<>(name, maxInputSize, maxOutputSize) {

            @Override
            public String toString() {
                return toString;
            }
        };
    }

    public static final Codec<SimpleMachineRecipeType<MachineRecipe>> CODEC = ResourceLocation.CODEC
            .comapFlatMap(id -> {
                var holder = ClayiumRecipeTypes.SIMPLE_MACHINE_RECIPE_TYPES.get(id);
                if (holder != null) {
                    return DataResult.success(holder.get());
                }
                return DataResult.error(() -> "Unknown SimpleMachineRecipeType: " + id);
            }, SimpleMachineRecipeType::getId);

    public static final StreamCodec<ByteBuf, SimpleMachineRecipeType<MachineRecipe>> STREAM_CODEC = ResourceLocation.STREAM_CODEC
            .map(
                    id -> {
                        var holder = ClayiumRecipeTypes.SIMPLE_MACHINE_RECIPE_TYPES.get(id);
                        if (holder != null) {
                            return holder.get();
                        }
                        throw new IllegalArgumentException("Unknown SimpleMachineRecipeType: " + id);
                    }, SimpleMachineRecipeType::getId);
}
