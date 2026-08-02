package net.kogepan.clayium.utils;

import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import java.util.EnumMap;
import java.util.Objects;

/** Mutable input or output routing modes for all six block faces. */
public final class MachineIOModes implements ValueIOSerializable {

    public static final Codec<MachineIOModes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MachineIOMode.CODEC.optionalFieldOf("down", MachineIOMode.NONE).forGetter(modes -> modes.down),
            MachineIOMode.CODEC.optionalFieldOf("up", MachineIOMode.NONE).forGetter(modes -> modes.up),
            MachineIOMode.CODEC.optionalFieldOf("north", MachineIOMode.NONE).forGetter(modes -> modes.north),
            MachineIOMode.CODEC.optionalFieldOf("south", MachineIOMode.NONE).forGetter(modes -> modes.south),
            MachineIOMode.CODEC.optionalFieldOf("west", MachineIOMode.NONE).forGetter(modes -> modes.west),
            MachineIOMode.CODEC.optionalFieldOf("east", MachineIOMode.NONE).forGetter(modes -> modes.east))
            .apply(instance, MachineIOModes::new));

    public static final StreamCodec<ByteBuf, MachineIOModes> STREAM_CODEC = StreamCodec.composite(
            MachineIOMode.STREAM_CODEC,
            modes -> modes.down,
            MachineIOMode.STREAM_CODEC,
            modes -> modes.up,
            MachineIOMode.STREAM_CODEC,
            modes -> modes.north,
            MachineIOMode.STREAM_CODEC,
            modes -> modes.south,
            MachineIOMode.STREAM_CODEC,
            modes -> modes.west,
            MachineIOMode.STREAM_CODEC,
            modes -> modes.east,
            MachineIOModes::new);

    private MachineIOMode down;
    private MachineIOMode up;
    private MachineIOMode north;
    private MachineIOMode south;
    private MachineIOMode west;
    private MachineIOMode east;

    public MachineIOModes() {
        this(
                MachineIOMode.NONE,
                MachineIOMode.NONE,
                MachineIOMode.NONE,
                MachineIOMode.NONE,
                MachineIOMode.NONE,
                MachineIOMode.NONE);
    }

    private MachineIOModes(
                           MachineIOMode down,
                           MachineIOMode up,
                           MachineIOMode north,
                           MachineIOMode south,
                           MachineIOMode west,
                           MachineIOMode east) {
        this.down = down;
        this.up = up;
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
    }

    public void setMode(Direction direction, MachineIOMode mode) {
        switch (direction) {
            case DOWN -> this.down = mode;
            case UP -> this.up = mode;
            case NORTH -> this.north = mode;
            case SOUTH -> this.south = mode;
            case WEST -> this.west = mode;
            case EAST -> this.east = mode;
        }
    }

    public MachineIOMode getMode(Direction direction) {
        return switch (direction) {
            case DOWN -> this.down;
            case UP -> this.up;
            case NORTH -> this.north;
            case SOUTH -> this.south;
            case WEST -> this.west;
            case EAST -> this.east;
        };
    }

    public void rotate(Rotation rotation) {
        EnumMap<Direction, MachineIOMode> previousModes = new EnumMap<>(Direction.class);
        for (Direction side : Direction.values()) {
            previousModes.put(side, this.getMode(side));
        }
        for (Direction side : Direction.values()) {
            this.setMode(rotation.rotate(side), Objects.requireNonNull(previousModes.get(side)));
        }
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putInt("down", this.down.getMode());
        output.putInt("up", this.up.getMode());
        output.putInt("north", this.north.getMode());
        output.putInt("south", this.south.getMode());
        output.putInt("west", this.west.getMode());
        output.putInt("east", this.east.getMode());
    }

    @Override
    public void deserialize(ValueInput input) {
        this.down = MachineIOMode.fromMode(input.getIntOr("down", MachineIOMode.NONE.getMode()));
        this.up = MachineIOMode.fromMode(input.getIntOr("up", MachineIOMode.NONE.getMode()));
        this.north = MachineIOMode.fromMode(input.getIntOr("north", MachineIOMode.NONE.getMode()));
        this.south = MachineIOMode.fromMode(input.getIntOr("south", MachineIOMode.NONE.getMode()));
        this.west = MachineIOMode.fromMode(input.getIntOr("west", MachineIOMode.NONE.getMode()));
        this.east = MachineIOMode.fromMode(input.getIntOr("east", MachineIOMode.NONE.getMode()));
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof MachineIOModes that && this.down == that.down && this.up == that.up &&
                this.north == that.north && this.south == that.south && this.west == that.west &&
                this.east == that.east;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.down, this.up, this.north, this.south, this.west, this.east);
    }
}
