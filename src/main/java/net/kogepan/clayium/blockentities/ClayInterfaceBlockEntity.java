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
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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

import java.util.Collections;
import java.util.List;

/**
 * Clay Interface block entity.
 * Proxies item and CE capabilities to a linked ClayContainer target.
 */
public class ClayInterfaceBlockEntity extends ClayContainerBlockEntity implements ISynchronizedInterface {

    private static final String LINKED_TARGET_TAG = "linkedTarget";
    private static final String LINKED_TARGET_PRESENT_TAG = "hasLinkedTarget";
    private static final String LINK_SOURCE_TAG = "linkSource";
    private static final String TARGET_VALID_TAG = "targetValid";
    private static final String DISPLAY_ITEM_STACK_TAG = "displayItemStack";
    private static final String CAN_SYNCHRONIZE_TAG = "canSynchronize";
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
    private boolean canSynchronize = false;
    private int validationTimer;
    private boolean targetValid = false;
    @Nullable
    private ClayContainerBlockEntity linkedTarget;
    @NotNull
    private ItemStack displayItemStack = ItemStack.EMPTY;
    // NeoForge stores capability listeners as weak references, so keep a strong reference here.
    @Nullable
    private ICapabilityInvalidationListener targetCapabilityInvalidationListener;

    public ClayInterfaceBlockEntity(@NotNull BlockPos pos,
                                    @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_INTERFACE_BLOCK_ENTITY.get(), pos, blockState,
                Collections.emptyList(), Collections.emptyList());
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
    @NotNull
    protected List<MachineIOMode> getCycleValidInputModes(@NotNull Direction direction) {
        ClayContainerBlockEntity targetContainer = this.resolveTargetForModeCycle();
        return targetContainer != null ? targetContainer.validInputModes : Collections.emptyList();
    }

    @Override
    @NotNull
    protected List<MachineIOMode> getCycleValidOutputModes(@NotNull Direction direction) {
        ClayContainerBlockEntity targetContainer = this.resolveTargetForModeCycle();
        return targetContainer != null ? targetContainer.validOutputModes : Collections.emptyList();
    }

    @Override
    public @Nullable GlobalPos getLinkedTargetPos() {
        return this.linkedTargetPos;
    }

    @NotNull
    public ItemStack getDisplayItemStack() {
        return this.displayItemStack;
    }

    @Override
    public @NotNull LinkSource getLinkSource() {
        return this.linkSource;
    }

    @Override
    public boolean canSynchronize() {
        return this.canSynchronize;
    }

    @Override
    public void setCanSynchronize(boolean canSynchronize) {
        if (this.canSynchronize == canSynchronize) {
            return;
        }
        this.canSynchronize = canSynchronize;
        this.setChanged();
    }

    @Override
    public boolean setLinkedTarget(@NotNull GlobalPos target, @NotNull LinkSource source) {
        if (source == LinkSource.MANUAL && !this.canSynchronize()) {
            return false;
        }
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
        tag.putBoolean(CAN_SYNCHRONIZE_TAG, this.canSynchronize);
        this.writeLinkedTargetTag(tag, provider, true);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.canSynchronize = tag.getBoolean(CAN_SYNCHRONIZE_TAG);
        this.readLinkedTargetTag(tag, provider, true);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.writeLinkedTargetTag(tag, provider, false);
        tag.putBoolean(TARGET_VALID_TAG, this.targetValid);
        this.writeDisplayItemStackTag(tag, provider);
        return tag;
    }

