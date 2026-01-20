package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.client.model.block.ClayContainerModelLoader;

import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class ClayContainerModelBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

    BlockModelBuilder baseModel;
    BlockModelBuilder overlayModel;

    public ClayContainerModelBuilder(BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(ClayContainerModelLoader.ID, parent, existingFileHelper, false);
    }

    public ClayContainerModelBuilder baseModel(BlockModelBuilder baseModel) {
        this.baseModel = baseModel;
        return this;
    }

    public ClayContainerModelBuilder overlayModel(BlockModelBuilder overlayModel) {
        this.overlayModel = overlayModel;
        return this;
    }

    @Override
    @NotNull
    public JsonObject toJson(@NotNull JsonObject json) {
        super.toJson(json);

        if (baseModel == null) {
            throw new IllegalStateException("baseModel is null");
        }
        json.add("base_model", baseModel.toJson());

        if (overlayModel != null) {
            json.add("overlay_model", overlayModel.toJson());
        }

        return json;
    }
}
