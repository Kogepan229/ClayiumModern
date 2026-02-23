package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.AutoIOTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.capability.IClayEnergyHolder;
import net.kogepan.clayium.capability.ISynchronizedInterface;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.client.model.block.ClayContainerModel;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Clay Interface block entity.
 * Proxies item and CE capabilities to a linked ClayContainer target.
 */
public class ClayInterfaceBlockEntity extends ClayContainerBlockEntity implements ISynchronizedInterface {

    private static final String LINKED_TARGET_TAG = "linkedTarget";
    private static final String LINK_SOURCE_TAG = "linkSource";
    private static final String TARGET_VALID_TAG = "targetValid";
    private static final int VALIDATION_INTERVAL = 20;
    private static final MachineIOModes HIDDEN_IO_MODES = new MachineIOModes();

    private static final IItemHandlerModifiable EMPTY_MODIFIABLE_ITEM_HANDLER = new ItemStackHandler(0);
    private static final IClayEnergyHolder EMPTY_ENERGY_HOLDER = new IClayEnergyHolder() {

        @Override
        public @NotNull IItemHandler getEnergizedClayItemHandler() {
            return EmptyItemHandler.INSTANCE;
        }

        @Override
        public long getEnergyStored() {
            return 0;
        }

        @Override
        public boolean drawEnergy(long amount, boolean simulate) {
            return false;
        }

        @Override
        public void addEnergy(long amount) {}

        @Override
        public boolean hasEnoughEnergy(long amount) {
            return false;
        }
    };

    @Nullable
    private GlobalPos linkedTargetPos;
    @NotNull
    private LinkSource linkSource = LinkSource.NONE;
    private int validationTimer;
    private boolean targetValid = false;
    @Nullable
    private ClayContainerBlockEntity linkedTarget;
    // NeoForge stores capability listeners as weak references, so keep a strong reference here.
    @Nullable
    private ICapabilityInvalidationListener targetCapabilityInvalidationListener;

