package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.api.configuration.ConfigurationToolAction;
import net.kogepan.clayium.api.configuration.ConfigurationToolUseHelper;
import net.kogepan.clayium.api.configuration.IMachineConfigurable;
import net.kogepan.clayium.api.configuration.MachineIOConfiguration;
import net.kogepan.clayium.api.configuration.MachineIOMode;
import net.kogepan.clayium.api.configuration.MachineIOModes;
import net.kogepan.clayium.blockentities.trait.ClayContainerTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blockentities.trait.ItemFilterHolderTrait;
import net.kogepan.clayium.blockentities.trait.OverclockHandler;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.IClayEnergyHolder;
import net.kogepan.clayium.capability.IItemFilterApplicatable;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.inventory.FilteredItemHandler;
import net.kogepan.clayium.inventory.MachineIOInventoryWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import lombok.Getter;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_EXPORT;
import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_FILTER_SIDES;
import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_FRONT_OVERLAY_VARIANT;
import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_IMPORT;

public abstract class ClayContainerBlockEntity extends BlockEntity implements IMachineConfigurable {

    @Getter
    protected final MachineIOModes inputModes = new MachineIOModes();
    @Getter
    protected final MachineIOModes outputModes = new MachineIOModes();
    protected final List<MachineIOMode> validInputModes;
    protected final List<MachineIOMode> validOutputModes;

    public final int tier;

    protected final Map<String, ClayContainerTrait> traits = new LinkedHashMap<>();

    protected final Map<@NotNull Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> neighborsItemHandlerCache = new EnumMap<>(
            Direction.class);

    public ClayContainerBlockEntity(@NotNull BlockEntityType<?> type, @NotNull BlockPos pos,
                                    @NotNull BlockState blockState,
                                    @NotNull List<MachineIOMode> validInputModes,
                                    @NotNull List<MachineIOMode> validOutputModes) {
        super(type, pos, blockState);

        assert !validInputModes.isEmpty();
        assert !validOutputModes.isEmpty();
        this.validInputModes = validInputModes;
        this.validOutputModes = validOutputModes;

        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            tier = containerBlock.tier;
        } else {
            throw new RuntimeException("Invalid block!");
        }

