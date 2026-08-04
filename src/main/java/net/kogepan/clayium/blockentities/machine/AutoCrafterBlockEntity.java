package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;
import net.kogepan.clayium.client.ldlib.textures.SlotTextures;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.kogepan.clayium.registries.ClayiumBlockEntityTypes.AUTO_CRAFTER_BLOCK_ENTITY;

public class AutoCrafterBlockEntity extends ClayContainerBlockEntity {

    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;
    private static final int GRID_SLOTS = GRID_WIDTH * GRID_HEIGHT;
    private static final int OUTPUT_COLUMNS = 2;
    private static final int OUTPUT_SLOTS = OUTPUT_COLUMNS * GRID_HEIGHT;
    private static final int MIN_TIER = 5;
    private static final int MAX_TIER = 9;

    private static final int[] REQUIRED_PROGRESS = { 20, 1, 1, 1, 1 };
    private static final int[] CRAFTS_PER_TICK = { 1, 1, 16, 64, 256 };
    private static final long[] ENERGY_PER_PROGRESS = {
            0,
            CEUtils.TEN_MICRO_CE * 10,
            CEUtils.TEN_MICRO_CE * 40,
            CEUtils.TEN_MICRO_CE * 160,
            CEUtils.TEN_MICRO_CE * 640
    };
    private static final int[] AUTO_IO_INTERVAL = { 4, 2, 1, 1, 1 };
    private static final int[] AUTO_IO_LIMIT = { 4, 16, 1, 16, 576 };

    private static final String INPUT_INVENTORY_TAG = "inputInventory";
    private static final String OUTPUT_INVENTORY_TAG = "outputInventory";
    private static final String SAMPLE_INVENTORY_TAG = "sampleInventory";
    private static final String PROCESSING_INVENTORY_TAG = "processingInventory";
    private static final String REMAINDER_INVENTORY_TAG = "remainderInventory";
    private static final String PROGRESS_TAG = "progress";
    private static final String ACTIVE_RECIPE_TAG = "activeRecipe";
    private static final String ACTIVE_RECIPE_SNAPSHOT_TAG = "activeRecipeSnapshot";

    private final ClayiumItemStackHandler inputInventory;
    private final ClayiumItemStackHandler outputInventory;
    private final ClayiumItemStackHandler sampleInventory;
    private final ClayiumItemStackHandler processingInventory;
    private final ClayiumItemStackHandler remainderInventory;
    @Nullable
    private final ClayEnergyHolder energyHolder;
    private final RecipeManager.CachedCheck<CraftingInput, CraftingRecipe> recipeCheck = RecipeManager
            .createCheck(RecipeType.CRAFTING);

    private int progress;
    @Nullable
    private ResourceLocation activeRecipeId;
    @Nullable
    private Tag activeRecipeSnapshot;
    @Nullable
    private CraftingRecipe activeRecipe;
    @Nullable
    private CraftingRecipe snapshotRecipe;
    @Nullable
    private Tag cachedRecipeSnapshot;
    private boolean inputNeedsDistribution = true;

