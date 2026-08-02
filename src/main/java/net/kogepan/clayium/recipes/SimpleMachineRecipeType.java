package net.kogepan.clayium.recipes;

import net.kogepan.clayium.recipes.recipes.MachineRecipe;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;

public final class SimpleMachineRecipeType<T extends Recipe<?>> implements RecipeType<T> {

    public static final Codec<SimpleMachineRecipeType<MachineRecipe>> CODEC = Identifier.CODEC.comapFlatMap(id -> {
        var holder = ClayiumRecipeTypes.SIMPLE_MACHINE_RECIPE_TYPES.get(id);
        if (holder == null) {
            return DataResult.error(() -> "Unknown SimpleMachineRecipeType: " + id);
        }
        return DataResult.success(holder.get());
    }, SimpleMachineRecipeType::id);

    public static final StreamCodec<ByteBuf, SimpleMachineRecipeType<MachineRecipe>> STREAM_CODEC = Identifier.STREAM_CODEC
            .map(id -> {
                var holder = ClayiumRecipeTypes.SIMPLE_MACHINE_RECIPE_TYPES.get(id);
                if (holder == null) {
                    throw new IllegalArgumentException("Unknown SimpleMachineRecipeType: " + id);
                }
                return holder.get();
            }, SimpleMachineRecipeType::id);

    private final Identifier id;
    private final int maxInputSize;
    private final int maxOutputSize;

    private SimpleMachineRecipeType(Identifier id, int maxInputSize, int maxOutputSize) {
        this.id = id;
        this.maxInputSize = maxInputSize;
        this.maxOutputSize = maxOutputSize;
    }

    public static <T extends Recipe<?>> SimpleMachineRecipeType<T> create(Identifier id, int maxInputSize,
                                                                          int maxOutputSize) {
        return new SimpleMachineRecipeType<>(id, maxInputSize, maxOutputSize);
    }

    public Identifier id() {
        return this.id;
    }

    public int maxInputSize() {
        return this.maxInputSize;
    }

    public int maxOutputSize() {
        return this.maxOutputSize;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
