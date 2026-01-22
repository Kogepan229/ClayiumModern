package net.kogepan.clayium.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public enum MachineIOMode {

    NONE(0),
    FIRST(1),
    SECOND(2),
    ALL(100),
    CE(101),
    ;

    @Getter
    private final int mode;

    MachineIOMode(int mode) {
        this.mode = mode;
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
