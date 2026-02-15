package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.ColoredIngotTints;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ClayiumItemModelProvider extends ItemModelProvider {

    private static final ResourceLocation INGOT_BASE_TEXTURE = Clayium.id("item/ingot_base");
    private static final ResourceLocation INGOT_DARK_TEXTURE = Clayium.id("item/ingot_dark");
    private static final ResourceLocation INGOT_LIGHT_TEXTURE = Clayium.id("item/ingot_light");

    public ClayiumItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Clayium.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (var itemHolder : ClayiumItems.ITEMS.getEntries()) {
            Item item = itemHolder.get();
            if (item instanceof BlockItem) {
                continue;
            }
            ResourceLocation id = itemHolder.getId();
            // Use 3-layer ingot model (base, dark, light) + ItemColors tint per layer (see ColoredIngotTints)
            if (ColoredIngotTints.isColoredIngot(itemHolder)) {
                getBuilder(id.getPath())
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", INGOT_BASE_TEXTURE)
                        .texture("layer1", INGOT_DARK_TEXTURE)
                        .texture("layer2", INGOT_LIGHT_TEXTURE);
            } else {
                basicItem(item);
            }
        }
    }
}
