package net.kogepan.clayium.blockentities;

import net.kogepan.clayium.blockentities.trait.ClayContainerTrait;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.inventory.MachineIOInventoryWrapper;
import net.kogepan.clayium.utils.MachineIOMode;
import net.kogepan.clayium.utils.MachineIOModes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
import lombok.Getter;
import org.appliedenergistics.yoga.YogaJustify;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_EXPORT;
import static net.kogepan.clayium.client.model.block.ClayContainerModel.MODEL_DATA_IMPORT;

public abstract class ClayContainerBlockEntity extends BlockEntity {

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
    }

    public static void tick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @NotNull ClayContainerBlockEntity blockEntity) {
        blockEntity.tick();
    }

    @MustBeInvokedByOverriders
    protected void tick() {
        this.traits.values().forEach(ClayContainerTrait::tick);
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

        this.traits.values().forEach(ClayContainerTrait::onLoad);
    }

    public void addTrait(ClayContainerTrait trait) {
        this.traits.put(trait.id, trait);
    }

    @Nullable
    public ClayContainerTrait getTrait(String id) {
        return this.traits.get(id);
    }

    public void notifyItemInputInventoryChanged() {
        for (ClayContainerTrait trait : this.traits.values()) {
            trait.notifyItemInputInventoryChanged();
        }
    }

    public void notifyItemOutputInventoryChanged() {
        for (ClayContainerTrait trait : this.traits.values()) {
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

    public void cycleInputMode(@NotNull Direction direction) {
        MachineIOMode current = this.inputModes.getMode(direction);
        int currentIndex = this.validInputModes.indexOf(current);
        MachineIOMode next = this.validInputModes.get((currentIndex + 1) % this.validInputModes.size());

        this.inputModes.setMode(direction, next);
        if (level != null && !level.isClientSide()) {
            setChanged();
            this.invalidateItemHandlerCapability();
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    public void cycleOutputMode(@NotNull Direction direction) {
        MachineIOMode current = this.outputModes.getMode(direction);
        int currentIndex = this.validOutputModes.indexOf(current);
        MachineIOMode next = this.validOutputModes.get((currentIndex + 1) % this.validOutputModes.size());

        this.outputModes.setMode(direction, next);
        if (level != null && !level.isClientSide()) {
            setChanged();
            this.invalidateItemHandlerCapability();
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
        }
    }

    public abstract IItemHandlerModifiable getInputInventory();

    public abstract IItemHandlerModifiable getOutputInventory();

    @Nullable
    public IItemHandler getExposedItemHandler(@Nullable Direction side) {
        if (side == null) {
            return null;
        } else {
            IItemHandler inputInventory = switch (this.inputModes.getMode(side)) {
                case FIRST -> new RangedWrapper(this.getInputInventory(), 0, 1);
                case SECOND -> new RangedWrapper(this.getInputInventory(), 1, 2);
                case ALL -> this.getInputInventory();
                case CE -> null;
                default -> null;
            };

            IItemHandler outputInventory = switch (this.outputModes.getMode(side)) {
                case FIRST -> new RangedWrapper(this.getOutputInventory(), 0, 1);
                case SECOND -> new RangedWrapper(this.getOutputInventory(), 1, 2);
                case ALL -> this.getOutputInventory();
                default -> null;
            };

            return new MachineIOInventoryWrapper(inputInventory, outputInventory);
        }
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
        this.initDefaultRoutes();

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

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inputModes", this.inputModes.serializeNBT(provider));
        tag.put("outputModes", this.outputModes.serializeNBT(provider));

        // Save trait data
        for (ClayContainerTrait trait : this.traits.values()) {
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
        for (ClayContainerTrait trait : this.traits.values()) {
            if (tag.contains(trait.id)) {
                CompoundTag traitTag = tag.getCompound(trait.id);
                trait.loadAdditional(traitTag, provider);
            }
        }

        if (this.level != null && this.level.isClientSide()) {
            this.requestModelDataUpdate();
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                    Block.UPDATE_NONE);
        }
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.put("inputModes", this.inputModes.serializeNBT(provider));
        tag.put("outputModes", this.outputModes.serializeNBT(provider));
        return tag;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @NotNull
    public ModelData getModelData() {
        return ModelData.builder()
                .with(MODEL_DATA_IMPORT, this.inputModes)
                .with(MODEL_DATA_EXPORT, this.outputModes)
                .build();
    }

    protected abstract void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root);

    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        var root = new UIElement().layout(layout -> layout
                .paddingAll(6)
                .setJustifyContent(YogaJustify.CENTER))
                .addClass("panel_bg");
        root.addChild(
                new CLabel().setText(this.getBlockState().getBlock().getName().getString()));

        this.createMainUI(holder, root);

        root.addChild(new CLabel().setText("Inventory"));
        root.addChild(new InventorySlots());
        return new ModularUI(UI.of(root, List.of(StylesheetManager.INSTANCE.getStylesheetSafe(StylesheetManager.MC))),
                holder.player);
    }
}
