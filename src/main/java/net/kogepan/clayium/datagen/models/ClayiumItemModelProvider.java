package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.ColoredDustTints;
import net.kogepan.clayium.ColoredIngotTints;
import net.kogepan.clayium.ColoredMatterTints;
import net.kogepan.clayium.ColoredPlateTints;
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

    private static final ResourceLocation PLATE_BASE_TEXTURE = Clayium.id("item/plate_base");
    private static final ResourceLocation PLATE_DARK_TEXTURE = Clayium.id("item/plate_dark");
    private static final ResourceLocation PLATE_LIGHT_TEXTURE = Clayium.id("item/plate_light");
    private static final ResourceLocation LARGE_PLATE_BASE_TEXTURE = Clayium.id("item/largeplate_base");
    private static final ResourceLocation LARGE_PLATE_DARK_TEXTURE = Clayium.id("item/largeplate_dark");
    private static final ResourceLocation LARGE_PLATE_LIGHT_TEXTURE = Clayium.id("item/largeplate_light");

    private static final ResourceLocation DUST_BASE_TEXTURE = Clayium.id("item/dust_base");
    private static final ResourceLocation DUST_DARK_TEXTURE = Clayium.id("item/dust_dark");
    private static final ResourceLocation DUST_LIGHT_TEXTURE = Clayium.id("item/dust_light");

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
            if (ColoredIngotTints.isColoredIngot(itemHolder)) {
                getBuilder(id.getPath())
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", INGOT_BASE_TEXTURE)
                        .texture("layer1", INGOT_DARK_TEXTURE)
                        .texture("layer2", INGOT_LIGHT_TEXTURE);
            } else if (ColoredPlateTints.isColoredPlate(itemHolder)) {
                boolean large = ColoredPlateTints.isLargePlate(itemHolder);
                getBuilder(id.getPath())
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", large ? LARGE_PLATE_BASE_TEXTURE : PLATE_BASE_TEXTURE)
                        .texture("layer1", large ? LARGE_PLATE_DARK_TEXTURE : PLATE_DARK_TEXTURE)
                        .texture("layer2", large ? LARGE_PLATE_LIGHT_TEXTURE : PLATE_LIGHT_TEXTURE);
            } else if (ColoredDustTints.isColoredDust(itemHolder)) {
                getBuilder(id.getPath())
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", DUST_BASE_TEXTURE)
                        .texture("layer1", DUST_DARK_TEXTURE)
                        .texture("layer2", DUST_LIGHT_TEXTURE);
            } else if (ColoredMatterTints.isColoredMatter(itemHolder)) {
                String prefix = ColoredMatterTints.getTexturePrefix(itemHolder);
                getBuilder(id.getPath())
                        .parent(new ModelFile.UncheckedModelFile("minecraft:item/generated"))
                        .texture("layer0", Clayium.id("item/" + prefix + "_base"))
                        .texture("layer1", Clayium.id("item/" + prefix + "_dark"))
                        .texture("layer2", Clayium.id("item/" + prefix + "_light"));
            } else {
                basicItem(item);
            }
        }
    }
}