        this.addTrait(new ItemFilterHolderTrait(this));
    }

    public static void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @NotNull ClayContainerBlockEntity blockEntity) {
        blockEntity.tick();
    }

    @MustBeInvokedByOverriders
    protected void tick() {
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.tick();
        }
    }

    @Override
    public void onLoad() {
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                BlockCapabilityCache<IItemHandler, @Nullable Direction> cache = BlockCapabilityCache.create(
                        Capabilities.ItemHandler.BLOCK,
                        serverLevel,
                        worldPosition.relative(direction),
                        direction.getOpposite());
                this.neighborsItemHandlerCache.put(direction, cache);
            }
        }

        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.onLoad();
        }
    }

    public void addTrait(ClayContainerTrait trait) {
        this.traits.put(trait.id, trait);
    }

    @Nullable
    public ClayContainerTrait removeTrait(@NotNull String id) {
        return this.traits.remove(id);
    }

    @Nullable
    public ClayContainerTrait getTrait(String id) {
        return this.traits.get(id);
    }

    @Nullable
    public OverclockHandler getOverclockHandler() {
        ClayContainerTrait trait = this.getTrait(OverclockHandler.TRAIT_ID);
        return trait instanceof OverclockHandler handler ? handler : null;
    }

    public void refreshOverclockFactor() {
        OverclockHandler handler = this.getOverclockHandler();
        if (handler != null) {
            handler.refreshOverclockFactor();
        }
    }

    public void notifyItemInputInventoryChanged() {
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.notifyItemInputInventoryChanged();
        }
    }

    public void notifyItemOutputInventoryChanged() {
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.notifyItemOutputInventoryChanged();
        }
    }

    public void initDefaultRoutes() {}

    @Nullable
    public IItemHandler getNeighborItemHandler(@NotNull Direction direction) {
        return this.neighborsItemHandlerCache.get(direction).getCapability();
    }

    @NotNull
    public MachineIOMode getInputMode(@NotNull Direction direction) {
        return this.inputModes.getMode(direction);
    }

    @NotNull
    public MachineIOMode getOutputMode(@NotNull Direction direction) {
        return this.outputModes.getMode(direction);
    }

    @NotNull
    protected List<MachineIOMode> getCycleValidInputModes(@NotNull Direction direction) {
        return this.validInputModes;
    }

    @NotNull
    protected List<MachineIOMode> getCycleValidOutputModes(@NotNull Direction direction) {
        return this.validOutputModes;
    }

    public void cycleInputMode(@NotNull Direction direction) {
        List<MachineIOMode> validModes = this.getCycleValidInputModes(direction);
        if (validModes.isEmpty()) {
            return;
        }

        MachineIOMode current = this.inputModes.getMode(direction);
        int currentIndex = validModes.indexOf(current);
        MachineIOMode next = currentIndex >= 0 ?
                validModes.get((currentIndex + 1) % validModes.size()) :
                validModes.get(0);

        this.inputModes.setMode(direction, next);
        if (level != null && !level.isClientSide()) {
            setChanged();
            this.invalidateItemHandlerCapability();
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    public void cycleOutputMode(@NotNull Direction direction) {
        List<MachineIOMode> validModes = this.getCycleValidOutputModes(direction);
        if (validModes.isEmpty()) {
            return;
        }

        MachineIOMode current = this.outputModes.getMode(direction);
        int currentIndex = validModes.indexOf(current);
        MachineIOMode next = currentIndex >= 0 ?
                validModes.get((currentIndex + 1) % validModes.size()) :
                validModes.get(0);

        this.outputModes.setMode(direction, next);
        if (level != null && !level.isClientSide()) {
            setChanged();
            this.invalidateItemHandlerCapability();
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    public boolean canApplyIOModes(@NotNull MachineIOModes inputModes, @NotNull MachineIOModes outputModes) {
        for (Direction direction : Direction.values()) {
            if (!this.getCycleValidInputModes(direction).contains(inputModes.getMode(direction)) ||
                    !this.getCycleValidOutputModes(direction).contains(outputModes.getMode(direction))) {
                return false;
            }
        }
        return true;
    }

    public boolean applyIOModes(@NotNull MachineIOModes inputModes, @NotNull MachineIOModes outputModes) {
        if (!this.canApplyIOModes(inputModes, outputModes)) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            this.inputModes.setMode(direction, inputModes.getMode(direction));
            this.outputModes.setMode(direction, outputModes.getMode(direction));
        }
        if (level != null && !level.isClientSide()) {
            setChanged();
            this.invalidateItemHandlerCapability();
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
        return true;
    }

    @Override
    public boolean canConfigure(@NotNull ConfigurationToolAction action, @NotNull UseOnContext context) {
        return action != ConfigurationToolAction.FILTER_REMOVER || this.getConfigurationFilterTarget() != null;
    }

    @Override
    public void configure(@NotNull ConfigurationToolAction action, @NotNull UseOnContext context) {
        Level contextLevel = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = contextLevel.getBlockState(pos);
        if (contextLevel.isClientSide() || !(state.getBlock() instanceof ClayContainerBlock containerBlock)) {
            return;
        }

        Direction clickedSide = this.getConfigurationHitDirection(context, state);
        switch (action) {
            case INSERTION -> this.cycleInputMode(clickedSide);
            case EXTRACTION -> this.cycleOutputMode(clickedSide);
            case PIPING -> this.togglePipe(contextLevel, pos, state);
            case ROTATION -> this.rotateFromSide(contextLevel, pos, state, containerBlock.getFacingProperty(),
                    clickedSide);
            case FILTER_REMOVER -> {
                IItemFilterApplicatable filterTarget = this.getConfigurationFilterTarget();
                if (filterTarget != null) {
                    filterTarget.clearFilter(clickedSide);
                }
            }
        }
    }

    @Override
    public boolean supportsConfigurationMemory(@NotNull UseOnContext context) {
        return context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof ClayContainerBlock;
    }

    @Override
    @Nullable
    public MachineIOConfiguration captureConfiguration(@NotNull UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (!(state.getBlock() instanceof ClayContainerBlock containerBlock)) {
            return null;
        }

        List<MachineIOMode> capturedInputModes = new ArrayList<>(Direction.values().length);
        List<MachineIOMode> capturedOutputModes = new ArrayList<>(Direction.values().length);
        for (Direction direction : Direction.values()) {
            capturedInputModes.add(this.getInputMode(direction));
            capturedOutputModes.add(this.getOutputMode(direction));
        }
        DirectionProperty facingProperty = containerBlock.getFacingProperty();
        boolean supportsVerticalFacing = facingProperty.getPossibleValues().contains(Direction.UP);
        return new MachineIOConfiguration(capturedInputModes, capturedOutputModes,
                state.getValue(facingProperty), supportsVerticalFacing, state.getValue(ClayContainerBlock.PIPE));
    }

    @Override
    public boolean applyConfiguration(@NotNull UseOnContext context,
                                      @NotNull MachineIOConfiguration configuration) {
        Level contextLevel = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = contextLevel.getBlockState(pos);
        if (contextLevel.isClientSide() || !(state.getBlock() instanceof ClayContainerBlock containerBlock)) {
            return false;
        }

        DirectionProperty facingProperty = containerBlock.getFacingProperty();
        boolean supportsVerticalFacing = facingProperty.getPossibleValues().contains(Direction.UP);
        if (supportsVerticalFacing != configuration.supportsVerticalFacing() ||
                !facingProperty.getPossibleValues().contains(configuration.facing())) {
            return false;
        }

        MachineIOModes configuredInputModes = configuration.createInputModes();
        MachineIOModes configuredOutputModes = configuration.createOutputModes();
        if (!this.canApplyIOModes(configuredInputModes, configuredOutputModes)) {
            return false;
        }

        BlockState configuredState = state
                .setValue(facingProperty, configuration.facing())
                .setValue(ClayContainerBlock.PIPE, configuration.pipe());
        for (Direction direction : Direction.values()) {
            configuredState = configuredState.setValue(ClayContainerBlock.getProperty(direction), false);
        }
        if (configuration.pipe()) {
            configuredState = this.updatePipeConnectionState(configuredState);
        }

        contextLevel.setBlock(pos, configuredState, Block.UPDATE_ALL);
        return this.applyIOModes(configuredInputModes, configuredOutputModes);
    }

    @NotNull
    private Direction getConfigurationHitDirection(@NotNull UseOnContext context, @NotNull BlockState state) {
        if (!state.getValue(ClayContainerBlock.PIPE)) {
            return context.getClickedFace();
        }

        Vec3 localHit = context.getClickLocation().subtract(
                context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
        if (ClayContainerBlock.ARM_NORTH.bounds().contains(localHit)) return Direction.NORTH;
        if (ClayContainerBlock.ARM_SOUTH.bounds().contains(localHit)) return Direction.SOUTH;
        if (ClayContainerBlock.ARM_WEST.bounds().contains(localHit)) return Direction.WEST;
        if (ClayContainerBlock.ARM_EAST.bounds().contains(localHit)) return Direction.EAST;
        if (ClayContainerBlock.ARM_UP.bounds().contains(localHit)) return Direction.UP;
        if (ClayContainerBlock.ARM_DOWN.bounds().contains(localHit)) return Direction.DOWN;
        return context.getClickedFace();
    }

    private void togglePipe(@NotNull Level contextLevel, @NotNull BlockPos pos, BlockState state) {
        if (state.getValue(ClayContainerBlock.PIPE)) {
            state = state.setValue(ClayContainerBlock.PIPE, false);
            for (Direction direction : Direction.values()) {
                state = state.setValue(ClayContainerBlock.getProperty(direction), false);
            }
            contextLevel.setBlock(pos, state, Block.UPDATE_CLIENTS);
            return;
        }

        state = state.setValue(ClayContainerBlock.PIPE, true);
        contextLevel.setBlock(pos, this.updatePipeConnectionState(state), Block.UPDATE_ALL);
    }

    private void rotateFromSide(@NotNull Level contextLevel, @NotNull BlockPos pos, @NotNull BlockState state,
                                @NotNull DirectionProperty facingProperty, @NotNull Direction clickedSide) {
        BlockState rotatedState = ConfigurationToolUseHelper.rotateFacingFromSide(state, facingProperty, clickedSide);
        if (rotatedState != state) {
            contextLevel.setBlock(pos, rotatedState, Block.UPDATE_ALL);
        }
    }

    @Nullable
    private IItemFilterApplicatable getConfigurationFilterTarget() {
        if (this instanceof ClayInterfaceBlockEntity clayInterface && !clayInterface.hasValidTarget()) {
            return null;
        }
        ClayContainerTrait trait = this.getTrait(ItemFilterHolderTrait.TRAIT_ID);
        return trait instanceof IItemFilterApplicatable applicatable ? applicatable : null;
    }

    public abstract IItemHandlerModifiable getInputInventory();

    public abstract IItemHandlerModifiable getOutputInventory();

    public void dropInventoryContents(@NotNull Level level) {
        Set<IItemHandler> droppedInventories = Collections.newSetFromMap(new IdentityHashMap<>());
        for (IItemHandler inventory : this.getInventoryHandlersForDrops()) {
            if (!droppedInventories.add(inventory)) {
                continue;
            }
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                ItemStack stack = inventory.extractItem(slot, Integer.MAX_VALUE, false);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, this.worldPosition.getX(), this.worldPosition.getY(),
                            this.worldPosition.getZ(), stack);
                }
            }
        }
    }

    @NotNull
    protected List<IItemHandler> getInventoryHandlersForDrops() {
        List<IItemHandler> inventories = new ArrayList<>();
        inventories.add(this.getInputInventory());
        inventories.add(this.getOutputInventory());

        ClayContainerTrait energyTrait = this.getTrait(ClayEnergyHolder.TRAIT_ID);
        if (energyTrait instanceof ClayEnergyHolder energyHolder) {
            inventories.add(energyHolder.getEnergizedClayItemHandler());
        }
        return inventories;
    }

    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }
        IItemHandler inputInventory = switch (this.inputModes.getMode(side)) {
            case FIRST -> new RangedWrapper(this.getInputInventory(), 0, 1);
            case SECOND -> new RangedWrapper(this.getInputInventory(), 1, 2);
            case ALL -> this.getInputInventory();
            case CE -> null;
            case M_ALL, M_1, M_2, M_3, M_4, M_5, M_6 -> null;
            default -> null;
        };

        IItemHandler outputInventory = switch (this.outputModes.getMode(side)) {
            case FIRST -> new RangedWrapper(this.getOutputInventory(), 0, 1);
            case SECOND -> new RangedWrapper(this.getOutputInventory(), 1, 2);
            case ALL -> this.getOutputInventory();
            case M_ALL, M_1, M_2, M_3, M_4, M_5, M_6 -> null;
            default -> null;
        };

        ItemFilterData filter = getFilterForSide(side);
        if (filter != null) {
            inputInventory = inputInventory != null ? new FilteredItemHandler(inputInventory, filter) : null;
            outputInventory = outputInventory != null ? new FilteredItemHandler(outputInventory, filter) : null;
        }

        return new MachineIOInventoryWrapper(inputInventory, outputInventory);
    }

    @Nullable
    protected ItemFilterData getFilterForSide(@NotNull Direction side) {
        ClayContainerTrait trait = getTrait(ItemFilterHolderTrait.TRAIT_ID);
        return trait instanceof IItemFilterApplicatable applicatable ? applicatable.getFilter(side) : null;
    }

    @Nullable
    public IClayEnergyHolder getExposedClayEnergyHolder(@Nullable Direction side) {
        ClayContainerTrait trait = getTrait(ClayEnergyHolder.TRAIT_ID);
        return trait instanceof IClayEnergyHolder holder ? holder : null;
    }

    protected void invalidateItemHandlerCapability() {
        if (level != null) {
            level.invalidateCapabilities(this.worldPosition);
        }
    }

    public boolean canConnectTo(Direction direction) {
        if (this.level == null) return false;

        return this.getNeighborItemHandler(direction) != null;
    }

    public BlockState updatePipeConnectionState(BlockState state) {
        if (!state.getValue(ClayContainerBlock.PIPE)) {
            return state;
        }

        for (Direction direction : Direction.values()) {
            state = state.setValue(
                    ClayContainerBlock.getProperty(direction),
                    this.canConnectTo(direction));
        }

        return state;
    }

    public void onPlacedByServer(@Nullable LivingEntity placer, ItemStack stack) {
        if (!stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).contains("inputModes")) {
            this.initDefaultRoutes();
        }

        this.setChanged();

        if (this.level != null) {
            BlockState state = getBlockState();
            this.level.sendBlockUpdated(
                    this.getBlockPos(),
                    state,
                    state,
                    Block.UPDATE_CLIENTS);
        }
    }

    public boolean acceptsClayInterfaceSynchronization() {
        return true;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inputModes", this.inputModes.serializeNBT(provider));
        tag.put("outputModes", this.outputModes.serializeNBT(provider));

        // Save trait data
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            CompoundTag traitTag = new CompoundTag();
            trait.saveAdditional(traitTag, provider);
            if (!traitTag.isEmpty()) {
                tag.put(trait.id, traitTag);
            }
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inputModes")) {
            this.inputModes.deserializeNBT(provider, tag.getCompound("inputModes"));
        }
        if (tag.contains("outputModes")) {
            this.outputModes.deserializeNBT(provider, tag.getCompound("outputModes"));
        }

        // Load trait data
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            if (tag.contains(trait.id)) {
                CompoundTag traitTag = tag.getCompound(trait.id);
                trait.loadAdditional(traitTag, provider);
            }
        }
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.put("inputModes", this.inputModes.serializeNBT(provider));
        tag.put("outputModes", this.outputModes.serializeNBT(provider));

        // Save trait data
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            CompoundTag traitTag = new CompoundTag();
            trait.saveForUpdate(traitTag, provider);
            if (!traitTag.isEmpty()) {
                tag.put(trait.id, traitTag);
            }
        }

        return tag;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        if (tag.isEmpty()) return;

        onReceivePacket(tag, provider);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt,
                             @NotNull HolderLookup.Provider provider) {
        CompoundTag tag = pkt.getTag();
        if (tag.isEmpty()) return;

        onReceivePacket(tag, provider);
    }

    protected void onReceivePacket(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        boolean refreshModelData = this.shouldRefreshModelDataForUpdate(tag, provider);
        if (tag.contains("inputModes")) {
            this.inputModes.deserializeNBT(provider, tag.getCompound("inputModes"));
        }
        if (tag.contains("outputModes")) {
            this.outputModes.deserializeNBT(provider, tag.getCompound("outputModes"));
        }

        // Load trait data
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            if (tag.contains(trait.id)) {
                CompoundTag traitTag = tag.getCompound(trait.id);
                trait.loadForUpdate(traitTag, provider);
            }
        }

        Level level = this.level;
        if (refreshModelData && level != null && level.isClientSide()) {
            this.requestModelDataUpdate();
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_NONE);
        }
    }

    protected boolean shouldRefreshModelDataForUpdate(@NotNull CompoundTag tag,
                                                      @NotNull HolderLookup.Provider provider) {
        return true;
    }

    @NotNull
    private List<ClayContainerTrait> getTraitsSnapshot() {
        return List.copyOf(this.traits.values());
    }

    @Override
    @NotNull
    public ModelData getModelData() {
        boolean[] filterSides = new boolean[6];
        ClayContainerTrait trait = getTrait(ItemFilterHolderTrait.TRAIT_ID);
        if (trait instanceof ItemFilterHolderTrait filterHolder) {
            for (Direction d : Direction.values()) {
                filterSides[d.ordinal()] = filterHolder.getFilter(d) != null || filterHolder.hasFilterClientOnly(d);
            }
        }
        ModelData.Builder builder = ModelData.builder()
                .with(MODEL_DATA_IMPORT, this.inputModes)
                .with(MODEL_DATA_EXPORT, this.outputModes)
                .with(MODEL_DATA_FILTER_SIDES, filterSides);
        String frontOverlayVariant = this.getFrontOverlayVariant();
        if (frontOverlayVariant != null) {
            builder.with(MODEL_DATA_FRONT_OVERLAY_VARIANT, frontOverlayVariant);
        }
        return builder.build();
    }

    @Nullable
    protected String getFrontOverlayVariant() {
        // Override in machines that need state-dependent front overlays.
        return null;
    }

    protected abstract void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root);

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement().layout(layout -> layout
                .paddingAll(6)
                .justifyContent(AlignContent.CENTER))
                .addClass("panel_bg");
        root.addChild(
                new CLabel().setText(this.getBlockState().getBlock().getName().getString()));

        this.createMainUI(holder, root);

        UIElement playerInventorySection = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.COLUMN)
                .alignItems(AlignItems.CENTER)
                .widthPercent(100));
        UIElement playerInventoryLabelAndSlots = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.COLUMN)
                .alignItems(AlignItems.FLEX_START));
        playerInventoryLabelAndSlots.addChild(new CLabel().setText("Inventory")
                .layout(layout -> layout.alignSelf(AlignItems.START)));
        playerInventoryLabelAndSlots.addChild(new InventorySlots());
        playerInventorySection.addChild(playerInventoryLabelAndSlots);
        root.addChild(playerInventorySection);
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }
}
