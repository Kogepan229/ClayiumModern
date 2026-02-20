package net.kogepan.clayium.blockentities.machine;

import net.kogepan.clayium.Config;
import net.kogepan.clayium.capability.IClayLaserAcceptor;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.laser.ClayLaserIrradiator;
import net.kogepan.clayium.laser.Laser;
import net.kogepan.clayium.laser.LaserIrradiationRecipeHandler;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Laser Reflector block entity.
 * <p>
 * Accepts laser from multiple faces, merges them (age &lt; 10: sum RGB; age &gt;= 10: max per channel),
 * and re-emits in the facing direction.
 */
public class LaserReflectorBlockEntity extends BlockEntity implements IClayLaserSource, IClayLaserAcceptor {

    private final Map<Direction, Laser> receivedLasers = new EnumMap<>(Direction.class);
    private final ClayLaserIrradiator irradiator;

    @Nullable
    private Laser irradiatingLaser = null;
    private int length = 0;

    public LaserReflectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ClayiumBlockEntityTypes.LASER_REFLECTOR_BLOCK_ENTITY.get(), pos, blockState);
        int maxLength = Config.MAX_CLAY_LASER_LENGTH.get();
        this.irradiator = new ClayLaserIrradiator(this, maxLength, new LaserIrradiationRecipeHandler());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LaserReflectorBlockEntity blockEntity) {
        if (level.isClientSide()) {
            return;
        }

        Laser merged = Laser.merge(blockEntity.receivedLasers.values());
        int prevLength = blockEntity.length;
        Laser prevLaser = blockEntity.irradiatingLaser;

        blockEntity.irradiatingLaser = merged;

        if (merged == null) {
            blockEntity.irradiator.stopIrradiation();
            blockEntity.length = 0;
        } else {
            Direction direction = state.getValue(net.kogepan.clayium.blocks.machine.LaserReflectorBlock.FACING);
            blockEntity.length = blockEntity.irradiator.irradiateLaser(direction, merged);
        }

        if (prevLength != blockEntity.length || !java.util.Objects.equals(prevLaser, merged)) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void acceptLaser(@NotNull Direction irradiatedSide, @Nullable Laser laser) {
        if (laser == null) {
            this.receivedLasers.remove(irradiatedSide);
        } else {
            this.receivedLasers.put(irradiatedSide, laser);
        }
    }

    @Override
    @Nullable
    public Laser getIrradiatingLaser() {
        return this.irradiatingLaser;
    }

    @Override
    @NotNull
    public Direction getDirection() {
        BlockState state = this.getBlockState();
        if (state.getBlock() instanceof net.kogepan.clayium.blocks.machine.LaserReflectorBlock reflectorBlock) {
            return state.getValue(reflectorBlock.FACING);
        }
        return Direction.NORTH;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("irradiating", this.irradiatingLaser != null);
        tag.putInt("laserLength", this.length);
        if (this.irradiatingLaser != null) {
            tag.put("laser", this.irradiatingLaser.toTag());
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("irradiating") && tag.getBoolean("irradiating") && tag.contains("laser")) {
            this.irradiatingLaser = Laser.fromTag(tag.getCompound("laser"));
        } else {
            this.irradiatingLaser = null;
        }
        this.length = tag.getInt("laserLength");
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("irradiating", this.irradiatingLaser != null);
        tag.putInt("laserLength", this.length);
        if (this.irradiatingLaser != null) {
            tag.put("laser", this.irradiatingLaser.toTag());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
        if (tag.contains("irradiating") && tag.getBoolean("irradiating") && tag.contains("laser")) {
            this.irradiatingLaser = Laser.fromTag(tag.getCompound("laser"));
        } else {
            this.irradiatingLaser = null;
        }
        this.length = tag.getInt("laserLength");
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
