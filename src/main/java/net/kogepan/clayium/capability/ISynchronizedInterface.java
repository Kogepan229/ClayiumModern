package net.kogepan.clayium.capability;

import net.minecraft.core.GlobalPos;

import org.jspecify.annotations.Nullable;

public interface ISynchronizedInterface {

    enum LinkSource {
        NONE,
        MANUAL,
        MULTIBLOCK
    }

    @Nullable
    GlobalPos getLinkedTargetPos();

    LinkSource getLinkSource();

    boolean canSynchronize();

    void setCanSynchronize(boolean canSynchronize);

    boolean setLinkedTarget(GlobalPos target, LinkSource source);

    void clearLinkedTarget();
}
