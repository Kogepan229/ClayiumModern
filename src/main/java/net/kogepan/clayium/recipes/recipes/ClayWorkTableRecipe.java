package net.kogepan.clayium.recipes.recipes;

import net.kogepan.clayium.recipes.ClayiumRecipeSerializers;
import net.kogepan.clayium.recipes.ClayiumRecipeTypes;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.display.ClayWorkTableRecipeDisplay;
import net.kogepan.clayium.recipes.display.ClayiumRecipeBookCategories;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record ClayWorkTableRecipe(ItemIngredientStack ingredient,
                                  ItemStackTemplate result,
                                  Optional<ItemStackTemplate> byproduct,
                                  int button,
                                  int cost)
        implements Recipe<SingleRecipeInput> {

    private static final Codec<Integer> BUTTON_CODEC = ExtraCodecs.intRange(0, 5);

    public static final MapCodec<ClayWorkTableRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ItemIngredientStack.CODEC.fieldOf("input_item").forGetter(ClayWorkTableRecipe::ingredient),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(ClayWorkTableRecipe::result),
                    ItemStackTemplate.CODEC.optionalFieldOf("byproduct").forGetter(ClayWorkTableRecipe::byproduct),
                    BUTTON_CODEC.fieldOf("button").forGetter(ClayWorkTableRecipe::button),
                    ExtraCodecs.POSITIVE_INT.fieldOf("cost").forGetter(ClayWorkTableRecipe::cost))
            .apply(instance, ClayWorkTableRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClayWorkTableRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemIngredientStack.STREAM_CODEC,
            ClayWorkTableRecipe::ingredient,
            ItemStackTemplate.STREAM_CODEC,
            ClayWorkTableRecipe::result,
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC),
            ClayWorkTableRecipe::byproduct,
            ByteBufCodecs.VAR_INT,
            ClayWorkTableRecipe::button,
            ByteBufCodecs.VAR_INT,
            ClayWorkTableRecipe::cost,
            ClayWorkTableRecipe::new);

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item()) && input.item().getCount() >= this.ingredient.amount();
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return this.result.create();
    }

    public Optional<ItemStack> createByproduct() {
        return this.byproduct.map(ItemStackTemplate::create);
    }

    public int adjustedCost() {
        return this.adjustedCost(ProgressionRates.current());
    }

    public int adjustedCost(double progressionRate) {
        return Math.max(1, ProgressionRates.divideInt(this.cost, progressionRate));
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
    public RecipeSerializer<ClayWorkTableRecipe> getSerializer() {
        return ClayiumRecipeSerializers.CLAY_WORK_TABLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<ClayWorkTableRecipe> getType() {
        return ClayiumRecipeTypes.CLAY_WORK_TABLE_RECIPE_TYPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.ingredient.ingredient());
    }

    @Override
    public List<RecipeDisplay> display() {
        Optional<SlotDisplay> byproductDisplay = this.byproduct
                .map(SlotDisplay.ItemStackSlotDisplay::new)
                .map(SlotDisplay.class::cast);
        return List.of(new ClayWorkTableRecipeDisplay(
                this.ingredient.display(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                byproductDisplay,
                this.button,
                this.adjustedCost()));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ClayiumRecipeBookCategories.CLAYIUM.get();
    }
}
