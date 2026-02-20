package net.kogepan.clayium.client.model.block;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import com.mojang.math.Transformation;
import org.jetbrains.annotations.NotNull;

/**
 * Unbaked geometry for the Laser Reflector pyramid model.
 * Reads texture from JSON and bakes with the blockstate variant rotation.
 */
public class UnbakedLaserReflectorModel implements IUnbakedGeometry<UnbakedLaserReflectorModel> {

    private final ResourceLocation texture;

    public UnbakedLaserReflectorModel(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    @NotNull
    public BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker,
                           @NotNull java.util.function.Function<Material, TextureAtlasSprite> spriteGetter,
                           @NotNull ModelState modelState, @NotNull ItemOverrides overrides) {
        Material mat = new Material(InventoryMenu.BLOCK_ATLAS, texture);
        TextureAtlasSprite sprite = spriteGetter.apply(mat);
        Transformation rotation = modelState.getRotation();
        return new BakedLaserReflectorModel(sprite, rotation);
    }
}
