package net.kogepan.clayium.integration.jade;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ProgressView;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum ClayiumProgressProvider implements IServerExtensionProvider<CompoundTag>,
        IClientExtensionProvider<CompoundTag, ProgressView> {

    INSTANCE;

    private static final ResourceLocation UID = Clayium.id("machine_progress");

    @Override
    public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof ClayContainerBlockEntity machine) ||
                !machine.hasProcessingProgress()) {
            return List.of();
        }
        return List.of(new ViewGroup<>(List.of(ProgressView.create(machine.getProcessingProgress()))));
    }

    @Override
    public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor,
                                                               List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(groups, ProgressView::read, null);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
