package net.kogepan.clayium.integration.jade;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum ClayiumItemStorageProvider implements IServerExtensionProvider<ItemStack>,
        IClientExtensionProvider<ItemStack, ItemView> {

    INSTANCE;

    private static final ResourceLocation UID = Clayium.id("machine_item_storage");

    @Override
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof ClayContainerBlockEntity machine)) {
            return List.of();
        }
        List<ItemStack> contents = machine.getInventoryContentsForDisplay();
        return contents.isEmpty() ? List.of() : List.of(new ViewGroup<>(contents));
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor,
                                                           List<ViewGroup<ItemStack>> groups) {
        return ClientViewGroup.map(groups, ItemView::new, null);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