    public AutoCrafterBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(AUTO_CRAFTER_BLOCK_ENTITY.get(), pos, blockState, validInputModes(blockState),
                List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        if (this.tier < MIN_TIER || this.tier > MAX_TIER) {
            throw new IllegalArgumentException("Unsupported Auto Crafter tier: " + this.tier);
        }

        this.sampleInventory = new ClayiumItemStackHandler(this, GRID_SLOTS) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                AutoCrafterBlockEntity.this.inputNeedsDistribution = true;
            }
        };
        this.inputInventory = new ClayiumItemStackHandler(this, GRID_SLOTS) {

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return AutoCrafterBlockEntity.this.matchesSample(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                AutoCrafterBlockEntity.this.inputNeedsDistribution = true;
            }
        };
        this.outputInventory = new ClayiumItemStackHandler(this, OUTPUT_SLOTS);
        this.processingInventory = new ClayiumItemStackHandler(this, GRID_SLOTS);
        this.remainderInventory = new ClayiumItemStackHandler(this, GRID_SLOTS);

        int tierIndex = tierIndex();
        this.addTrait(new AutoIOTrait.Combined(this, this.tier, false,
                AUTO_IO_INTERVAL[tierIndex], AUTO_IO_INTERVAL[tierIndex],
                AUTO_IO_LIMIT[tierIndex], AUTO_IO_LIMIT[tierIndex]));
        if (this.tier > MIN_TIER) {
            this.energyHolder = new ClayEnergyHolder(this);
            this.addTrait(this.energyHolder);
        } else {
            this.energyHolder = null;
        }
    }

    private static List<MachineIOMode> validInputModes(BlockState blockState) {
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock && containerBlock.tier > MIN_TIER) {
            return List.of(MachineIOMode.NONE, MachineIOMode.ALL, MachineIOMode.CE);
        }
        return List.of(MachineIOMode.NONE, MachineIOMode.ALL);
    }

    private int tierIndex() {
        return this.tier - MIN_TIER;
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.ALL);
        if (this.tier > MIN_TIER && this.getBlockState().getBlock() instanceof ClayContainerBlock containerBlock) {
            this.inputModes.setMode(this.getBlockState().getValue(containerBlock.getFacingProperty()).getOpposite(),
                    MachineIOMode.CE);
        }
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.inputInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.outputInventory;
    }

    @Override
    @NotNull
    protected List<IItemHandler> getInventoryHandlersForDrops() {
        List<IItemHandler> inventories = new ArrayList<>(super.getInventoryHandlersForDrops());
        inventories.add(this.processingInventory);
        inventories.add(this.remainderInventory);
        return inventories;
    }

    @Override
    protected void tick() {
        super.tick();

        Level level = this.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (!drainRemainders()) {
            return;
        }
        distributeInputItems();

        int operations = CRAFTS_PER_TICK[tierIndex()];
        for (int operation = 0; operation < operations; operation++) {
            if (!runProgressStep(level)) {
                break;
            }
            if (!drainRemainders()) {
                break;
            }
            distributeInputItems();
        }
    }

    private boolean runProgressStep(Level level) {
        CraftPlan plan;
        if (isEmpty(this.processingInventory)) {
            if (this.progress != 0 || this.activeRecipeId != null) {
                resetProgress();
            }
            plan = findNewCraftPlan(level);
        } else {
            plan = findActiveCraftPlan(level);
            if (plan == null) {
                cancelProcessing();
                return false;
            }
        }

        if (plan == null || !canInsertResult(plan.result())) {
            return false;
        }
        if (!consumeEnergy()) {
            return false;
        }

        if (isEmpty(this.processingInventory)) {
            stageIngredients(plan.recipe(), plan.recipeSnapshot());
        }

        this.progress++;
        this.setChanged();
        if (this.progress >= REQUIRED_PROGRESS[tierIndex()]) {
            completeCraft(plan);
        }
        return true;
    }

    @Nullable
    private CraftPlan findNewCraftPlan(Level level) {
        if (!inputMatchesSample()) {
            return null;
        }
        CraftingInput.Positioned positioned = createCraftingInput(this.inputInventory);
        if (positioned.input().isEmpty()) {
            return null;
        }
        Optional<RecipeHolder<CraftingRecipe>> recipe = this.recipeCheck.getRecipeFor(positioned.input(), level);
        return recipe.map(holder -> createCraftPlan(level, holder, positioned)).orElse(null);
    }

    @Nullable
    private CraftPlan findActiveCraftPlan(Level level) {
        if (this.activeRecipeId == null) {
            return null;
        }
        CraftingInput.Positioned positioned = createCraftingInput(this.processingInventory);
        if (positioned.input().isEmpty()) {
            return null;
        }

        Optional<RecipeHolder<?>> recipeHolder = level.getRecipeManager().byKey(this.activeRecipeId);
        if (recipeHolder.isEmpty() || !(recipeHolder.get().value() instanceof CraftingRecipe recipe) ||
                !recipe.matches(positioned.input(), level)) {
            return null;
        }
        if (this.activeRecipeSnapshot == null) {
            return null;
        }
        if (this.activeRecipe != recipe) {
            Tag recipeSnapshot = getRecipeSnapshot(level, recipe);
            if (recipeSnapshot == null || !this.activeRecipeSnapshot.equals(recipeSnapshot)) {
                return null;
            }
            this.activeRecipe = recipe;
        }
        return createCraftPlan(level, new RecipeHolder<>(recipeHolder.get().id(), recipe), positioned,
                this.activeRecipeSnapshot);
    }

    @Nullable
    private CraftPlan createCraftPlan(Level level, RecipeHolder<CraftingRecipe> recipe,
                                      CraftingInput.Positioned positioned) {
        Tag recipeSnapshot = getRecipeSnapshot(level, recipe.value());
        return recipeSnapshot == null ? null : createCraftPlan(level, recipe, positioned, recipeSnapshot);
    }

    @Nullable
    private static CraftPlan createCraftPlan(Level level, RecipeHolder<CraftingRecipe> recipe,
                                             CraftingInput.Positioned positioned, Tag recipeSnapshot) {
        ItemStack result = recipe.value().assemble(positioned.input(), level.registryAccess());
        if (result.isEmpty() || !result.isItemEnabled(level.enabledFeatures())) {
            return null;
        }
        return new CraftPlan(recipe, positioned, result, recipeSnapshot);
    }

    @Nullable
    private Tag getRecipeSnapshot(Level level, CraftingRecipe recipe) {
        if (this.snapshotRecipe == recipe && this.cachedRecipeSnapshot != null) {
            return this.cachedRecipeSnapshot;
        }
        Tag recipeSnapshot = Recipe.CODEC
                .encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), recipe)
                .result()
                .orElse(null);
        this.snapshotRecipe = recipe;
        this.cachedRecipeSnapshot = recipeSnapshot;
        return recipeSnapshot;
    }

    private static CraftingInput.Positioned createCraftingInput(IItemHandler handler) {
        List<ItemStack> items = new ArrayList<>(GRID_SLOTS);
        for (int slot = 0; slot < GRID_SLOTS; slot++) {
            ItemStack stack = handler.getStackInSlot(slot).copy();
            if (!stack.isEmpty()) {
                stack.setCount(1);
            }
            items.add(stack);
        }
        return CraftingInput.ofPositioned(GRID_WIDTH, GRID_HEIGHT, items);
    }

    private boolean inputMatchesSample() {
        for (int slot = 0; slot < GRID_SLOTS; slot++) {
            ItemStack sample = this.sampleInventory.getStackInSlot(slot);
            ItemStack input = this.inputInventory.getStackInSlot(slot);
            if (sample.isEmpty() != input.isEmpty()) {
                return false;
            }
            if (!sample.isEmpty() && !matchesSample(slot, input)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesSample(int slot, ItemStack candidate) {
        if (candidate.isEmpty()) {
            return false;
        }
        ItemStack sample = this.sampleInventory.getStackInSlot(slot);
        if (sample.isEmpty()) {
            return false;
        }
        ItemFilterData filter = sample.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        return filter != null ? filter.test(candidate) : ItemStack.isSameItemSameComponents(sample, candidate);
    }

    private void distributeInputItems() {
        if (!this.inputNeedsDistribution) {
            return;
        }

        for (int sourceSlot = 0; sourceSlot < GRID_SLOTS; sourceSlot++) {
            while (this.inputInventory.getStackInSlot(sourceSlot).getCount() > 1) {
                ItemStack source = this.inputInventory.getStackInSlot(sourceSlot);
                int targetSlot = findDistributionTarget(sourceSlot, source);
                if (targetSlot < 0) {
                    break;
                }
                ItemStack moved = this.inputInventory.extractItem(sourceSlot, 1, false);
                this.inputInventory.setStackInSlot(targetSlot, moved);
            }
        }
        this.inputNeedsDistribution = false;
    }

    private int findDistributionTarget(int sourceSlot, ItemStack source) {
        for (int targetSlot = 0; targetSlot < GRID_SLOTS; targetSlot++) {
            if (targetSlot != sourceSlot && this.inputInventory.getStackInSlot(targetSlot).isEmpty() &&
                    matchesSample(targetSlot, source)) {
                return targetSlot;
            }
        }
        return -1;
    }

    private boolean canInsertResult(ItemStack result) {
        return ItemHandlerHelper.insertItemStacked(this.outputInventory, result, true).isEmpty();
    }

    private boolean consumeEnergy() {
        long amount = ENERGY_PER_PROGRESS[tierIndex()];
        if (amount == 0) {
            return true;
        }
        return this.energyHolder != null && this.energyHolder.drawEnergy(amount, false);
    }

    private void stageIngredients(RecipeHolder<CraftingRecipe> recipe, Tag recipeSnapshot) {
        for (int slot = 0; slot < GRID_SLOTS; slot++) {
            if (!this.inputInventory.getStackInSlot(slot).isEmpty()) {
                this.processingInventory.setStackInSlot(slot, this.inputInventory.extractItem(slot, 1, false));
            }
        }
        this.activeRecipeId = recipe.id();
        this.activeRecipeSnapshot = recipeSnapshot.copy();
        this.activeRecipe = recipe.value();
        this.setChanged();
    }

    private void completeCraft(CraftPlan plan) {
        ItemStack uninserted = ItemHandlerHelper.insertItemStacked(this.outputInventory, plan.result().copy(), false);
        if (!uninserted.isEmpty()) {
            return;
        }

        NonNullList<ItemStack> remaining = plan.recipe().value().getRemainingItems(plan.positioned().input());
        for (int slot = 0; slot < GRID_SLOTS; slot++) {
            this.processingInventory.setStackInSlot(slot, ItemStack.EMPTY);
        }
        for (int row = 0; row < plan.positioned().input().height(); row++) {
            for (int column = 0; column < plan.positioned().input().width(); column++) {
                int gridSlot = column + plan.positioned().left() +
                        (row + plan.positioned().top()) * GRID_WIDTH;
                int remainderSlot = column + row * plan.positioned().input().width();
                this.remainderInventory.setStackInSlot(gridSlot, remaining.get(remainderSlot));
            }
        }
        resetProgress();
    }

    private boolean drainRemainders() {
        boolean empty = true;
        for (int slot = 0; slot < GRID_SLOTS; slot++) {
            ItemStack stack = this.remainderInventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack uninserted = ItemHandlerHelper.insertItemStacked(this.outputInventory, stack.copy(), false);
            this.remainderInventory.setStackInSlot(slot, uninserted);
            if (!uninserted.isEmpty()) {
                empty = false;
            }
        }
        return empty;
    }

    private void cancelProcessing() {
        for (int slot = 0; slot < GRID_SLOTS; slot++) {
            ItemStack stack = this.processingInventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack uninserted = ItemHandlerHelper.insertItemStacked(this.remainderInventory, stack.copy(), false);
            this.processingInventory.setStackInSlot(slot, uninserted);
        }
        resetProgress();
    }

    private void resetProgress() {
        this.progress = 0;
        this.activeRecipeId = null;
        this.activeRecipeSnapshot = null;
        this.activeRecipe = null;
        this.setChanged();
    }

    private static boolean isEmpty(IItemHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            if (!handler.getStackInSlot(slot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(INPUT_INVENTORY_TAG, this.inputInventory.serializeNBT(provider));
        tag.put(OUTPUT_INVENTORY_TAG, this.outputInventory.serializeNBT(provider));
        tag.put(SAMPLE_INVENTORY_TAG, this.sampleInventory.serializeNBT(provider));
        tag.put(PROCESSING_INVENTORY_TAG, this.processingInventory.serializeNBT(provider));
        tag.put(REMAINDER_INVENTORY_TAG, this.remainderInventory.serializeNBT(provider));
        tag.putInt(PROGRESS_TAG, this.progress);
        if (this.activeRecipeId != null) {
            tag.putString(ACTIVE_RECIPE_TAG, this.activeRecipeId.toString());
        }
        if (this.activeRecipeSnapshot != null) {
            tag.put(ACTIVE_RECIPE_SNAPSHOT_TAG, this.activeRecipeSnapshot.copy());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(INPUT_INVENTORY_TAG)) {
            this.inputInventory.deserializeNBT(provider, tag.getCompound(INPUT_INVENTORY_TAG));
        }
        if (tag.contains(OUTPUT_INVENTORY_TAG)) {
            this.outputInventory.deserializeNBT(provider, tag.getCompound(OUTPUT_INVENTORY_TAG));
        }
        if (tag.contains(SAMPLE_INVENTORY_TAG)) {
            this.sampleInventory.deserializeNBT(provider, tag.getCompound(SAMPLE_INVENTORY_TAG));
        }
        if (tag.contains(PROCESSING_INVENTORY_TAG)) {
            this.processingInventory.deserializeNBT(provider, tag.getCompound(PROCESSING_INVENTORY_TAG));
        }
        if (tag.contains(REMAINDER_INVENTORY_TAG)) {
            this.remainderInventory.deserializeNBT(provider, tag.getCompound(REMAINDER_INVENTORY_TAG));
        }
        this.progress = Math.clamp(tag.getInt(PROGRESS_TAG), 0, REQUIRED_PROGRESS[tierIndex()] - 1);
        this.activeRecipeId = tag.contains(ACTIVE_RECIPE_TAG) ?
                ResourceLocation.tryParse(tag.getString(ACTIVE_RECIPE_TAG)) : null;
        this.activeRecipeSnapshot = tag.contains(ACTIVE_RECIPE_SNAPSHOT_TAG) ?
                tag.get(ACTIVE_RECIPE_SNAPSHOT_TAG).copy() : null;
        this.activeRecipe = null;
        this.snapshotRecipe = null;
        this.cachedRecipeSnapshot = null;
        this.inputNeedsDistribution = true;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement machineSection = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER)
                .marginVertical(8));
        root.addChild(machineSection);

        machineSection.addChild(createSampleGrid());
        machineSection.addChild(createInputGrid().layout(layout -> layout.marginLeft(8)));
        machineSection.addChild(createOutputGrid().layout(layout -> layout.marginLeft(8)));
        if (this.energyHolder != null) {
            root.addChild(this.energyHolder.createEnergyTextUIElement());
        }
    }

    private UIElement createSampleGrid() {
        UIElement grid = new UIElement();
        for (int row = 0; row < GRID_HEIGHT; row++) {
            UIElement rowElement = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
            grid.addChild(rowElement);
            for (int column = 0; column < GRID_WIDTH; column++) {
                int slot = column + row * GRID_WIDTH;
                rowElement.addChild(PhantomItemSlot.create(this.sampleInventory, slot).slotStyle(style -> style
                        .slotOverlay(SlotTextures.PHANTOM_ITEM_SLOT_OVERLAY)
                        .showSlotOverlayOnlyEmpty(true)));
            }
        }
        return grid;
    }

    private UIElement createInputGrid() {
        UIElement grid = new UIElement();
        for (int row = 0; row < GRID_HEIGHT; row++) {
            UIElement rowElement = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
            grid.addChild(rowElement);
            for (int column = 0; column < GRID_WIDTH; column++) {
                int slot = column + row * GRID_WIDTH;
                rowElement.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.inputInventory, slot)));
            }
        }
        return grid;
    }

    private UIElement createOutputGrid() {
        UIElement grid = new UIElement();
        for (int row = 0; row < GRID_HEIGHT; row++) {
            UIElement rowElement = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
            grid.addChild(rowElement);
            for (int column = 0; column < OUTPUT_COLUMNS; column++) {
                int slot = column + row * OUTPUT_COLUMNS;
                rowElement.addChild(new ItemSlot()
                        .bind(new ItemHandlerSlot(this.outputInventory, slot).setCanPlace(stack -> false)));
            }
        }
        return grid;
    }

    private record CraftPlan(RecipeHolder<CraftingRecipe> recipe, CraftingInput.Positioned positioned,
                             ItemStack result, Tag recipeSnapshot) {}
}
