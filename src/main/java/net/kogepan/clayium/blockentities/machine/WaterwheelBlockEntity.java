package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.capability.ClayiumCapabilities;
import net.kogepan.clayium.capability.IClayEnergyHolder;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.BindableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class WaterwheelBlockEntity extends ClayContainerBlockEntity {

    private static final int MAX_PROGRESS = 20_000;
    private static final int BASE_PROGRESS_EFFICIENCY = 1_000;
    private static final int PROGRESS_RANDOM_DIVISOR = 40;

    private final Map<@NotNull Direction, BlockCapabilityCache<IClayEnergyHolder, @Nullable Direction>> neighborsEnergyHolderCache = new EnumMap<>(
            Direction.class);

    private final long clayEnergyPerWork;
    private final long maxClayEnergy;

    private int waterCount;
    private int progress;
    private int progressEfficiency;

    public WaterwheelBlockEntity(@NotNull BlockPos pos,
                                 @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.WATERWHEEL_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.CE),
                List.of(MachineIOMode.NONE));

        long tierValue = Math.max(this.tier, 1);

        this.progress = 0;
        this.progressEfficiency = (int) (BASE_PROGRESS_EFFICIENCY * Math.pow(tierValue, 3.0D));

        this.clayEnergyPerWork = (long) Math.pow(tierValue, 8.0D);
        this.maxClayEnergy = this.clayEnergyPerWork * 5L;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                BlockCapabilityCache<IClayEnergyHolder, @Nullable Direction> cache = BlockCapabilityCache.create(
                        ClayiumCapabilities.CLAY_ENERGY_HOLDER,
                        serverLevel,
                        worldPosition.relative(direction),
                        direction.getOpposite());
                this.neighborsEnergyHolderCache.put(direction, cache);
            }
        }
    }

    @Override
    protected void tick() {
        super.tick();
        Level level = this.level;
        if (level == null || level.isClientSide()) {
            return;
        }

        this.waterCount = this.getWaterFlowsCount(level);

        if (level.getRandom().nextInt(PROGRESS_RANDOM_DIVISOR) < this.waterCount) {
            int delta = (int) (this.progressEfficiency * Math.pow(Math.max(this.tier, 1), 3.0D));
            this.progress += delta;
            setChanged();
        }

        if (this.progress >= MAX_PROGRESS) {
            this.progress -= MAX_PROGRESS;

            if (level.getRandom().nextInt(5) == 0) {
                this.progressEfficiency -= 1;
            }

            this.emitEnergy(level);
            setChanged();
        }
    }

    private void emitEnergy(@NotNull Level level) {
        for (Direction side : Direction.values()) {
            BlockPos neighborPos = this.worldPosition.relative(side);

            if (level.getBlockEntity(neighborPos) instanceof ClayContainerBlockEntity container) {
                if (!acceptWaterwheelTier(container.tier)) {
                    continue;
                }
            }

            IClayEnergyHolder energyHolder = getNeighborEnergyHolder(side);
            if (energyHolder == null) {
                continue;
            }

            long energyStored = energyHolder.getEnergyStored();
            if (energyStored < this.maxClayEnergy) {
                energyHolder.addEnergy(this.clayEnergyPerWork);
            }
        }
    }

    @Nullable
    private IClayEnergyHolder getNeighborEnergyHolder(@NotNull Direction direction) {
        BlockCapabilityCache<IClayEnergyHolder, @Nullable Direction> cache = this.neighborsEnergyHolderCache
                .get(direction);
        return cache != null ? cache.getCapability() : null;
    }

    private int getWaterFlowsCount(@NotNull Level level) {
        int waterFlows = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = this.worldPosition.offset(dx, dy, dz);
                    FluidState fluidState = level.getFluidState(checkPos);
                    if (fluidState.is(FluidTags.WATER) && !fluidState.isSource()) {
                        waterFlows++;
                    }
                }
            }
        }

        return waterFlows;
    }

    private static boolean acceptWaterwheelTier(int tier) {
        return tier == 2 || tier == 3;
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return (IItemHandlerModifiable) EmptyItemHandler.INSTANCE;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return (IItemHandlerModifiable) EmptyItemHandler.INSTANCE;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.progress)
                .remoteSetter((val) -> this.progress = val).build()));
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.progressEfficiency)
                .remoteSetter((val) -> this.progressEfficiency = val).build()));
        root.addChild(new BindableValue<Integer>().bind(DataBindingBuilder.intValS2C(() -> this.waterCount)
                .remoteSetter((val) -> this.waterCount = val).build()));

        root.addChild(new UIElement()
                .addChild(new CLabel().bindDataSource(SupplierDataSource
                        .of(() -> Component.translatable("gui.clayium.waterwheel.progress", this.progress))))
                .addChild(new CLabel().bindDataSource(SupplierDataSource.of(
                        () -> Component.translatable("gui.clayium.waterwheel.durability", this.progressEfficiency))))
                .addChild(new CLabel().bindDataSource(SupplierDataSource
                        .of(() -> Component.translatable("gui.clayium.waterwheel.water", this.waterCount)))));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("progress", this.progress);
        tag.putInt("progressEfficiency", this.progressEfficiency);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("progress")) {
            this.progress = tag.getInt("progress");
        }
        if (tag.contains("progressEfficiency")) {
            this.progressEfficiency = tag.getInt("progressEfficiency");
        }
    }
}
