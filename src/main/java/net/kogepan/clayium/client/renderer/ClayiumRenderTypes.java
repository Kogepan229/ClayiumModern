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

import java.util.OptionalDouble;
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

    /**
     * Filled box highlight for linked Clay Interface target.
     * Uses no depth test to remain visible through blocks, matching original Clayium behavior.
     */
    public static final RenderType INTERFACE_TARGET_FILLED_NO_DEPTH = RenderType.create(
            "clayium_interface_target_filled_no_depth",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLE_STRIP,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false));

    /**
     * Line highlight for linked Clay Interface target and source-target connector.
     * Uses no depth test to keep lines visible through terrain.
     */
    public static final RenderType INTERFACE_TARGET_LINES_NO_DEPTH = RenderType.create(
            "clayium_interface_target_lines_no_depth",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false));

    private ClayiumRenderTypes() {}
}
