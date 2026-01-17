package net.kogepan.clayium.client.model.block;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class UnbakedClayContainerModel implements IUnbakedGeometry<UnbakedClayContainerModel> {

    private final BlockModel base;

    public UnbakedClayContainerModel(BlockModel base) {
        this.base = base;
    }

    @Override
    @NotNull
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
                           ItemOverrides overrides) {
        BakedModel bakedBase = this.base.bake(baker, spriteGetter, modelState);

        return new ClayContainerModel(bakedBase);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.base.resolveParents(modelGetter);
    }
}
