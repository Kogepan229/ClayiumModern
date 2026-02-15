package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.recipes.recipes.QuartzCrucibleRecipe;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuartzCrucibleBlockEntity extends BlockEntity {

    @Getter
    private int ingotQuantity = 0;
    @Getter
    private int progress = 0;
    @Nullable
    private RecipeHolder<QuartzCrucibleRecipe> recipeHolder = null;
    /**
     * Set when loading from NBT and getLevel() was null (e.g. chunk load order).
     * Cleared when recipeHolder is resolved in resolveRecipeHolder(Level).
     */
    @Nullable
    private ResourceLocation pendingRecipeId = null;

    public QuartzCrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.QUARTZ_CRUCIBLE_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state,
                            QuartzCrucibleBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }

        if (blockEntity.ingotQuantity <= 0) {
            return;
        }
        int requiredTicks = blockEntity.getRequiredTicks();
        if (requiredTicks <= 0 || blockEntity.progress >= requiredTicks) {
            return;
        }
        blockEntity.progress++;
        blockEntity.setChanged();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        resolveRecipeHolder(level);
    }

    /**
     * Returns total ticks required for current recipe and quantity, or 0 if recipe not found.
     */
    public int getRequiredTicks() {
        if (recipeHolder == null || ingotQuantity <= 0) {
            return 0;
        }
        return recipeHolder.value().duration() * ingotQuantity;
    }

    @Nullable
    public ResourceLocation getCurrentRecipeId() {
        if (recipeHolder != null) {
            return recipeHolder.id();
        }
        return pendingRecipeId;
    }

    /**
     * Resolves pendingRecipeId to recipeHolder when level is available. Call when level is known.
     */
    private void resolveRecipeHolder(Level level) {
        if (recipeHolder != null || level == null || pendingRecipeId == null) {
            return;
        }
        recipeHolder = resolveRecipeHolderFromId(level, pendingRecipeId);
        if (recipeHolder != null) {
            pendingRecipeId = null;
        } else {
            reset();
        }
    }

    @Nullable
    public QuartzCrucibleRecipe getCurrentRecipe() {
        return recipeHolder != null ? recipeHolder.value() : null;
    }

    public void setRecipeAndAddIngot(@NotNull RecipeHolder<QuartzCrucibleRecipe> holder) {
        this.recipeHolder = holder;
        this.ingotQuantity++;
        setChanged();
    }

    public void addIngot() {
        ingotQuantity++;
        setChanged();
    }

    public void reset() {
        ingotQuantity = 0;
        progress = 0;
        recipeHolder = null;
        pendingRecipeId = null;
        setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("ingotQuantity", ingotQuantity);
        tag.putInt("progress", progress);
        ResourceLocation id = recipeHolder != null ? recipeHolder.id() : pendingRecipeId;
        if (id != null) {
            tag.putString("recipeId", id.toString());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        ingotQuantity = tag.getInt("ingotQuantity");
        progress = tag.getInt("progress");
        recipeHolder = null;
        pendingRecipeId = null;
        if (!tag.contains("recipeId")) {
            return;
        }
        ResourceLocation recipeId = ResourceLocation.parse(tag.getString("recipeId"));
        Level level = getLevel();
        if (level != null) {
            recipeHolder = resolveRecipeHolderFromId(level, recipeId);
            if (recipeHolder == null) {
                reset();
            }
        } else {
            pendingRecipeId = recipeId;
        }
    }

    @SuppressWarnings("unchecked")
    private static RecipeHolder<QuartzCrucibleRecipe> resolveRecipeHolderFromId(Level level,
                                                                                ResourceLocation recipeId) {
        return (RecipeHolder<QuartzCrucibleRecipe>) level.getRecipeManager()
                .byKey(recipeId)
                .filter(holder -> holder.value() instanceof QuartzCrucibleRecipe).orElse(null);
    }
}
