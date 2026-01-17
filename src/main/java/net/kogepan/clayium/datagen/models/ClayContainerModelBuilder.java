package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.client.model.block.ClayContainerModelLoader;

import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ClayContainerModelBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

    public ClayContainerModelBuilder(BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(ClayContainerModelLoader.ID, parent, existingFileHelper, false);
    }
}
