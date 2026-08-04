package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.MachineRecipeMatcher;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.utils.TransferUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecipeLogic extends ClayContainerTrait {

    public static final String TRAIT_ID = "recipeLogic";

    @Nullable
    protected RecipeHolder<?> processingRecipeHolder;
    /**
     * Set when loading from NBT and getLevel() was null (e.g. chunk load order).
     * Cleared when recipeHolder is resolved in resolveRecipeHolder(Level).
     */
    @Nullable
    protected ResourceLocation pendingRecipeId = null;
    protected long currentProgress = 0;
    protected long processingCEPerTick = 0;
    protected long processingDuration = 0;
    protected boolean effectiveRecipeValuesLoaded = false;
    protected boolean canProgress = false; // TODO: onFIrstTick
    protected boolean workedThisTick;
    protected final RecipeType<?> recipeType;
    protected final OverclockHandler overclockHandler;

    protected boolean isInvalidInputsForRecipes = false;
    protected boolean noEnoughOutputSpace = false;
    protected boolean isInputItemInventoryChanged = true;
    protected boolean isOutputItemInventoryChanged = true;

    public AbstractRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity, RecipeType<?> recipeType) {
        super(blockEntity, TRAIT_ID);
        this.recipeType = recipeType;
        OverclockHandler handler = blockEntity.getOverclockHandler();
        if (handler == null) {
            throw new IllegalStateException("Recipe logic requires an OverclockHandler");
        }
        this.overclockHandler = handler;
    }

    @Override
    public void tick() {
        this.workedThisTick = false;
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        if (this.blockEntity instanceof WorkableClayContainerBlockEntity workable &&
                !workable.canRunExternallyControlledWork()) {
            return;
        }

        // if (metaTileEntity.offsetTimer % 20 == 0L) checkCanProgress()

        if (!canProgress) return;

        int operations = this.overclockHandler.getOperationsThisTick();
        for (int virtualTick = 0; virtualTick < operations; virtualTick++) {
            if (processingRecipeHolder == null && shouldSearchForRecipe()) {
                tryPrepareNewRecipe();
            }

            if (processingRecipeHolder != null) {
                updateWorkingProgress(virtualTick);
            }
        }
    }

    @Override
    public void onLoad() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        resolveRecipeHolder(level);
        this.canProgress = checkCanProgress();
    }

    /**
     * Resolves pendingRecipeId to processingRecipeHolder when level is available. Call when level is known.
     */
    protected void resolveRecipeHolder(Level level) {
        if (processingRecipeHolder != null || level == null || pendingRecipeId == null) {
            return;
        }
        processingRecipeHolder = resolveRecipeHolderFromId(level, pendingRecipeId);

        if (processingRecipeHolder == null) {
            clearProcessingState();
        } else if (!this.effectiveRecipeValuesLoaded) {
            restoreBaseRecipeValues(processingRecipeHolder);
        }
        pendingRecipeId = null;
    }

    @Nullable
    private RecipeHolder<?> resolveRecipeHolderFromId(Level level, ResourceLocation recipeId) {
        return level.getRecipeManager()
                .byKey(recipeId)
                .orElse(null);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putLong("progress", currentProgress);
        if (processingRecipeHolder != null) {
            tag.putString("recipeId", processingRecipeHolder.id().toString());
            tag.putLong("processingCEPerTick", this.processingCEPerTick);
            tag.putLong("processingDuration", this.processingDuration);
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        currentProgress = tag.getLong("progress");
        processingRecipeHolder = null;
        pendingRecipeId = null;
        this.processingCEPerTick = 0L;
        this.processingDuration = 0L;
        this.effectiveRecipeValuesLoaded = tag.contains("processingCEPerTick", Tag.TAG_LONG) &&
                tag.contains("processingDuration", Tag.TAG_LONG);
        if (this.effectiveRecipeValuesLoaded) {
            this.processingCEPerTick = Math.max(0L, tag.getLong("processingCEPerTick"));
            this.processingDuration = Math.max(1L, tag.getLong("processingDuration"));
        }
        if (!tag.contains("recipeId")) {
            clearProcessingState();
            return;
        }
        ResourceLocation recipeId = ResourceLocation.parse(tag.getString("recipeId"));
        Level level = blockEntity.getLevel();
        if (level != null) {
            processingRecipeHolder = resolveRecipeHolderFromId(level, recipeId);
            if (processingRecipeHolder == null) {
                clearProcessingState();
            } else if (!this.effectiveRecipeValuesLoaded) {
                restoreBaseRecipeValues(processingRecipeHolder);
            }
        } else {
            pendingRecipeId = recipeId;
        }
    }

    protected abstract RecipeHolder<?> getMatchedRecipe(Level level, List<ItemStack> inventoryStacks);

    protected long getRecipeCEPerTick(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof MachineRecipe recipe) {
            return recipe.cePerTick();
        }
        throw new IllegalStateException();
    }

    protected long getRecipeDuration(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof MachineRecipe recipe) {
            return recipe.adjustedDuration();
        }
        throw new IllegalStateException();
    }

    protected List<ItemIngredientStack> getRecipeInputs(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof MachineRecipe recipe) {
            return recipe.inputs();
        }
        throw new IllegalStateException();
    }

    protected List<ItemStack> getRecipeOutputs(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof MachineRecipe recipe) {
            return recipe.outputs();
        }
        throw new IllegalStateException();
    }

    protected List<ItemStack> getCopiedRecipeOutputs(RecipeHolder<?> recipeHolder) {
        if (recipeHolder.value() instanceof MachineRecipe recipe) {
            return recipe.copyOutputs();
        }
        throw new IllegalStateException();
    }

    public void notifyItemInputInventoryChanged() {
        isInputItemInventoryChanged = true;
    }

    public void notifyItemOutputInventoryChanged() {
        isOutputItemInventoryChanged = true;
    }

    protected boolean shouldSearchForRecipe() {
        return isReadyForInputs() && isReadyForOutputs();
    }

    protected boolean isReadyForInputs() {
        if (isInvalidInputsForRecipes && !isInputItemInventoryChanged) return false;

        isInvalidInputsForRecipes = false;
        isInputItemInventoryChanged = false;
        return true;
    }

    protected boolean isReadyForOutputs() {
        if (noEnoughOutputSpace && !isOutputItemInventoryChanged) return false;

        noEnoughOutputSpace = false;
        isOutputItemInventoryChanged = false;
        return true;
    }

    /**
     * Converts the given inventory's contents into a list of item stacks,
     * merging stacks that are the same item with the same components.
     *
     * @param inventory the item handler to read from
     * @return a list of merged item stacks (no duplicate item types)
     */
    private static List<ItemStack> getList(IItemHandler inventory) {
        List<ItemStack> list = new ArrayList<>();

        outer:
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            for (int j = 0; j < list.size(); j++) {
                ItemStack inList = list.get(j);
                if (ItemStack.isSameItemSameComponents(stack, inList)) {
                    inList.setCount(inList.getCount() + stack.getCount());
                    continue outer;
                }
            }

            list.add(stack);
        }

        return list;
    }

    protected boolean tryPrepareNewRecipe() {
        Level level = this.blockEntity.getLevel();
        if (level == null) return false;

        List<ItemStack> inventoryStacks = getList(blockEntity.getInputInventory());
        RecipeHolder<?> matchedRecipeHolder = getMatchedRecipe(level, inventoryStacks);

        if (matchedRecipeHolder != null) {
            return prepareRecipe(matchedRecipeHolder, inventoryStacks);
        }

        isInvalidInputsForRecipes = true;
        return false;
    }

    protected boolean prepareRecipe(RecipeHolder<?> holder, List<ItemStack> inventoryStacks) {
        List<ItemIngredientStack> recipeInputs = getRecipeInputs(holder);
        int[] matchedSlots = MachineRecipeMatcher.findMatches(recipeInputs, inventoryStacks);
        if (matchedSlots == null) return false;
        long effectiveCEPerTick = this.overclockHandler.applyCEPerTick(getRecipeCEPerTick(holder));
        long effectiveDuration = this.overclockHandler.applyDuration(getRecipeDuration(holder));
        if (!this.drawEnergy(effectiveCEPerTick, true)) return false;
        if (!hasEnoughOutputSpace(holder)) {
            noEnoughOutputSpace = true;
            return false;
        }

        IItemHandlerModifiable machineInventory = blockEntity.getInputInventory();

        for (int ingredientIndex = 0; ingredientIndex < recipeInputs.size(); ingredientIndex++) {
            machineInventory.extractItem(matchedSlots[ingredientIndex],
                    recipeInputs.get(ingredientIndex).getAmount(), false);
        }

        processingRecipeHolder = holder;
        this.processingCEPerTick = effectiveCEPerTick;
        this.processingDuration = effectiveDuration;
        this.effectiveRecipeValuesLoaded = true;
        currentProgress = getInitialProgress();
        return true;
    }

    protected boolean hasEnoughOutputSpace(RecipeHolder<?> recipeHolder) {
        return TransferUtils.simulateInsertItemsToHandler(blockEntity.getOutputInventory(),
                getRecipeOutputs(recipeHolder));
    }

    protected boolean canContinueProcessing(RecipeHolder<?> recipeHolder) {
        return true;
    }

    protected boolean checkCanProgress() {
        return this.canProgress = true;
    }

    protected abstract boolean drawEnergy(long amount, boolean simulate);

    protected long getProgressPerTick() {
        return 1;
    }

    protected long getProgressPerTick(int virtualTick) {
        return getProgressPerTick();
    }

    protected long getInitialProgress() {
        return getProgressPerTick();
    }

    protected void updateWorkingProgress(int virtualTick) {
        if (processingRecipeHolder == null) {
            return;
        }

        if (!canContinueProcessing(processingRecipeHolder)) {
            return;
        }

        if (!drawEnergy(this.processingCEPerTick, false)) {
            return;
        }

        this.workedThisTick = true;
        currentProgress += getProgressPerTick(virtualTick);
        if (currentProgress >= this.processingDuration) {
            completeWork();
        }
    }

    protected void completeWork() {
        IItemHandler outputInventory = blockEntity.getOutputInventory();
        assert this.processingRecipeHolder != null;
        for (ItemStack stack : getCopiedRecipeOutputs(processingRecipeHolder)) {
            ItemHandlerHelper.insertItem(outputInventory, stack, false);
        }
        clearProcessingState();
        if (this.blockEntity instanceof WorkableClayContainerBlockEntity workable) {
            workable.onExternallyControlledWorkCompleted();
        }
    }

    /** Returns whether a recipe is processing or the current inputs can schedule one. */
    public boolean hasWorkScheduled() {
        if (this.processingRecipeHolder != null || this.pendingRecipeId != null) {
            return true;
        }
        Level level = this.blockEntity.getLevel();
        if (level == null) {
            return false;
        }
        RecipeHolder<?> matchedRecipe = getMatchedRecipe(level, getList(this.blockEntity.getInputInventory()));
        return matchedRecipe != null && hasEnoughOutputSpace(matchedRecipe);
    }

    /** Returns whether this recipe logic performed work during its latest tick. */
    public boolean workedThisTick() {
        return this.workedThisTick;
    }

    public boolean isProcessingRecipe() {
        return this.processingRecipeHolder != null;
    }

    public float getProgressFraction() {
        if (!this.isProcessingRecipe() || this.processingDuration <= 0L) {
            return 0.0F;
        }
        return Math.clamp((float) this.currentProgress / this.processingDuration, 0.0F, 1.0F);
    }

    private void restoreBaseRecipeValues(@NotNull RecipeHolder<?> holder) {
        this.processingCEPerTick = Math.max(0L, getRecipeCEPerTick(holder));
        this.processingDuration = Math.max(1L, getRecipeDuration(holder));
        this.effectiveRecipeValuesLoaded = true;
    }

    private void clearProcessingState() {
        this.processingRecipeHolder = null;
        this.pendingRecipeId = null;
        this.currentProgress = 0L;
        this.processingCEPerTick = 0L;
        this.processingDuration = 0L;
        this.effectiveRecipeValuesLoaded = false;
    }

    public UIElement createProgressUIElement() {
        return new ProgressArrow()
                .bind(DataBindingBuilder
                        .floatValS2C(this::getProgressFraction)
                        .build())
                .layout(layout -> layout.width(22));
    }
}
