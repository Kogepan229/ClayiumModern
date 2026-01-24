package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.utils.TransferUtils;

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
    protected RecipeHolder<MachineRecipe> processingRecipeHolder;
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

    public void onLoad() {
        Level level = this.blockEntity.getLevel();
        if (level == null || level.isClientSide()) return;

        this.canProgress = checkCanProgress();
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

        var recipeHolders = level.getRecipeManager()
                .getAllRecipesFor(this.recipeType);

        List<ItemStack> inventoryStacks = getList(blockEntity.getInputInventory());
        for (RecipeHolder<MachineRecipe> holder : recipeHolders) {
            MachineRecipe recipe = holder.value();
            if (recipe.recipeTier() > this.blockEntity.tier) continue;

            if (recipe.matchesItems(inventoryStacks)) {
                return prepareRecipe(holder, inventoryStacks);
            }
        }

        isInvalidInputsForRecipes = true;
        return false;
    }

    protected boolean prepareRecipe(RecipeHolder<MachineRecipe> holder, List<ItemStack> inventoryStacks) {
        MachineRecipe recipe = holder.value();
        if (!this.drawEnergy(recipe.cePerTick(), true)) return false;
        if (!TransferUtils.simulateInsertItemsToHandler(blockEntity.getOutputInventory(), recipe.outputs())) {
            noEnoughOutputSpace = true;
            return false;
        }

        IItemHandlerModifiable machineInventory = blockEntity.getInputInventory();

        // Consume ingredients
        for (var ingredient : recipe.inputs()) {
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

        MachineRecipe recipe = processingRecipeHolder.value();
        if (!drawEnergy(recipe.cePerTick(), false)) {
            return;
        }

        currentProgress += getProgressPerTick();
        if (currentProgress >= recipe.duration()) {
            completeWork();
        }
    }

    protected void completeWork() {
        currentProgress = 0;

        IItemHandler outputInventory = blockEntity.getOutputInventory();
        assert this.processingRecipeHolder != null;
        for (ItemStack stack : this.processingRecipeHolder.value().copyOutputs()) {
            ItemHandlerHelper.insertItem(outputInventory, stack, false);
        }
        this.processingRecipeHolder = null;
    }

    public UIElement createProgressUIElement() {
        return new ProgressArrow()
                .bind(DataBindingBuilder
                        .floatValS2C(() -> this.processingRecipeHolder != null ?
                                (float) this.currentProgress / this.processingRecipeHolder.value().duration() : 0)
                        .build())
                .layout(layout -> layout.width(22));
    }
}
