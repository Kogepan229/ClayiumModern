package net.kogepan.clayium.client.model.block;

import net.kogepan.clayium.Clayium;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

/**
 * Loads the Laser Reflector pyramid geometry from model JSON.
 * Expected JSON: { "loader": "clayium:laser_reflector_geometry", "texture": "clayium:block/machine/laser_reflector" }
 */
public class LaserReflectorGeometryLoader implements IGeometryLoader<UnbakedLaserReflectorModel> {

    public static final LaserReflectorGeometryLoader INSTANCE = new LaserReflectorGeometryLoader();
    public static final ResourceLocation ID = Clayium.id("laser_reflector_geometry");

    private LaserReflectorGeometryLoader() {}

    @Override
    @NotNull
    public UnbakedLaserReflectorModel read(@NotNull JsonObject json, @NotNull JsonDeserializationContext context)
                                                                                                                  throws JsonParseException {
        if (!json.has("texture")) {
            throw new JsonParseException("Laser reflector model requires 'texture'");
        }
        ResourceLocation texture = ResourceLocation.parse(json.get("texture").getAsString());
        return new UnbakedLaserReflectorModel(texture);
    }
}