    @Override
    protected void onReceivePacket(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (tag.contains(LINKED_TARGET_PRESENT_TAG) || tag.contains(LINKED_TARGET_TAG)) {
            this.readLinkedTargetTag(tag, provider, false);
        }
        if (tag.contains(TARGET_VALID_TAG)) {
            this.targetValid = tag.getBoolean(TARGET_VALID_TAG);
        }
        this.readDisplayItemStackTag(tag, provider);
        super.onReceivePacket(tag, provider);
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

    private void writeLinkedTargetTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider,
                                      boolean includeLinkSource) {
        boolean hasLinkedTarget = this.linkedTargetPos != null;
        tag.putBoolean(LINKED_TARGET_PRESENT_TAG, hasLinkedTarget);
        if (!hasLinkedTarget) {
            return;
        }

        GlobalPos.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.linkedTargetPos)
                .result()
                .ifPresent(encoded -> tag.put(LINKED_TARGET_TAG, encoded));
        if (includeLinkSource) {
            tag.putString(LINK_SOURCE_TAG, this.linkSource.name());
        }
    }

    private void readLinkedTargetTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider,
                                     boolean includeLinkSource) {
        this.linkedTargetPos = null;
        if (includeLinkSource) {
            this.linkSource = LinkSource.NONE;
        }

        boolean hasLinkedTarget = tag.contains(LINKED_TARGET_PRESENT_TAG) ? tag.getBoolean(LINKED_TARGET_PRESENT_TAG) :
                tag.contains(LINKED_TARGET_TAG);
        if (!hasLinkedTarget || !tag.contains(LINKED_TARGET_TAG)) {
            this.linkSource = LinkSource.NONE;
            return;
        }

        this.linkedTargetPos = GlobalPos.CODEC
                .parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(LINKED_TARGET_TAG))
                .result()
                .orElse(null);
        if (includeLinkSource && this.linkedTargetPos != null) {
            this.linkSource = readLinkSource(tag.getString(LINK_SOURCE_TAG));
        }
    }

    private void writeDisplayItemStackTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ItemStack displayStack = this.createDisplayItemStackForClientSync();
        if (!displayStack.isEmpty()) {
            tag.put(DISPLAY_ITEM_STACK_TAG, displayStack.save(provider));
        }
    }

    private void readDisplayItemStackTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        this.displayItemStack = ItemStack.EMPTY;
        if (!tag.contains(DISPLAY_ITEM_STACK_TAG, Tag.TAG_COMPOUND)) {
            return;
        }
        this.displayItemStack = ItemStack.parse(provider, tag.get(DISPLAY_ITEM_STACK_TAG)).orElse(ItemStack.EMPTY);
    }

    @NotNull
    private ItemStack createDisplayItemStackForClientSync() {
        if (!(this.level instanceof ServerLevel serverLevel) || this.linkedTargetPos == null) {
            return ItemStack.EMPTY;
        }
        if (serverLevel.dimension().equals(this.linkedTargetPos.dimension())) {
            return ItemStack.EMPTY;
        }

        ServerLevel targetLevel = serverLevel.getServer().getLevel(this.linkedTargetPos.dimension());
        if (targetLevel == null || !targetLevel.isLoaded(this.linkedTargetPos.pos())) {
            return ItemStack.EMPTY;
        }

        BlockPos targetPos = this.linkedTargetPos.pos();
        BlockEntity targetBlockEntity = targetLevel.getBlockEntity(targetPos);
        if (!(targetBlockEntity instanceof ClayContainerBlockEntity) ||
                targetBlockEntity instanceof ClayInterfaceBlockEntity) {
            return ItemStack.EMPTY;
        }

        BlockState targetState = targetLevel.getBlockState(targetPos);
        ItemStack displayStack = targetState.getBlock().getCloneItemStack(targetLevel, targetPos, targetState);
        if (displayStack.isEmpty()) {
            displayStack = new ItemStack(targetState.getBlock());
        }
        return displayStack;
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        return this.createUI(holder, this.linkedTargetPos, null);
    }

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder, @Nullable GlobalPos target) {
        return this.createUI(holder, target, null);
    }

    /**
     * Builds the linked target UI when possible.
     * <p>
     * On the server we resolve the real target block entity (including other dimensions).
     * On the client, when the real target cannot be resolved locally, we may use a temporary proxy block entity
     * created from {@code targetState} to keep the rendered UI layout consistent with the server side.
     */
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder, @Nullable GlobalPos target,
                              @Nullable BlockState targetState) {
        TargetResolution resolution = this.resolveTargetForUI(target);
        ClayContainerBlockEntity targetContainer = resolution.target();
        if (resolution.state() != TargetState.VALID || targetContainer == null) {
            // Client fallback: build UI from serialized target state when the actual BE is not locally accessible.
            targetContainer = this.createClientTargetUIProxy(target, targetState);
        }

        if (targetContainer != null) {
            BlockState resolvedTargetState = targetContainer.getBlockState();
            if (resolvedTargetState.getBlock() instanceof BlockUIMenuType.BlockUI blockUI) {
                BlockUIMenuType.BlockUIHolder targetHolder = new BlockUIMenuType.BlockUIHolder(
                        blockUI,
                        holder.player,
                        targetContainer.getBlockPos(),
                        resolvedTargetState);
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
        if (target == null) {
            return TargetResolution.none();
        }
        Level level = this.level;
        if (level == null) {
            return TargetResolution.unloaded();
        }

        if (level instanceof ServerLevel serverLevel) {
            // Server can resolve linked targets across dimensions via the server level registry.
            MinecraftServer server = serverLevel.getServer();
            ServerLevel targetLevel = server.getLevel(target.dimension());
            if (targetLevel == null || !targetLevel.isLoaded(target.pos())) {
                return TargetResolution.unloaded();
            }
            return this.resolveTargetFromLevel(targetLevel, target.pos());
        }

        // Client only has direct access to the current dimension's loaded chunks.
        if (!level.dimension().equals(target.dimension()) || !level.isLoaded(target.pos())) {
            return TargetResolution.unloaded();
        }

        return this.resolveTargetFromLevel(level, target.pos());
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

        TargetResolution result = this.resolveTargetFromLevel(targetLevel, target.pos());
        refreshLinkStatus(result);
        return result;
    }

    @NotNull
    private TargetResolution resolveTargetFromLevel(@NotNull Level targetLevel, @NotNull BlockPos targetPos) {
        BlockEntity blockEntity = targetLevel.getBlockEntity(targetPos);
        if (blockEntity instanceof ClayContainerBlockEntity targetContainer &&
                !(targetContainer instanceof ClayInterfaceBlockEntity)) {
            return TargetResolution.valid(targetContainer);
        }
        return TargetResolution.invalid();
    }

    @Nullable
    private ClayContainerBlockEntity createClientTargetUIProxy(@Nullable GlobalPos target,
                                                               @Nullable BlockState targetState) {
        Level level = this.level;
        if (level == null || !level.isClientSide() || target == null || targetState == null) {
            return null;
        }
        if (!(targetState.getBlock() instanceof EntityBlock entityBlock)) {
            return null;
        }

        // This BE is UI-only. It is never inserted into a world chunk and must not be used for game logic.
        BlockEntity blockEntity = entityBlock.newBlockEntity(target.pos(), targetState);
        // Some UI code paths expect a non-null level, so attach the current client level.
        blockEntity.setLevel(level);
        if (blockEntity instanceof ClayContainerBlockEntity targetContainer &&
                !(targetContainer instanceof ClayInterfaceBlockEntity)) {
            return targetContainer;
        }
        return null;
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

    @Nullable
    private ClayContainerBlockEntity resolveTargetForModeCycle() {
        if (this.level instanceof ServerLevel) {
            TargetResolution resolution = this.resolveTarget(this.linkedTargetPos);
            if (resolution.state() == TargetState.VALID) {
                return resolution.target();
            }
            return null;
        }
        if (this.targetValid) {
            return this.linkedTarget;
        }
        return null;
    }

    public boolean hasValidTarget() {
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
