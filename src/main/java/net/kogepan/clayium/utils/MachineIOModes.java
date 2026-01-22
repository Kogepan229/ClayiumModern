package net.kogepan.clayium.utils;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import org.jetbrains.annotations.NotNull;

public class MachineIOModes implements INBTSerializable<CompoundTag> {

    MachineIOMode down = MachineIOMode.NONE;
    MachineIOMode up = MachineIOMode.NONE;
    MachineIOMode north = MachineIOMode.NONE;
    MachineIOMode south = MachineIOMode.NONE;
    MachineIOMode west = MachineIOMode.NONE;
    MachineIOMode east = MachineIOMode.NONE;

    public MachineIOModes() {}

    public void setMode(@NotNull Direction direction, @NotNull MachineIOMode mode) {
        switch (direction) {
            case DOWN -> down = mode;
            case UP -> up = mode;
            case NORTH -> north = mode;
            case SOUTH -> south = mode;
            case WEST -> west = mode;
            case EAST -> east = mode;
        }
    }

    @NotNull
    public MachineIOMode getMode(@NotNull Direction direction) {
        return switch (direction) {
            case DOWN -> down;
            case UP -> up;
            case NORTH -> north;
            case SOUTH -> south;
            case WEST -> west;
            case EAST -> east;
        };
    }

    @Override
    @NotNull
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("down", down.getMode());
        tag.putInt("up", up.getMode());
        tag.putInt("north", north.getMode());
        tag.putInt("south", south.getMode());
        tag.putInt("west", west.getMode());
        tag.putInt("east", east.getMode());
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag compoundTag) {
        this.down = MachineIOMode.fromMode(compoundTag.getInt("down"));
        this.up = MachineIOMode.fromMode(compoundTag.getInt("up"));
        this.north = MachineIOMode.fromMode(compoundTag.getInt("north"));
        this.south = MachineIOMode.fromMode(compoundTag.getInt("south"));
        this.west = MachineIOMode.fromMode(compoundTag.getInt("west"));
        this.east = MachineIOMode.fromMode(compoundTag.getInt("east"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MachineIOModes that = (MachineIOModes) obj;
        return down == that.down &&
                up == that.up &&
                north == that.north &&
                south == that.south &&
                west == that.west &&
                east == that.east;
    }

    @Override
    public int hashCode() {
        int result = down.hashCode();
        result = 31 * result + up.hashCode();
        result = 31 * result + north.hashCode();
        result = 31 * result + south.hashCode();
        result = 31 * result + west.hashCode();
        result = 31 * result + east.hashCode();
        return result;
    }
}
