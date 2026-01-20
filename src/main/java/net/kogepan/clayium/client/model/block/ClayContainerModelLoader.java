package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

public class ClayContainerModelLoader implements IGeometryLoader<UnbakedClayContainerModel> {

    public static final ClayContainerModelLoader INSTANCE = new ClayContainerModelLoader();
    public static final ResourceLocation ID = Clayium.id("clay_container_model_loader");

    private ClayContainerModelLoader() {}

    @Override
    @NotNull
    public UnbakedClayContainerModel read(@NotNull JsonObject json,
                                          @NotNull JsonDeserializationContext context) throws JsonParseException {
        BlockModel base = null;
        BlockModel overlay = null;

        if (json.has("base_model")) {
            base = context.deserialize(json.getAsJsonObject("base_model"), BlockModel.class);
        }
        if (json.has("overlay_model")) {
            overlay = context.deserialize(json.getAsJsonObject("overlay_model"), BlockModel.class);
        }
        if (base == null) {
            throw new JsonParseException("ClayContainerModel dosen't have 'base_model'");
        }

        return new UnbakedClayContainerModel(base, overlay);
    }
}
