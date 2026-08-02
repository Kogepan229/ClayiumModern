package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.display.ClayiumRecipeBookCategories;
import net.kogepan.clayium.recipes.display.QuartzCrucibleRecipeDisplay;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record QuartzCrucibleRecipe(Ingredient input,
                                   Ingredient catalyst,
                                   ItemStackTemplate result,
                                   int duration)
        implements Recipe<SingleRecipeInput> {

    public static final MapCodec<QuartzCrucibleRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Ingredient.CODEC.fieldOf("input").forGetter(QuartzCrucibleRecipe::input),
                    Ingredient.CODEC.fieldOf("catalyst").forGetter(QuartzCrucibleRecipe::catalyst),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(QuartzCrucibleRecipe::result),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("duration").forGetter(QuartzCrucibleRecipe::duration))
            .apply(instance, QuartzCrucibleRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuartzCrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            QuartzCrucibleRecipe::input,
            Ingredient.CONTENTS_STREAM_CODEC,
            QuartzCrucibleRecipe::catalyst,
            ItemStackTemplate.STREAM_CODEC,
            QuartzCrucibleRecipe::result,
            ByteBufCodecs.VAR_INT,
            QuartzCrucibleRecipe::duration,
            QuartzCrucibleRecipe::new);

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return this.result.create();
    }

    public ItemStack createResultForQuantity(int quantity) {
        ItemStack output = this.result.create();
        output.setCount(this.result.count() * quantity);
        return output;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<QuartzCrucibleRecipe> getSerializer() {
        return ClayiumRecipeSerializers.QUARTZ_CRUCIBLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<QuartzCrucibleRecipe> getType() {
        return ClayiumRecipeTypes.QUARTZ_CRUCIBLE_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input);
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new QuartzCrucibleRecipeDisplay(
                this.input.display(),
                this.catalyst.display(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                this.duration));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ClayiumRecipeBookCategories.CLAYIUM.get();
    }
}
