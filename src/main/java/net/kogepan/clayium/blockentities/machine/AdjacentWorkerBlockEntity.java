package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.ldlib.elements.MachineControlButton;
import net.kogepan.clayium.client.ldlib.elements.PhantomItemSlot;
import net.kogepan.clayium.client.ldlib.textures.ButtonTextures;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.slot.ItemHandlerSlot;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class AdjacentWorkerBlockEntity extends ClayContainerBlockEntity {

    public static final int INVENTORY_SIZE = 9;
    public static final int PROGRESS_PER_TICK = 100;
    public static final String ACTIVE_OVERLAY_VARIANT = "active";

    protected final ClayiumItemStackHandler itemInventory = new ClayiumItemStackHandler(this, INVENTORY_SIZE);
    protected final ClayiumItemStackHandler blockFilterInventory = new ClayiumItemStackHandler(this, 1) {

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.isEmpty() || stack.getItem() instanceof BlockItem ||
                    stack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA) != null;
        }
    };

    protected int progress;
    protected WorkSchedule workSchedule = WorkSchedule.CONTINUOUS;
    protected TargetDisplayMode targetDisplayMode = TargetDisplayMode.OFF;

    private boolean activeForRender = true;

    protected AdjacentWorkerBlockEntity(@NotNull BlockEntityType<?> type, @NotNull BlockPos pos,
                                        @NotNull BlockState blockState,
                                        @NotNull List<MachineIOMode> validInputModes,
                                        @NotNull List<MachineIOMode> validOutputModes) {
        super(type, pos, blockState, validInputModes, validOutputModes);
    }

    @Override
    protected void tick() {
        super.tick();
        if (!(this.level instanceof ServerLevel serverLevel) ||
                this.workSchedule == WorkSchedule.STOPPED) {
            return;
        }
        this.tickWorker(serverLevel);
    }

    protected abstract void tickWorker(@NotNull ServerLevel level);

    protected void completeJob() {
        this.progress = 0;
        if (this.workSchedule == WorkSchedule.RUN_ONCE) {
            this.setWorkSchedule(WorkSchedule.STOPPED);
        } else {
            this.setChanged();
        }
    }

    protected void setWorkSchedule(@NotNull WorkSchedule schedule) {
        boolean wasActive = this.workSchedule != WorkSchedule.STOPPED;
        this.workSchedule = schedule;
        boolean isActive = schedule != WorkSchedule.STOPPED;
        this.activeForRender = isActive;
        this.setChanged();
        if (wasActive != isActive) {
            this.syncRenderState();
        }
    }

    protected void cycleTargetDisplayMode() {
        this.targetDisplayMode = this.targetDisplayMode.next();
        this.setChanged();
        this.syncRenderState();
    }

    private void syncRenderState() {
        Level level = this.level;
        if (level != null && !level.isClientSide()) {
            BlockState state = this.getBlockState();
            level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @NotNull
    public Direction getWorkDirection() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ClayContainerBlock containerBlock) {
            return state.getValue(containerBlock.getFacingProperty()).getOpposite();
        }
        return Direction.SOUTH;
    }

    @NotNull
    public BlockPos getWorkPos() {
        return this.worldPosition.relative(this.getWorkDirection());
    }

    @NotNull
    public TargetDisplayMode getTargetDisplayMode() {
        return this.targetDisplayMode;
    }

    protected boolean matchesBlockFilter(@NotNull BlockState state) {
        ItemStack filterStack = this.blockFilterInventory.getStackInSlot(0);
        if (filterStack.isEmpty()) {
            return true;
        }
        ItemStack blockStack = new ItemStack(state.getBlock().asItem());
        if (blockStack.isEmpty()) {
            return false;
        }
        if (ItemStack.isSameItemSameComponents(filterStack, blockStack)) {
            return true;
        }
        ItemFilterData filter = filterStack.getCapability(ClayiumCapabilities.ITEM_FILTER_DATA);
        return filter != null && filter.test(blockStack);
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.itemInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.itemInventory;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("itemInventory", this.itemInventory.serializeNBT(provider));
        tag.put("blockFilterInventory", this.blockFilterInventory.serializeNBT(provider));
        tag.putInt("progress", this.progress);
        tag.putByte("workSchedule", (byte) this.workSchedule.ordinal());
        tag.putByte("targetDisplayMode", (byte) this.targetDisplayMode.ordinal());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("itemInventory")) {
            this.itemInventory.deserializeNBT(provider, tag.getCompound("itemInventory"));
        }
        if (tag.contains("blockFilterInventory")) {
            this.blockFilterInventory.deserializeNBT(provider, tag.getCompound("blockFilterInventory"));
        }
        this.progress = tag.getInt("progress");
        if (tag.contains("workSchedule")) {
            this.workSchedule = WorkSchedule.byOrdinal(tag.getByte("workSchedule"));
        }
        if (tag.contains("targetDisplayMode")) {
            this.targetDisplayMode = TargetDisplayMode.byOrdinal(tag.getByte("targetDisplayMode"));
        }
        this.activeForRender = this.workSchedule != WorkSchedule.STOPPED;
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("workerActive", this.workSchedule != WorkSchedule.STOPPED);
        tag.putByte("targetDisplayMode", (byte) this.targetDisplayMode.ordinal());
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.contains("workerActive")) {
            this.activeForRender = tag.getBoolean("workerActive");
        }
        if (tag.contains("targetDisplayMode")) {
            this.targetDisplayMode = TargetDisplayMode.byOrdinal(tag.getByte("targetDisplayMode"));
        }
        super.onReceivePacket(tag, provider);
    }

    @Override
    @Nullable
    protected String getFrontOverlayVariant() {
        return this.activeForRender ? ACTIVE_OVERLAY_VARIANT : null;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.progress)
                .remoteSetter(value -> this.progress = value).build()));
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(
                () -> this.workSchedule.ordinal())
                .remoteSetter(value -> this.workSchedule = WorkSchedule.byOrdinal(value)).build()));
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(
                () -> this.targetDisplayMode.ordinal())
                .remoteSetter(value -> this.targetDisplayMode = TargetDisplayMode.byOrdinal(value)).build()));

        UIElement controls = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .justifyContent(AlignContent.CENTER)
                .gapAll(2));
        controls.addChild(this.createScheduleButton(ButtonTextures.RUN_ONCE, WorkSchedule.RUN_ONCE,
                "gui.clayium.worker.run_once"));
        controls.addChild(this.createScheduleButton(ButtonTextures.STOP, WorkSchedule.STOPPED,
                "gui.clayium.worker.stop"));
        controls.addChild(this.createScheduleButton(ButtonTextures.CONTINUOUS, WorkSchedule.CONTINUOUS,
                "gui.clayium.worker.continuous"));
        MachineControlButton displayButton = new MachineControlButton(ButtonTextures.TARGET_DISPLAY);
        displayButton.style(style -> style.tooltips(Component.translatable("gui.clayium.worker.target_display")));
        displayButton.addServerEventListener(UIEvents.CLICK, event -> this.cycleTargetDisplayMode());
        controls.addChild(displayButton);
        root.addChild(controls);

        root.addChild(new CLabel().bindDataSource(
                SupplierDataSource.of(() -> Component.translatable("gui.clayium.worker.progress", this.progress))));

        UIElement inventoryAndFilter = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .justifyContent(AlignContent.CENTER)
                .gapAll(4));
        UIElement slots = new UIElement();
        for (int row = 0; row < 3; row++) {
            UIElement slotRow = new UIElement().layout(layout -> layout.flexDirection(FlexDirection.ROW));
            for (int column = 0; column < 3; column++) {
                int slot = row * 3 + column;
                slotRow.addChild(new ItemSlot().bind(new ItemHandlerSlot(this.itemInventory, slot)));
            }
            slots.addChild(slotRow);
        }
        inventoryAndFilter.addChild(slots);
        ItemSlot filterSlot = PhantomItemSlot.create(this.blockFilterInventory, 0);
        filterSlot.style(style -> style.tooltips(Component.translatable("gui.clayium.worker.block_filter")));
        inventoryAndFilter.addChild(filterSlot);
        root.addChild(inventoryAndFilter);

        this.createWorkerSettingsUI(holder, root);
    }

    protected void createWorkerSettingsUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {}

    protected MachineControlButton createSelectionButton(ButtonTextures.ButtonTexture texture,
                                                         BooleanSupplier enabled,
                                                         String tooltipKey,
                                                         Runnable serverAction) {
        MachineControlButton button = new MachineControlButton(texture);
        button.bind(DataBindingBuilder.boolS2C(enabled::getAsBoolean).build());
        button.style(style -> style.tooltips(Component.translatable(tooltipKey)));
        button.addServerEventListener(UIEvents.CLICK, event -> serverAction.run());
        return button;
    }

    private MachineControlButton createScheduleButton(ButtonTextures.ButtonTexture texture,
                                                      WorkSchedule schedule,
                                                      String tooltipKey) {
        return this.createSelectionButton(texture, () -> this.workSchedule != schedule, tooltipKey,
                () -> this.setWorkSchedule(schedule));
    }

    public enum WorkSchedule {

        RUN_ONCE,
        STOPPED,
        CONTINUOUS;

        @NotNull
        private static WorkSchedule byOrdinal(int ordinal) {
            WorkSchedule[] values = values();
            return values[Math.floorMod(ordinal, values.length)];
        }
    }

    public enum TargetDisplayMode {

        OFF,
        DEPTH_TESTED,
        XRAY;

        @NotNull
        private static TargetDisplayMode byOrdinal(int ordinal) {
            TargetDisplayMode[] values = values();
            return values[Math.floorMod(ordinal, values.length)];
        }

        @NotNull
        private TargetDisplayMode next() {
            return byOrdinal(this.ordinal() + 1);
        }
    }
}
