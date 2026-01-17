package net.kogepan.clayium.utils;

public enum MachineIOMode {

    NONE(0),
    FIRST(1),
    SECOND(2),
    ALL(100),
    CE(101),
    ;

    private final int mode;

    MachineIOMode(int mode) {
        this.mode = mode;
    }
}
