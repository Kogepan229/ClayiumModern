package net.kogepan.clayium.datagen.models;

import net.kogepan.clayium.client.model.block.LaserReflectorGeometryLoader;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * Datagen builder for the Laser Reflector custom geometry model.
 * Outputs JSON: { "loader": "clayium:laser_reflector_geometry", "texture": "..." }
 */
public class LaserReflectorModelBuilder extends CustomLoaderBuilder<BlockModelBuilder> {

    private ResourceLocation texture;

    public LaserReflectorModelBuilder(BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(LaserReflectorGeometryLoader.ID, parent, existingFileHelper, false);
    }

    public LaserReflectorModelBuilder texture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    @Override
    @NotNull
    public JsonObject toJson(@NotNull JsonObject json) {
        super.toJson(json);
        if (texture == null) {
            throw new IllegalStateException("Laser reflector model requires texture");
        }
        json.addProperty("texture", texture.toString());
        return json;
    }
}
