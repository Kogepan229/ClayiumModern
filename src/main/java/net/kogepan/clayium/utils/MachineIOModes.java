package net.kogepan.clayium.utils;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;

public class MachineIOModes {

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
}
