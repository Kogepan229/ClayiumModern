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
        json.remove("loader");
        BlockModel base = context.deserialize(json, BlockModel.class);

        return new UnbakedClayContainerModel(base);
    }
}
