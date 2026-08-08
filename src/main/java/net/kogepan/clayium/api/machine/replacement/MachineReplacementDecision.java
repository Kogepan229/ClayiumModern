package net.kogepan.clayium.api.machine.replacement;

import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

public record MachineReplacementDecision(boolean allowed, @Nullable Component failureMessage) {

    public static MachineReplacementDecision allow() {
        return new MachineReplacementDecision(true, null);
    }

    public static MachineReplacementDecision deny(Component failureMessage) {
        return new MachineReplacementDecision(false, failureMessage);
    }
}
