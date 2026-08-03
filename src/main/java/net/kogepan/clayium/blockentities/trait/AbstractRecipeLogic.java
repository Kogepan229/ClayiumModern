package net.kogepan.clayium.blockentities.trait;

import net.kogepan.clayium.blockentities.WorkableClayContainerBlockEntity;
import net.kogepan.clayium.recipes.SimpleMachineRecipeType;
import net.kogepan.clayium.recipes.inputs.MachineRecipeInput;
import net.kogepan.clayium.recipes.recipes.MachineRecipe;
import net.kogepan.clayium.utils.TransferUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecipeLogic extends ClayContainerTrait {

    public static final String TRAIT_ID = "recipeLogic";

    private static final String PROGRESS_KEY = "progress";
    private static final String RECIPE_ID_KEY = "recipeId";

    protected final WorkableClayContainerBlockEntity blockEntity;
    protected final SimpleMachineRecipeType<MachineRecipe> recipeType;

    protected @Nullable RecipeHolder<MachineRecipe> processingRecipe;
    private @Nullable RecipeHolder<MachineRecipe> matchedRecipe;
    private @Nullable Identifier pendingRecipeId;
    protected long currentProgress;
    private boolean recipeSearchRequired = true;

    protected AbstractRecipeLogic(
                                  WorkableClayContainerBlockEntity blockEntity,
                                  SimpleMachineRecipeType<MachineRecipe> recipeType) {
        super(blockEntity, TRAIT_ID);
        this.blockEntity = blockEntity;
        this.recipeType = recipeType;
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        this.resolvePendingRecipe(serverLevel);
        if (this.processingRecipe == null) {
            if (this.matchedRecipe == null && this.recipeSearchRequired) {
                this.matchedRecipe = this.findMatchedRecipe(serverLevel);
                this.recipeSearchRequired = false;
            }
            RecipeHolder<MachineRecipe> recipe = this.matchedRecipe;
            if (recipe != null && this.prepareRecipe(recipe)) {
                this.matchedRecipe = null;
                this.completeRecipeIfReady();
            }
            return;
        }

        if (this.currentProgress >= this.recipeDuration(this.processingRecipe)) {
            this.completeRecipeIfReady();
            return;
        }

        if (this.drawEnergy(this.processingRecipe.value().cePerTick())) {
            this.currentProgress++;
            this.blockEntity.setChanged();
            this.completeRecipeIfReady();
        }
    }

    @Override
    public void onLoad() {
        if (this.blockEntity.getLevel() instanceof ServerLevel serverLevel) {
            this.resolvePendingRecipe(serverLevel);
        }
    }

    public final float progressFraction() {
        RecipeHolder<MachineRecipe> recipe = this.processingRecipe;
        if (recipe == null) {
            return 0.0F;
        }
        return Math.min(1.0F, this.currentProgress / (float) this.recipeDuration(recipe));
    }

    public final boolean tryAddManualEnergy(long amount) {
        if (amount <= 0L || !this.canProceedWithManualEnergy()) {
            return false;
        }
        try (Transaction transaction = Transaction.openRoot()) {
            if (this.blockEntity.energyHolder().insert(amount, transaction) != amount) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    private boolean canProceedWithManualEnergy() {
        RecipeHolder<MachineRecipe> recipe = this.processingRecipe;
        if (recipe != null) {
            return TransferUtils.canInsertAllItems(
                    this.blockEntity.getOutputInventory(),
                    recipe.value().createOutputs(),
                    null);
        }
        if (!(this.blockEntity.getLevel() instanceof ServerLevel serverLevel)) {
            return false;
        }
        recipe = this.matchedRecipe;
        if (recipe == null) {
            recipe = this.findMatchedRecipe(serverLevel);
            this.matchedRecipe = recipe;
            this.recipeSearchRequired = false;
        }
        return recipe != null && TransferUtils.canInsertAllItems(
                this.blockEntity.getOutputInventory(),
                recipe.value().createOutputs(),
                null);
    }

    private @Nullable RecipeHolder<MachineRecipe> findMatchedRecipe(ServerLevel level) {
        MachineRecipeInput input = this.createRecipeInput();
        for (RecipeHolder<MachineRecipe> holder : level.recipeAccess().recipeMap().byType(this.recipeType)) {
            if (holder.value().consumptionPlan(input, this.blockEntity.tier()).isPresent()) {
                return holder;
            }
        }
        return null;
    }

    private boolean prepareRecipe(RecipeHolder<MachineRecipe> recipe) {
        MachineRecipeInput input = this.createRecipeInput();
        var plan = recipe.value().consumptionPlan(input, this.blockEntity.tier());
        if (plan.isEmpty()) {
            return false;
        }
        List<Integer> consumption = plan.get();

        List<ItemStack> outputs = recipe.value().createOutputs();
        if (!TransferUtils.canInsertAllItems(this.blockEntity.getOutputInventory(), outputs, null)) {
            return false;
        }

        ResourceHandler<ItemResource> inputInventory = this.blockEntity.getInputInventory();
        try (Transaction transaction = Transaction.openRoot()) {
            for (int index = 0; index < consumption.size(); index++) {
                int amount = consumption.get(index);
                if (amount == 0) {
                    continue;
                }
                ItemResource resource = inputInventory.getResource(index);
                if (resource.isEmpty() || inputInventory.extract(index, resource, amount, transaction) != amount) {
                    return false;
                }
            }
            if (this.blockEntity.energyHolder().extract(recipe.value().cePerTick(), transaction) != recipe.value()
                    .cePerTick()) {
                return false;
            }
            transaction.commit();
        }

        this.processingRecipe = recipe;
        this.currentProgress = 1L;
        this.blockEntity.setChanged();
        return true;
    }

    private boolean drawEnergy(long amount) {
        try (Transaction transaction = Transaction.openRoot()) {
            if (this.blockEntity.energyHolder().extract(amount, transaction) != amount) {
                return false;
            }
            transaction.commit();
            return true;
        }
    }

    private void completeRecipeIfReady() {
        RecipeHolder<MachineRecipe> recipe = this.processingRecipe;
        if (recipe == null || this.currentProgress < this.recipeDuration(recipe)) {
            return;
        }
        if (!TransferUtils.insertAllItems(
                this.blockEntity.getOutputInventory(),
                recipe.value().createOutputs(),
                null)) {
            return;
        }
        this.processingRecipe = null;
        this.currentProgress = 0L;
        this.blockEntity.setChanged();
    }

    private long recipeDuration(RecipeHolder<MachineRecipe> recipe) {
        return Math.max(1L, recipe.value().adjustedDuration());
    }

    private MachineRecipeInput createRecipeInput() {
        ResourceHandler<ItemResource> inventory = this.blockEntity.getInputInventory();
        List<ItemStack> stacks = new ArrayList<>(inventory.size());
        for (int index = 0; index < inventory.size(); index++) {
            stacks.add(inventory.getResource(index).toStack(inventory.getAmountAsInt(index)));
        }
        return new MachineRecipeInput(stacks);
    }

    @Override
    public void notifyItemInputInventoryChanged() {
        this.matchedRecipe = null;
        this.recipeSearchRequired = true;
    }

    private void resolvePendingRecipe(ServerLevel level) {
        Identifier recipeId = this.pendingRecipeId;
        if (recipeId == null) {
            return;
        }
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, recipeId);
        RecipeHolder<?> holder = level.recipeAccess().byKey(key).orElse(null);
        if (holder != null && holder.value() instanceof MachineRecipe recipe && recipe.getType() == this.recipeType) {
            this.processingRecipe = new RecipeHolder<>(holder.id(), recipe);
        } else {
            this.currentProgress = 0L;
        }
        this.pendingRecipeId = null;
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        output.putLong(PROGRESS_KEY, this.currentProgress);
        if (this.processingRecipe != null) {
            output.store(RECIPE_ID_KEY, Identifier.CODEC, this.processingRecipe.id().identifier());
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        this.currentProgress = Math.max(0L, input.getLongOr(PROGRESS_KEY, 0L));
        this.processingRecipe = null;
        this.matchedRecipe = null;
        this.recipeSearchRequired = true;
        this.pendingRecipeId = input.read(RECIPE_ID_KEY, Identifier.CODEC).orElse(null);
        if (this.pendingRecipeId == null) {
            this.currentProgress = 0L;
        }
    }
}
