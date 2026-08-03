package net.kogepan.clayium.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public final class PipedMachineIoRenderState extends BlockEntityRenderState {

    boolean active;
    int connectionMask;
    int packedInputModes;
    int packedOutputModes;
}
