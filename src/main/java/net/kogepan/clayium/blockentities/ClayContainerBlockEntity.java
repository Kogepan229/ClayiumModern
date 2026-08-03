package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.configuration.ConfigurationToolAction;
import net.kogepan.clayium.api.configuration.IMachineConfigurable;
import net.kogepan.clayium.blockentities.trait.ClayContainerTrait;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blockentities.trait.ItemFilterHolderTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.energy.ClayEnergyHandler;
import net.kogepan.clayium.capability.filter.data.ItemFilterData;
import net.kogepan.clayium.inventory.MachineIOItemResourceHandler;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import com.lowdragmc.lowdraglib2.gui.ui.style.StylesheetManager;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class ClayContainerBlockEntity extends BlockEntity implements IMachineConfigurable {

    private static final String INPUT_MODES_KEY = "inputModes";
    private static final String OUTPUT_MODES_KEY = "outputModes";

    protected final MachineIOModes inputModes = new MachineIOModes();
    protected final MachineIOModes outputModes = new MachineIOModes();
    protected final List<MachineIOMode> validInputModes;
    protected final List<MachineIOMode> validOutputModes;

    private final int tier;

    private final Map<String, ClayContainerTrait> traits = new LinkedHashMap<>();
    private final Map<Direction, BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction>> neighborItemHandlerCaches = new EnumMap<>(
            Direction.class);

    protected ClayContainerBlockEntity(
                                       BlockEntityType<?> type,
                                       BlockPos pos,
                                       BlockState blockState,
                                       List<MachineIOMode> validInputModes,
                                       List<MachineIOMode> validOutputModes) {
        super(type, pos, blockState);
        if (validInputModes.isEmpty() || validOutputModes.isEmpty()) {
            throw new IllegalArgumentException("Valid input and output modes must not be empty");
        }
        this.validInputModes = List.copyOf(validInputModes);
        this.validOutputModes = List.copyOf(validOutputModes);
        if (blockState.getBlock() instanceof ClayContainerBlock containerBlock) {
            this.tier = containerBlock.tier();
        } else {
            throw new IllegalArgumentException("Clay container block entity requires a ClayContainerBlock");
        }
        this.addTrait(new ItemFilterHolderTrait(this));
    }

    public static void tick(
                            Level level,
                            BlockPos pos,
                            BlockState state,
                            ClayContainerBlockEntity blockEntity) {
        blockEntity.tick();
    }

    protected void tick() {
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.tick();
        }
    }

    public final MachineIOModes getInputModes() {
        return this.inputModes;
    }

    public final int tier() {
        return this.tier;
    }

    public final MachineIOModes getOutputModes() {
        return this.outputModes;
    }

    public final MachineIOMode getInputMode(Direction direction) {
        return this.inputModes.getMode(direction);
    }

    public final MachineIOMode getOutputMode(Direction direction) {
        return this.outputModes.getMode(direction);
    }

    public void initDefaultRoutes() {}

    public final void onPlacedByServer() {
        this.initDefaultRoutes();
        this.notifyTransferConfigurationChanged();
    }

    public void cycleInputMode(Direction direction) {
        List<MachineIOMode> validModes = this.getCycleValidInputModes(direction);
        if (validModes.isEmpty()) {
            return;
        }
        this.inputModes.setMode(direction, nextMode(this.inputModes.getMode(direction), validModes));
        this.notifyTransferConfigurationChanged();
    }

    public void cycleOutputMode(Direction direction) {
        List<MachineIOMode> validModes = this.getCycleValidOutputModes(direction);
        if (validModes.isEmpty()) {
            return;
        }
        this.outputModes.setMode(direction, nextMode(this.outputModes.getMode(direction), validModes));
        this.notifyTransferConfigurationChanged();
    }

    @Override
    public boolean canConfigure(ConfigurationToolAction action, UseOnContext context) {
        Direction clickedSide = context.getClickedFace();
        return switch (action) {
            case INSERTION -> !this.getCycleValidInputModes(clickedSide).isEmpty();
            case EXTRACTION -> !this.getCycleValidOutputModes(clickedSide).isEmpty();
            case FILTER_REMOVER -> this.getTrait(ItemFilterHolderTrait.TRAIT_ID) instanceof ItemFilterHolderTrait;
            case PIPING, ROTATION -> false;
        };
    }

    @Override
    public void configure(ConfigurationToolAction action, UseOnContext context) {
        if (context.getLevel().isClientSide() || !this.canConfigure(action, context)) {
            return;
        }

        Direction clickedSide = context.getClickedFace();
        switch (action) {
            case INSERTION -> this.cycleInputMode(clickedSide);
            case EXTRACTION -> this.cycleOutputMode(clickedSide);
            case FILTER_REMOVER -> {
                if (this.getTrait(ItemFilterHolderTrait.TRAIT_ID) instanceof ItemFilterHolderTrait filterHolder) {
                    filterHolder.clearFilter(clickedSide);
                }
            }
            case PIPING, ROTATION -> {}
        }
    }

    protected List<MachineIOMode> getCycleValidInputModes(Direction direction) {
        return this.validInputModes;
    }

    protected List<MachineIOMode> getCycleValidOutputModes(Direction direction) {
        return this.validOutputModes;
    }

    public final void addTrait(ClayContainerTrait trait) {
        Objects.requireNonNull(trait);
        this.traits.put(trait.id(), trait);
    }

    public final @Nullable ClayContainerTrait removeTrait(String id) {
        return this.traits.remove(id);
    }

    public final @Nullable ClayContainerTrait getTrait(String id) {
        return this.traits.get(id);
    }

    protected final List<ClayContainerTrait> getTraitsSnapshot() {
        return List.copyOf(this.traits.values());
    }

    public final void notifyItemInputInventoryChanged() {
        this.setChanged();
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.notifyItemInputInventoryChanged();
        }
    }

    public final void notifyItemOutputInventoryChanged() {
        this.setChanged();
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.notifyItemOutputInventoryChanged();
        }
    }

    public final void notifyTransferConfigurationChanged() {
        this.setChanged();
        Level currentLevel = this.level;
        if (currentLevel != null && !currentLevel.isClientSide()) {
            this.invalidateCapabilities();
            BlockState state = this.getBlockState();
            currentLevel.sendBlockUpdated(
                    this.worldPosition,
                    state,
                    state,
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    public final @Nullable ResourceHandler<ItemResource> getNeighborItemHandler(Direction direction) {
        BlockCapabilityCache<ResourceHandler<ItemResource>, @Nullable Direction> cache = this.neighborItemHandlerCaches
                .get(direction);
        return cache != null ? cache.getCapability() : null;
    }

    public @Nullable ResourceHandler<ItemResource> getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        }
        return MachineIOItemResourceHandler.create(
                this.getInputInventory(),
                this.getOutputInventory(),
                this.inputModes.getMode(side),
                this.outputModes.getMode(side),
                this.getFilterForSide(side));
    }

    public @Nullable ClayEnergyHandler getExposedClayEnergyHandler(@Nullable Direction side) {
        if (side == null || this.inputModes.getMode(side) != MachineIOMode.CE) {
            return null;
        }
        ClayContainerTrait trait = this.getTrait(ClayEnergyHolder.TRAIT_ID);
        return trait instanceof ClayEnergyHandler energyHandler ? energyHandler : null;
    }

    protected final @Nullable ItemFilterData getFilterForSide(Direction side) {
        ClayContainerTrait trait = this.getTrait(ItemFilterHolderTrait.TRAIT_ID);
        return trait instanceof ItemFilterHolderTrait filterHolder ? filterHolder.getFilter(side) : null;
    }

    public abstract ResourceHandler<ItemResource> getInputInventory();

    public abstract ResourceHandler<ItemResource> getOutputInventory();

    protected abstract void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root);

    public final ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        UIElement root = new UIElement().layout(layout -> layout
                .paddingAll(6)
                .justifyContent(AlignContent.CENTER))
                .addClass("panel_bg");
        root.addChild(createPanelLabel(this.getBlockState().getBlock().getName()));
        this.createMainUI(holder, root);

        UIElement playerInventorySection = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.COLUMN)
                .alignItems(AlignItems.CENTER)
                .widthPercent(100));
        UIElement playerInventory = new UIElement().layout(layout -> layout
                .flexDirection(FlexDirection.COLUMN)
                .alignItems(AlignItems.FLEX_START));
        playerInventory.addChild(createPanelLabel(Component.translatable("container.inventory"))
                .layout(layout -> layout.alignSelf(AlignItems.START)));
        playerInventory.addChild(new InventorySlots());
        playerInventorySection.addChild(playerInventory);
        root.addChild(playerInventorySection);

        return new ModularUI(
                UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }

    private static Label createPanelLabel(Component text) {
        Label label = new Label();
        label.textStyle(style -> style.textColor(0xff404040).textShadow(false));
        label.setText(text);
        return label;
    }

    protected List<ResourceHandler<ItemResource>> getInventoryHandlersForDrops() {
        List<ResourceHandler<ItemResource>> handlers = new ArrayList<>();
        handlers.add(this.getInputInventory());
        handlers.add(this.getOutputInventory());
        return handlers;
    }

    @Override
    public void onLoad() {
        this.neighborItemHandlerCaches.clear();
        if (this.level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                this.neighborItemHandlerCaches.put(
                        direction,
                        BlockCapabilityCache.create(
                                Capabilities.Item.BLOCK,
                                serverLevel,
                                this.worldPosition.relative(direction),
                                direction.getOpposite()));
            }
        }
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.onLoad();
        }
        this.requestModelDataUpdate();
    }

    @Override
    public void onChunkUnloaded() {
        this.neighborItemHandlerCaches.clear();
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.onChunkUnloaded();
        }
    }

    @Override
    public void setRemoved() {
        this.neighborItemHandlerCaches.clear();
        if (!this.isRemoved()) {
            for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
                trait.onRemoved();
            }
        }
        super.setRemoved();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        Level currentLevel = this.level;
        if (currentLevel == null || currentLevel.isClientSide()) {
            return;
        }

        Set<ResourceHandler<ItemResource>> droppedHandlers = Collections
                .newSetFromMap(new IdentityHashMap<>());
        for (ResourceHandler<ItemResource> handler : this.getInventoryHandlersForDrops()) {
            if (!droppedHandlers.add(handler)) {
                continue;
            }
            for (int index = 0; index < handler.size(); index++) {
                ItemResource resource = handler.getResource(index);
                int amount = handler.getAmountAsInt(index);
                if (resource.isEmpty() || amount == 0) {
                    continue;
                }
                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = handler.extract(index, resource, amount, transaction);
                    transaction.commit();
                    if (extracted > 0) {
                        Containers.dropItemStack(
                                currentLevel,
                                pos.getX(),
                                pos.getY(),
                                pos.getZ(),
                                resource.toStack(extracted));
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(INPUT_MODES_KEY, this.inputModes);
        output.putChild(OUTPUT_MODES_KEY, this.outputModes);

        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            ValueOutput traitOutput = output.child(trait.id());
            trait.saveAdditional(traitOutput);
            if (traitOutput.isEmpty()) {
                output.discard(trait.id());
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inputModes.deserialize(input.childOrEmpty(INPUT_MODES_KEY));
        this.outputModes.deserialize(input.childOrEmpty(OUTPUT_MODES_KEY));

        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            trait.loadAdditional(input.childOrEmpty(trait.id()));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(
                this.problemPath(), Clayium.LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            output.putChild(INPUT_MODES_KEY, this.inputModes);
            output.putChild(OUTPUT_MODES_KEY, this.outputModes);

            for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
                ValueOutput traitOutput = output.child(trait.id());
                trait.saveForUpdate(traitOutput);
                if (traitOutput.isEmpty()) {
                    output.discard(trait.id());
                }
            }
            return output.buildResult();
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        this.onReceiveUpdate(input);
    }

    @Override
    public void onDataPacket(Connection connection, ValueInput input) {
        this.onReceiveUpdate(input);
    }

    protected void onReceiveUpdate(ValueInput input) {
        input.child(INPUT_MODES_KEY).ifPresent(this.inputModes::deserialize);
        input.child(OUTPUT_MODES_KEY).ifPresent(this.outputModes::deserialize);
        for (ClayContainerTrait trait : this.getTraitsSnapshot()) {
            input.child(trait.id()).ifPresent(trait::loadForUpdate);
        }

        Level currentLevel = this.level;
        if (currentLevel != null && currentLevel.isClientSide() && this.shouldRefreshModelDataForUpdate(input)) {
            this.requestModelDataUpdate();
            BlockState state = this.getBlockState();
            currentLevel.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_NONE);
        }
    }

    protected boolean shouldRefreshModelDataForUpdate(ValueInput input) {
        return true;
    }

    @Override
    public final ModelData getModelData() {
        int filterSides = 0;
        ClayContainerTrait trait = this.getTrait(ItemFilterHolderTrait.TRAIT_ID);
        if (trait instanceof ItemFilterHolderTrait filterHolder) {
            for (Direction side : Direction.values()) {
                if (filterHolder.getFilter(side) != null || filterHolder.hasFilterClientOnly(side)) {
                    filterSides |= 1 << side.get3DDataValue();
                }
            }
        }
        return ModelData.of(
                ClayContainerModelData.PROPERTY,
                ClayContainerModelData.snapshot(this.inputModes, this.outputModes, filterSides));
    }

    private static MachineIOMode nextMode(MachineIOMode current, List<MachineIOMode> validModes) {
        int currentIndex = validModes.indexOf(current);
        return currentIndex >= 0 ? validModes.get((currentIndex + 1) % validModes.size()) : validModes.getFirst();
    }
}