    public ClayInterfaceBlockEntity(@NotNull BlockPos pos,
                                    @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_INTERFACE_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.ALL, MachineIOMode.CE),
                List.of(MachineIOMode.NONE, MachineIOMode.ALL));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level instanceof ServerLevel) {
            this.resolveTarget(this.linkedTargetPos);
        }
    }

    @Override
    protected void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        this.validationTimer++;
        if (this.validationTimer >= VALIDATION_INTERVAL) {
            this.validationTimer = 0;
            this.resolveTarget(this.linkedTargetPos);
        }
    }

    @Override
    public @NotNull IItemHandlerModifiable getInputInventory() {
        TargetResolution resolution = this.resolveTarget(this.linkedTargetPos);
        if (resolution.state() == TargetState.VALID && resolution.target() != null) {
            return resolution.target().getInputInventory();
        }
        return EMPTY_MODIFIABLE_ITEM_HANDLER;
    }

    @Override
    public @NotNull IItemHandlerModifiable getOutputInventory() {
        TargetResolution resolution = this.resolveTarget(this.linkedTargetPos);
        if (resolution.state() == TargetState.VALID && resolution.target() != null) {
            return resolution.target().getOutputInventory();
        }
        return EMPTY_MODIFIABLE_ITEM_HANDLER;
    }

    @Override
    public @Nullable IClayEnergyHolder getExposedClayEnergyHolder(@Nullable Direction side) {
        TargetResolution resolution = this.resolveTarget(this.linkedTargetPos);
        if (resolution.state() == TargetState.VALID && resolution.target() != null) {
            IClayEnergyHolder holder = resolution.target().getExposedClayEnergyHolder(side);
            if (holder != null) {
                return holder;
            }
        }
        return EMPTY_ENERGY_HOLDER;
    }

    @Override
    public void cycleInputMode(@NotNull Direction direction) {
        if (!this.hasValidTargetForModeChange()) {
            return;
        }
        super.cycleInputMode(direction);
    }

    @Override
    public void cycleOutputMode(@NotNull Direction direction) {
        if (!this.hasValidTargetForModeChange()) {
            return;
        }
        super.cycleOutputMode(direction);
    }

    @Override
    public @Nullable GlobalPos getLinkedTargetPos() {
        return this.linkedTargetPos;
    }

    @Override
    public @NotNull LinkSource getLinkSource() {
        return this.linkSource;
    }

    @Override
    public boolean setLinkedTarget(@NotNull GlobalPos target, @NotNull LinkSource source) {
        if (this.isSelfTarget(target)) {
            return false;
        }

        boolean linkStateChanged = !target.equals(this.linkedTargetPos) || this.linkSource != source;
        this.linkedTargetPos = target;
        this.linkSource = source;
        if (linkStateChanged) {
            this.markLinkStateChanged();
        }
        this.resolveTarget(target);
        return true;
    }

    @Override
    public void clearLinkedTarget() {
        if (this.linkedTargetPos == null && this.linkSource == LinkSource.NONE) {
            return;
        }

        this.linkedTargetPos = null;
        this.linkSource = LinkSource.NONE;
        this.markLinkStateChanged();
        this.resolveTarget(null);
    }

    public void clearMultiblockLinkTo(@NotNull GlobalPos controllerTarget) {
        if (this.linkSource == LinkSource.MULTIBLOCK && controllerTarget.equals(this.linkedTargetPos)) {
            this.clearLinkedTarget();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.linkedTargetPos != null) {
            GlobalPos.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.linkedTargetPos)
                    .result()
                    .ifPresent(encoded -> tag.put(LINKED_TARGET_TAG, encoded));
            tag.putString(LINK_SOURCE_TAG, this.linkSource.name());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);

        this.linkedTargetPos = null;
        this.linkSource = LinkSource.NONE;
        if (tag.contains(LINKED_TARGET_TAG)) {
            this.linkedTargetPos = GlobalPos.CODEC
                    .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(LINKED_TARGET_TAG))
                    .result()
                    .orElse(null);
            this.linkSource = readLinkSource(tag.getString(LINK_SOURCE_TAG));
        }

        if (this.linkedTargetPos == null) {
            this.linkSource = LinkSource.NONE;
        }
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean(TARGET_VALID_TAG, this.targetValid);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.onReceivePacket(tag, provider);
        if (tag.contains(TARGET_VALID_TAG)) {
            this.targetValid = tag.getBoolean(TARGET_VALID_TAG);
        }
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        return this.createUI(holder, this.linkedTargetPos);
    }

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder, @Nullable GlobalPos target) {
        TargetResolution resolution = this.resolveTargetForUI(target);
        if (resolution.state() == TargetState.VALID && resolution.target() != null) {
            ClayContainerBlockEntity targetContainer = resolution.target();
            BlockState targetState = targetContainer.getBlockState();
            if (targetState.getBlock() instanceof BlockUIMenuType.BlockUI blockUI) {
                BlockUIMenuType.BlockUIHolder targetHolder = new BlockUIMenuType.BlockUIHolder(
                        blockUI,
                        holder.player,
                        targetContainer.getBlockPos(),
                        targetState);
                return targetContainer.createUI(targetHolder);
            }
        }
        return super.createUI(holder);
    }

    @Override
    @NotNull
    public ModelData getModelData() {
        ModelData baseData = super.getModelData();
        if (this.targetValid) {
            return baseData;
        }

        boolean[] filterSides = baseData.get(ClayContainerModel.MODEL_DATA_FILTER_SIDES);
        return baseData.derive()
                .with(ClayContainerModel.MODEL_DATA_IMPORT, HIDDEN_IO_MODES)
                .with(ClayContainerModel.MODEL_DATA_EXPORT, HIDDEN_IO_MODES)
                .with(ClayContainerModel.MODEL_DATA_FILTER_SIDES, filterSides != null ? filterSides : new boolean[6])
                .build();
    }

    private boolean isSelfTarget(@NotNull GlobalPos target) {
        if (this.level == null) {
            return false;
        }
        return this.level.dimension().equals(target.dimension()) && this.worldPosition.equals(target.pos());
    }

    @NotNull
    private TargetResolution resolveTargetForUI(@Nullable GlobalPos target) {
        Level level = this.level;
        if (target == null) {
            return TargetResolution.none();
        }
        if (level == null) {
            return TargetResolution.unloaded();
        }
        if (!level.dimension().equals(target.dimension()) || !level.isLoaded(target.pos())) {
            return TargetResolution.unloaded();
        }

        BlockEntity blockEntity = level.getBlockEntity(target.pos());
        if (blockEntity instanceof ClayContainerBlockEntity targetContainer &&
                !(targetContainer instanceof ClayInterfaceBlockEntity)) {
            return TargetResolution.valid(targetContainer);
        }
        return TargetResolution.invalid();
    }

    @NotNull
    private TargetResolution resolveTarget(@Nullable GlobalPos target) {
        if (target == null) {
            TargetResolution result = TargetResolution.none();
            refreshLinkStatus(result);
            return result;
        }

        Level level = this.level;
        if (!(level instanceof ServerLevel serverLevel)) {
            TargetResolution result = TargetResolution.unloaded();
            refreshLinkStatus(result);
            return result;
        }

        MinecraftServer server = serverLevel.getServer();
        ServerLevel targetLevel = server.getLevel(target.dimension());
        if (targetLevel == null || !targetLevel.isLoaded(target.pos())) {
            TargetResolution result = TargetResolution.unloaded();
            refreshLinkStatus(result);
            return result;
        }

        BlockEntity blockEntity = targetLevel.getBlockEntity(target.pos());
        if (blockEntity instanceof ClayContainerBlockEntity targetContainer &&
                !(targetContainer instanceof ClayInterfaceBlockEntity)) {
            TargetResolution result = TargetResolution.valid(targetContainer);
            refreshLinkStatus(result);
            return result;
        }

        TargetResolution result = TargetResolution.invalid();
        refreshLinkStatus(result);
        return result;
    }

    private void refreshLinkStatus(@NotNull TargetResolution resolution) {
        boolean valid = resolution.state() == TargetState.VALID;
        ClayContainerBlockEntity resolvedTarget = resolution.target();
        boolean validChanged = this.targetValid != valid;
        boolean targetEntityChanged = this.linkedTarget != resolvedTarget;

        if (validChanged || targetEntityChanged) {
            this.syncAutoIOTrait(resolution);
            this.invalidateCapabilities();
            this.linkedTarget = resolvedTarget;
        }

        if (targetEntityChanged) {
            this.updateTargetCapabilityListener(resolvedTarget);
        }

        if (targetEntityChanged && resolvedTarget != null) {
            this.resetIOModesToNoneIfUnsupported(resolvedTarget);
        }

        if (!validChanged) {
            return;
        }
        this.targetValid = valid;
        this.markLinkStateChanged();
    }

    private void resetIOModesToNoneIfUnsupported(@NotNull ClayContainerBlockEntity targetContainer) {
        if (!this.hasUnsupportedIOModeForTarget(targetContainer)) {
            return;
        }

        boolean changed = false;
        for (Direction direction : Direction.values()) {
            if (this.inputModes.getMode(direction) != MachineIOMode.NONE) {
                this.inputModes.setMode(direction, MachineIOMode.NONE);
                changed = true;
            }
            if (this.outputModes.getMode(direction) != MachineIOMode.NONE) {
                this.outputModes.setMode(direction, MachineIOMode.NONE);
                changed = true;
            }
        }

        if (!changed) {
            return;
        }

        this.invalidateItemHandlerCapability();
        this.markLinkStateChanged();
    }

    private boolean hasUnsupportedIOModeForTarget(@NotNull ClayContainerBlockEntity targetContainer) {
        for (Direction direction : Direction.values()) {
            if (!targetContainer.validInputModes.contains(this.inputModes.getMode(direction))) {
                return true;
            }
            if (!targetContainer.validOutputModes.contains(this.outputModes.getMode(direction))) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    private static LinkSource readLinkSource(@NotNull String name) {
        if (name.isEmpty()) {
            return LinkSource.MANUAL;
        }
        try {
            return LinkSource.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return LinkSource.MANUAL;
        }
    }

    public boolean hasValidTargetForModeChange() {
        if (this.level instanceof ServerLevel) {
            return this.resolveTarget(this.linkedTargetPos).state() == TargetState.VALID;
        }
        return this.targetValid;
    }

    private void markLinkStateChanged() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    private void syncAutoIOTrait(@NotNull TargetResolution resolution) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }

        boolean hasAutoIOTrait = this.getTrait(AutoIOTrait.TRAIT_ID) != null;
        if (hasAutoIOTrait) {
            this.removeTrait(AutoIOTrait.TRAIT_ID);
            this.setChanged();
        }

        boolean hasAutoIOECTrait = this.getTrait(AutoIOTrait.TRAIT_ID_EC) != null;
        if (hasAutoIOECTrait) {
            this.removeTrait(AutoIOTrait.TRAIT_ID_EC);
            this.setChanged();
        }

        boolean hasValidTarget = resolution.state() == TargetState.VALID && resolution.target() != null;
        if (hasValidTarget) {
            AutoIOTrait ioHandler = (AutoIOTrait) resolution.target().getTrait(AutoIOTrait.TRAIT_ID);
            if (ioHandler != null) {
                this.addTrait(new AutoIOTrait.Combined(this, resolution.target().tier, ioHandler.isBuffer()));
                this.setChanged();
            }
            ClayEnergyHolder energyHolder = (ClayEnergyHolder) resolution.target().getTrait(ClayEnergyHolder.TRAIT_ID);
            if (energyHolder != null) {
                this.addTrait(new AutoIOTrait.EcImporter(this, energyHolder.getEnergizedClayItemHandler()));
                this.setChanged();
            }
        }
    }

    private void updateTargetCapabilityListener(@Nullable ClayContainerBlockEntity resolvedTarget) {
        this.targetCapabilityInvalidationListener = null;
        if (resolvedTarget == null) {
            return;
        }

        Level targetLevel = resolvedTarget.getLevel();
        if (!(targetLevel instanceof ServerLevel targetServerLevel)) {
            return;
        }

        // Capture the target at registration time so stale listeners can self-remove after relinking.
        GlobalPos expectedTarget = GlobalPos.of(targetServerLevel.dimension(), resolvedTarget.getBlockPos());
        ICapabilityInvalidationListener listener = () -> this.onTargetCapabilityInvalidated(expectedTarget);
        this.targetCapabilityInvalidationListener = listener;
        targetServerLevel.registerCapabilityListener(
                resolvedTarget.getBlockPos(),
                listener);
    }

    private boolean onTargetCapabilityInvalidated(@NotNull GlobalPos expectedTarget) {
        this.invalidateItemHandlerCapability();
        // Returning false removes this listener from the old target's listener list.
        return expectedTarget.equals(this.linkedTargetPos);
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(this::statusMessage).build()));
        root.addChild(new CLabel().bind(DataBindingBuilder.componentS2C(this::targetLocationMessage).build()));
    }

    @NotNull
    private Component statusMessage() {
        if (this.linkedTargetPos == null) {
            return Component.translatable("gui.clayium.clay_interface.no_target");
        }

        return switch (this.resolveTarget(this.linkedTargetPos).state()) {
            case VALID -> Component.translatable("gui.clayium.clay_interface.target_ready");
            case UNLOADED -> Component.translatable("gui.clayium.clay_interface.target_unloaded");
            case INVALID, NONE -> Component.translatable("gui.clayium.clay_interface.target_invalid");
        };
    }

    @NotNull
    private Component targetLocationMessage() {
        if (this.linkedTargetPos == null) {
            return Component.empty();
        }

        return Component.translatable("gui.clayium.clay_interface.target_pos",
                Component.translatable(this.linkedTargetPos.dimension().location().toLanguageKey("dimension")),
                this.linkedTargetPos.pos().getX(), this.linkedTargetPos.pos().getY(),
                this.linkedTargetPos.pos().getZ());
    }

    private enum TargetState {
        NONE,
        VALID,
        UNLOADED,
        INVALID
    }

    private record TargetResolution(@NotNull TargetState state, @Nullable ClayContainerBlockEntity target) {

        @NotNull
        static TargetResolution none() {
            return new TargetResolution(TargetState.NONE, null);
        }

        @NotNull
        static TargetResolution valid(@NotNull ClayContainerBlockEntity target) {
            return new TargetResolution(TargetState.VALID, target);
        }

        @NotNull
        static TargetResolution unloaded() {
            return new TargetResolution(TargetState.UNLOADED, null);
        }

        @NotNull
        static TargetResolution invalid() {
            return new TargetResolution(TargetState.INVALID, null);
        }
    }
}
