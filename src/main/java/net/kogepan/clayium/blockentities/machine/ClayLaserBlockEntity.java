package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.Config;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.trait.ClayEnergyHolder;
import net.kogepan.clayium.blocks.ClayContainerBlock;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.client.ldlib.elements.CLabel;
import net.kogepan.clayium.inventory.ClayiumItemStackHandler;
import net.kogepan.clayium.laser.ClayLaserIrradiator;
import net.kogepan.clayium.laser.Laser;
import net.kogepan.clayium.laser.LaserIrradiationRecipeHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.utils.MachineIOMode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClayLaserBlockEntity extends ClayContainerBlockEntity implements IClayLaserSource {

    private static final long CONSUMING_ENERGY_BLUE = 40_000L;
    private static final long CONSUMING_ENERGY_GREEN = 400_000L;
    private static final long CONSUMING_ENERGY_RED = 4_000_000L;
    private static final long CONSUMING_ENERGY_WHITE = 40_000_000L;

    private static final Laser LASER_BLUE = new Laser(0, 0, 1);
    private static final Laser LASER_GREEN = new Laser(0, 1, 0);
    private static final Laser LASER_RED = new Laser(1, 0, 0);
    private static final Laser LASER_WHITE = new Laser(3, 3, 3);

    private final ClayEnergyHolder energyHolder;
    private final ClayLaserIrradiator irradiator;
    private final ClayiumItemStackHandler emptyInventory = new ClayiumItemStackHandler(this, 0);

    private final Laser sampleLaser;
    private final long consumingEnergy;

    private boolean invertRsCondition = false;
    private boolean irradiating = false;
    private int length = 0;

    public ClayLaserBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        super(ClayiumBlockEntityTypes.CLAY_LASER_BLOCK_ENTITY.get(), pos, blockState,
                List.of(MachineIOMode.NONE, MachineIOMode.CE),
                List.of(MachineIOMode.NONE));

        this.energyHolder = new ClayEnergyHolder(this);
        this.addTrait(this.energyHolder);

        int maxLength = Config.MAX_CLAY_LASER_LENGTH.get();
        this.irradiator = new ClayLaserIrradiator(this, maxLength, new LaserIrradiationRecipeHandler());

        this.sampleLaser = sampleLaserForTier(this.tier);
        this.consumingEnergy = consumingEnergyForTier(this.tier);
    }

    @Override
    public void initDefaultRoutes() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ClayContainerBlock containerBlock) {
            Direction back = state.getValue(containerBlock.getFacingProperty()).getOpposite();
            this.inputModes.setMode(back, MachineIOMode.CE);
        }
    }

    @Override
    protected void tick() {
        super.tick();
        Level level = this.level;
        if (level == null || level.isClientSide()) {
            return;
        }

        boolean prevIrradiating = this.irradiating;
        int prevLength = this.length;

        boolean shouldIrradiate = shouldIrradiate(level.hasNeighborSignal(this.worldPosition), this.invertRsCondition);
        boolean canEmit = shouldIrradiate && this.energyHolder.drawEnergy(this.consumingEnergy, false);

        if (canEmit) {
            this.irradiating = true;
            this.length = this.irradiator.irradiateLaser(this.getDirection(), this.sampleLaser);
        } else {
            if (this.irradiating) {
                this.irradiator.stopIrradiation();
            }
            this.irradiating = false;
            this.length = 0;
        }

        // Sync to client when irradiating state or length changes
        if (this.irradiating != prevIrradiating || this.length != prevLength) {
            this.setChanged();
            level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(),
                    net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }
    }

    public static boolean shouldIrradiate(boolean hasRedstoneSignal, boolean invertRsCondition) {
        return invertRsCondition ? hasRedstoneSignal : !hasRedstoneSignal;
    }

    public void setInvertRsCondition(boolean invertRsCondition) {
        this.invertRsCondition = invertRsCondition;
    }

    public boolean isInvertRsCondition() {
        return this.invertRsCondition;
    }

    @Override
    @Nullable
    public Laser getIrradiatingLaser() {
        return this.irradiating ? this.sampleLaser : null;
    }

    @Override
    @NotNull
    public Direction getDirection() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof ClayContainerBlock containerBlock) {
            return state.getValue(containerBlock.getFacingProperty());
        }
        return Direction.NORTH;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public IItemHandlerModifiable getInputInventory() {
        return this.emptyInventory;
    }

    @Override
    public IItemHandlerModifiable getOutputInventory() {
        return this.emptyInventory;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("invertRsCondition", this.invertRsCondition);
        tag.putBoolean("irradiating", this.irradiating);
        tag.putInt("laserLength", this.length);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("invertRsCondition")) {
            this.invertRsCondition = tag.getBoolean("invertRsCondition");
        }
        if (tag.contains("irradiating")) {
            this.irradiating = tag.getBoolean("irradiating");
        }
        if (tag.contains("laserLength")) {
            this.length = tag.getInt("laserLength");
        }
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("invertRsCondition", this.invertRsCondition);
        tag.putBoolean("irradiating", this.irradiating);
        tag.putInt("laserLength", this.length);
        return tag;
    }

    @Override
    protected void createMainUI(BlockUIMenuType.BlockUIHolder holder, UIElement root) {
        root.addChild(this.energyHolder.createEnergyTextUIElement().textStyle(style -> style.adaptiveWidth(true)));
        root.addChild(new CLabel().bindDataSource(SupplierDataSource.of(
                () -> Component.translatable("gui.clayium.clay_laser.length", this.length))));
        root.addChild(new CLabel().bindDataSource(SupplierDataSource.of(
                () -> Component.translatable("gui.clayium.clay_laser.mode",
                        this.invertRsCondition ? Component.translatable("gui.clayium.clay_laser.mode.inverted") :
                                Component.translatable("gui.clayium.clay_laser.mode.default")))));
    }

    private static Laser sampleLaserForTier(int tier) {
        return switch (tier) {
            case 7 -> LASER_BLUE;
            case 8 -> LASER_GREEN;
            case 9 -> LASER_RED;
            default -> LASER_WHITE;
        };
    }

    private static long consumingEnergyForTier(int tier) {
        return switch (tier) {
            case 7 -> CONSUMING_ENERGY_BLUE;
            case 8 -> CONSUMING_ENERGY_GREEN;
            case 9 -> CONSUMING_ENERGY_RED;
            default -> CONSUMING_ENERGY_WHITE;
        };
    }
}
