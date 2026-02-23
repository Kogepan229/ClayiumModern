package net.kogepan.clayium.capability;

import net.minecraft.core.GlobalPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface linking capability used by Clay Interface and synchronizer item.
 */
public interface ISynchronizedInterface {

    enum LinkSource {
        NONE,
        MANUAL,
        MULTIBLOCK
    }

    @Nullable
    GlobalPos getLinkedTargetPos();

    @NotNull
    LinkSource getLinkSource();

    boolean canSynchronize();

    void setCanSynchronize(boolean canSynchronize);

    boolean setLinkedTarget(@NotNull GlobalPos target, @NotNull LinkSource source);

    void clearLinkedTarget();
}
