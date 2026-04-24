package net.kogepan.clayium.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

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
    M_6(206),
    ;

    @Getter
    private final int mode;

    MachineIOMode(int mode) {
        this.mode = mode;
    }

    /**
     * For {@link #M_1}..{@link #M_6}, returns zero-based track index (0..5). Empty for other modes.
     */
    @NotNull
    public OptionalInt multitrackSingleTrackIndex() {
        int i = this.mode - M_1.mode;
        if (i >= 0 && i <= 5) {
            return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    public boolean isMultitrackMode() {
        return this == M_ALL || multitrackSingleTrackIndex().isPresent();
    }

    @NotNull
    public static MachineIOMode fromMode(int mode) {
        for (MachineIOMode value : values()) {
            if (value.mode == mode) {
                return value;
            }
        }
        return NONE;
    }
}
