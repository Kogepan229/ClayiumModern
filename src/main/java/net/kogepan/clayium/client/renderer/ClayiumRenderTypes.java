package net.kogepan.clayium.client.renderer;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;

/**
 * Custom RenderTypes for Clayium.
 */
@OnlyIn(Dist.CLIENT)
public final class ClayiumRenderTypes {

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
