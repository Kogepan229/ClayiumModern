package net.kogepan.clayium.datagen;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ClayiumItemModelProvider extends ItemModelProvider {

    public ClayiumItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Clayium.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (var itemHolder : ClayiumItems.ITEMS.getEntries()) {
            Item item = itemHolder.get();
            if (!(item instanceof BlockItem)) {
                basicItem(item);
            }
        }
    }
}
