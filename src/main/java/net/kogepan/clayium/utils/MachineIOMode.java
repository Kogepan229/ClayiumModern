package net.kogepan.clayium.utils;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;

import java.util.OptionalInt;

public enum MachineIOMode {

    NONE(0),
    FIRST(1),
    SECOND(2),
    ALL(100),
    CE(101),
    /** Multitrack buffer: all tracks on this face. */
    M_ALL(200),
    M_1(201),
    M_2(202),
    M_3(203),
    M_4(204),
    M_5(205),
    M_6(206);

    public static final Codec<MachineIOMode> CODEC = Codec.INT.xmap(MachineIOMode::fromMode, MachineIOMode::getMode);
    public static final StreamCodec<ByteBuf, MachineIOMode> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(MachineIOMode::fromMode, MachineIOMode::getMode);

    private final int mode;

    MachineIOMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }

    /**
     * Returns the zero-based track index for {@link #M_1} through {@link #M_6}.
     *
     * @return the track index, or an empty value for other modes
     */
    public OptionalInt multitrackSingleTrackIndex() {
        int index = this.mode - M_1.mode;
        return index >= 0 && index <= 5 ? OptionalInt.of(index) : OptionalInt.empty();
    }

    public boolean isMultitrackMode() {
        return this == M_ALL || this.multitrackSingleTrackIndex().isPresent();
    }

    public static MachineIOMode fromMode(int mode) {
        for (MachineIOMode value : values()) {
            if (value.mode == mode) {
                return value;
            }
        }
        return NONE;
    }
}
