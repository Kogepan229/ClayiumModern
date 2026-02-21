package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.recipes.ItemIngredientStack;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.utils.TransferUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
    protected boolean canProgress = false; // TODO: onFIrstTick
    protected final RecipeType<MachineRecipe> recipeType;

    protected boolean isInvalidInputsForRecipes = false;
    protected boolean noEnoughOutputSpace = false;
    protected boolean isInputItemInventoryChanged = true;
    protected boolean isOutputItemInventoryChanged = true;

    public AbstractRecipeLogic(@NotNull ClayContainerBlockEntity blockEntity, RecipeType<MachineRecipe> recipeType) {
        super(blockEntity, TRAIT_ID);
        this.recipeType = recipeType;
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        // if (metaTileEntity.offsetTimer % 20 == 0L) checkCanProgress()

        if (!canProgress) return;

        if (processingRecipeHolder == null && shouldSearchForRecipe()) {
            tryPrepareNewRecipe();
        }

        if (processingRecipeHolder != null) {
            updateWorkingProgress();
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
            currentProgress = 0;
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
        }
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        currentProgress = tag.getLong("progress");
        processingRecipeHolder = null;
        pendingRecipeId = null;
        if (!tag.contains("recipeId")) {
            return;
        }
        ResourceLocation recipeId = ResourceLocation.parse(tag.getString("recipeId"));
        Level level = blockEntity.getLevel();
        if (level != null) {
            processingRecipeHolder = resolveRecipeHolderFromId(level, recipeId);
            if (processingRecipeHolder == null) {
                currentProgress = 0;
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
            return recipe.duration();
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
        if (!this.drawEnergy(getRecipeCEPerTick(holder), true)) return false;
        if (!TransferUtils.simulateInsertItemsToHandler(blockEntity.getOutputInventory(), getRecipeOutputs(holder))) {
            noEnoughOutputSpace = true;
            return false;
        }

        IItemHandlerModifiable machineInventory = blockEntity.getInputInventory();

        // Consume ingredients;
        for (var ingredient : getRecipeInputs(holder)) {
            for (var inventoryStack : inventoryStacks) {
                if (!ingredient.test(inventoryStack)) continue;
                ItemStack stackToConsume = inventoryStack.copy();
                stackToConsume.setCount(ingredient.getAmount());

                for (int i = 0; i < machineInventory.getSlots(); i++) {
                    ItemStack machineStack = machineInventory.getStackInSlot(i);
                    if (machineStack.isEmpty() || !machineStack.is(stackToConsume.getItem())) continue;

                    ItemStack extracted = machineInventory.extractItem(i, stackToConsume.getCount(), false);
                    stackToConsume.setCount(stackToConsume.getCount() - extracted.getCount());
                    if (stackToConsume.isEmpty()) {
                        break;
                    }
                }
            }
        }

        processingRecipeHolder = holder;
        currentProgress = getProgressPerTick();
        return true;
    }

    protected boolean checkCanProgress() {
        return this.canProgress = true;
    }

    protected abstract boolean drawEnergy(long amount, boolean simulate);

    protected long getProgressPerTick() {
        return 1;
    }

    protected void updateWorkingProgress() {
        if (processingRecipeHolder == null) {
            return;
        }

        if (!drawEnergy(getRecipeCEPerTick(processingRecipeHolder), false)) {
            return;
        }

        currentProgress += getProgressPerTick();
        if (currentProgress >= getRecipeDuration(processingRecipeHolder)) {
            completeWork();
        }
    }

    protected void completeWork() {
        currentProgress = 0;

        IItemHandler outputInventory = blockEntity.getOutputInventory();
        assert this.processingRecipeHolder != null;
        for (ItemStack stack : getCopiedRecipeOutputs(processingRecipeHolder)) {
            ItemHandlerHelper.insertItem(outputInventory, stack, false);
        }
        this.processingRecipeHolder = null;
    }

    public UIElement createProgressUIElement() {
        return new ProgressArrow()
                .bind(DataBindingBuilder
                        .floatValS2C(() -> this.processingRecipeHolder != null ?
                                (float) this.currentProgress / getRecipeDuration(processingRecipeHolder) : 0)
                        .build())
                .layout(layout -> layout.width(22));
    }
}
