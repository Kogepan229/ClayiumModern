package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.OverclockHandler;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.LargeItemSlot;
import net.kogepan.clayium.client.ldlib.elements.ProgressArrow;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractClayFabricatorBlockEntity extends ClayContainerBlockEntity {

    private static final String TAG_PROCESSING = "processingStack";
    private static final String TAG_CRAFT_PROGRESS = "craftProgress";
    private static final String TAG_CRAFT_DURATION = "craftDuration";

    protected final ClayiumItemStackHandler inputInventory;
    protected final ClayiumItemStackHandler outputInventory;
    protected final OverclockHandler overclockHandler;

    protected ItemStack processingStack = ItemStack.EMPTY;
    protected long craftProgress;
    protected long craftDuration;
    protected long displayCraftEnergy;

    protected AbstractClayFabricatorBlockEntity(@NotNull BlockEntityType<?> type, @NotNull BlockPos pos,
                                                @NotNull BlockState blockState) {
        super(type, pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.ALL),
                List.of(MachineIOMode.NONE, MachineIOMode.ALL));

        this.inputInventory = new ClayiumItemStackHandler(this, 1) {

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return AbstractClayFabricatorBlockEntity.this.isValidInput(stack);
            }
        };
        this.outputInventory = new ClayiumItemStackHandler(this, 1);

        this.overclockHandler = new OverclockHandler(this);
        this.addTrait(this.overclockHandler);
        this.addTrait(new AutoIOTrait.Combined(this, this.tier, false));
    }

    protected abstract boolean isValidInput(@NotNull ItemStack stack);

    protected abstract void tryStartCraft();

    protected abstract void advanceCraft();

    /**
     * Advances the active craft by up to the requested number of virtual ticks.
     *
     * <p>
     * The default implementation preserves per-tick hooks for external subclasses. Built-in
     * fabricators override this method to batch side-effect-free progress between craft
     * boundaries.
     *
     * @return the number of virtual ticks consumed
     */
    protected int advanceCrafts(int maxVirtualTicks) {
        for (int virtualTick = 0; virtualTick < maxVirtualTicks; virtualTick++) {
            this.advanceCraft();
            if (this.processingStack.isEmpty()) {
                return virtualTick + 1;
            }
        }
        return maxVirtualTicks;
    }

    protected boolean canCraftAtCurrentPosition() {
        return true;
    }

    @Override
    public void initDefaultRoutes() {
        this.inputModes.setMode(Direction.UP, MachineIOMode.ALL);
        this.outputModes.setMode(Direction.DOWN, MachineIOMode.ALL);
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
    public List<ItemStack> getInventoryContentsForDisplay() {
        List<ItemStack> contents = new ArrayList<>(super.getInventoryContentsForDisplay());
        if (!this.processingStack.isEmpty()) {
            contents.add(this.processingStack.copy());
        }
        return contents;
    }

    @Override
    public boolean hasProcessingProgress() {
        return !this.processingStack.isEmpty() && this.craftDuration > 0L;
    }

    @Override
    public float getProcessingProgress() {
        if (!this.hasProcessingProgress()) {
            return 0.0F;
        }
        return Math.clamp((float) this.craftProgress / this.craftDuration, 0.0F, 1.0F);
    }

    @Override
    protected void tick() {
        super.tick();
        Level level = this.level;
        if (level == null || level.isClientSide() || !this.canCraftAtCurrentPosition()) {
            return;
        }

        int remainingOperations = this.overclockHandler.getOperationsThisTick();
        while (remainingOperations > 0) {
            if (this.processingStack.isEmpty()) {
                this.tryStartCraft();
                remainingOperations--;
                if (this.processingStack.isEmpty()) {
                    break;
                }
            } else {
                int consumedOperations = this.advanceCrafts(remainingOperations);
                if (consumedOperations <= 0 || consumedOperations > remainingOperations) {
                    throw new IllegalStateException("Invalid number of consumed virtual ticks");
                }
                remainingOperations -= consumedOperations;
            }
        }
    }

    protected boolean canFitOutput(@NotNull ItemStack result) {
        return ItemHandlerHelper.insertItemStacked(this.outputInventory, result, true).isEmpty();
    }

    protected void startProcessing(@NotNull ItemStack stack, long duration) {
        this.processingStack = stack;
        this.craftProgress = 0L;
        this.craftDuration = this.overclockHandler.applyDuration(duration);
        this.displayCraftEnergy = 0L;
        this.setChanged();
    }

    protected void finishProcessing() {
        this.processingStack = ItemStack.EMPTY;
        this.craftProgress = 0L;
        this.craftDuration = 0L;
        this.displayCraftEnergy = 0L;
    }

    protected void abortProcessing(boolean returnToInput) {
        if (returnToInput && !this.processingStack.isEmpty()) {
            ItemStack remain = ItemHandlerHelper.insertItemStacked(this.inputInventory, this.processingStack.copy(),
                    false);
            if (!remain.isEmpty()) {
                Block.popResource(this.level, this.worldPosition, remain);
            }
        }
        this.finishProcessing();
        this.setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inputInventory", this.inputInventory.serializeNBT(provider));
        tag.put("outputInventory", this.outputInventory.serializeNBT(provider));
        this.saveFabricatorState(tag, provider);
    }

    private void saveFabricatorState(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (this.processingStack.isEmpty()) {
            return;
        }
        tag.put(TAG_PROCESSING, this.processingStack.save(provider));
        tag.putLong(TAG_CRAFT_PROGRESS, this.craftProgress);
        tag.putLong(TAG_CRAFT_DURATION, this.craftDuration);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inputInventory")) {
            this.inputInventory.deserializeNBT(provider, tag.getCompound("inputInventory"));
        }
        if (tag.contains("outputInventory")) {
            this.outputInventory.deserializeNBT(provider, tag.getCompound("outputInventory"));
        }
        this.loadFabricatorState(tag, provider);
    }

    private void loadFabricatorState(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        this.processingStack = tag.contains(TAG_PROCESSING, Tag.TAG_COMPOUND) ?
                ItemStack.parse(provider, tag.getCompound(TAG_PROCESSING)).orElse(ItemStack.EMPTY) :
                ItemStack.EMPTY;
        this.craftProgress = tag.getLong(TAG_CRAFT_PROGRESS);
        this.craftDuration = tag.getLong(TAG_CRAFT_DURATION);
        if (this.processingStack.isEmpty()) {
            this.craftProgress = 0L;
            this.craftDuration = 0L;
        }
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        UIElement row = new UIElement().layout(layout -> layout
                .marginTop(8)
                .marginBottom(4)
                .flexDirection(FlexDirection.ROW)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER)
                .gapColumn(8));
        root.addChild(row);
        row.addChild(new LargeItemSlot().itemSlot(
                slot -> slot.bind(new ItemHandlerSlot(this.inputInventory, 0))));
        row.addChild(new ProgressArrow()
                .bind(DataBindingBuilder
                        .floatValS2C(this::getProcessingProgress)
                        .build())
                .layout(layout -> layout.width(22)));
        row.addChild(new LargeItemSlot().itemSlot(slot -> slot.bind(
                new ItemHandlerSlot(this.outputInventory, 0).setCanPlace(stack -> false))));

        var progressLabel = new CLabel().bindDataSource(SupplierDataSource.of(
                () -> Component.literal(this.formatProgressText())));
        progressLabel.addChild(new BindableValue<Long>()
                .bind(DataBindingBuilder.longValS2C(() -> this.displayCraftEnergy)
                        .remoteSetter(v -> this.displayCraftEnergy = v)
                        .build()));
        progressLabel.addChild(new BindableValue<Long>()
                .bind(DataBindingBuilder.longValS2C(() -> this.craftProgress)
                        .remoteSetter(v -> this.craftProgress = v)
                        .build()));
        progressLabel.addChild(new BindableValue<Long>()
                .bind(DataBindingBuilder.longValS2C(() -> this.craftDuration)
                        .remoteSetter(v -> this.craftDuration = v)
                        .build()));
        root.addChild(progressLabel);
        root.addChild(this.overclockHandler.createFactorUIElement());
    }

    private String formatProgressText() {
        if (this.craftDuration <= 0L) {
            return CEUtils.formatCE(this.displayCraftEnergy);
        }
        int pct = (int) (100L * this.craftProgress / this.craftDuration);
        return CEUtils.formatCE(this.displayCraftEnergy) + " (" + pct + "%)";
    }
}
