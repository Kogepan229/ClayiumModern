package net.kogepan.clayium.client.renderer;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.function.Function;

/**
 * Custom RenderTypes for Clayium.
 */
@OnlyIn(Dist.CLIENT)
public final class ClayiumRenderTypes {

    /**
     * Unlit translucent emissive-style RenderType. Similar to {@link RenderType#entityTranslucentEmissive}
     * but uses NeoForge's unlit shader (no diffuse lighting). Use for laser beams and other fullbright
     * translucent effects. Pass FULL_BRIGHT to vertices for uniform brightness.
     */
    private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_UNLIT_EMISSIVE = Util
            .memoize(ClayiumRenderTypes::createEntityTranslucentUnlitEmissive);

    private static RenderType createEntityTranslucentUnlitEmissive(ResourceLocation texture) {
        return RenderType.create(
                "clayium_entity_translucent_unlit_emissive",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                true,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(
                                ClientHooks.ClientEvents::getEntityTranslucentUnlitShader))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .createCompositeState(true));
    }

    /**
     * Returns an unlit translucent emissive RenderType for the given texture. Cached per texture.
     */
    public static RenderType entityTranslucentUnlitEmissive(ResourceLocation texture) {
        return ENTITY_TRANSLUCENT_UNLIT_EMISSIVE.apply(texture);
    }

    /**
     * Unlit translucent + cull for Laser Reflector. Uses NeoForge's unlit shader (no diffuse lighting)
     * with face culling. Pass FULL_BRIGHT to vertices for uniform brightness.
     */
    public static final RenderType LASER_REFLECTOR_TRANSLUCENT = RenderType.create(
            "clayium_laser_reflector_translucent",
            com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY,
            com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS,
            1536,
            true,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            ClientHooks.ClientEvents::getEntityTranslucentUnlitShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(
                            TextureAtlas.LOCATION_BLOCKS, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .createCompositeState(true));

    private ClayiumRenderTypes() {}
}
