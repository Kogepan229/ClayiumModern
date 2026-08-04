package net.kogepan.clayium.client.renderer;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.Config;
import net.kogepan.clayium.blockentities.machine.CAReactorBlockEntity;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/** Renders a cached translucent structure glow mesh for each working CA Reactor. */
@EventBusSubscriber(modid = Clayium.MODID, value = Dist.CLIENT)
public final class CAReactorRenderer {

    private static final float RED = 1.0F;
    private static final float GREEN = 1.0F;
    private static final float ALPHA = 0.11F;
    private static final int VERTICES_PER_BOX = 30;
    private static final int MINIMUM_BUFFER_SIZE = 1536;

    private static final Map<CAReactorBlockEntity, RenderCache> CACHES = new IdentityHashMap<>();

    private CAReactorRenderer() {}

    public static void render(@NotNull CAReactorBlockEntity reactor, @NotNull PoseStack poseStack) {
        if (!Config.CA_REACTOR_GLITTERING.get() || !reactor.isReactorWorking()) {
            return;
        }
        if (reactor.getReactorRank() <= 0 || reactor.getReactorGlowPositions().isEmpty()) {
            discardCache(reactor);
            return;
        }

        RenderCache cache = getOrCreateCache(reactor);
        RenderType renderType = ClayiumRenderTypes.CA_REACTOR_HULL_GLOW;
        renderType.setupRenderState();
        try {
            Matrix4f modelViewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix()).mul(poseStack.last().pose());
            cache.vertexBuffer.bind();
            cache.vertexBuffer.drawWithShader(modelViewMatrix, RenderSystem.getProjectionMatrix(),
                    Objects.requireNonNull(RenderSystem.getShader(), "CA Reactor hull glow shader is unavailable"));
        } finally {
            VertexBuffer.unbind();
            renderType.clearRenderState();
        }
    }

    @NotNull
    public static AABB getRenderBoundingBox(@NotNull CAReactorBlockEntity reactor) {
        RenderCache cache = CACHES.get(reactor);
        if (cache != null && (reactor.getReactorGlowPositions().isEmpty() ||
                cache.revision != reactor.getReactorRenderRevision())) {
            discardCache(reactor);
        }
        return reactor.getReactorRenderBoundingBox();
    }

    @NotNull
    private static RenderCache getOrCreateCache(@NotNull CAReactorBlockEntity reactor) {
        purgeRemovedCaches();
        int revision = reactor.getReactorRenderRevision();
        RenderCache cache = CACHES.get(reactor);
        if (cache != null && cache.revision == revision) {
            return cache;
        }

        RenderCache replacement = new RenderCache(revision, buildVertexBuffer(reactor));
        CACHES.put(reactor, replacement);
        if (cache != null) {
            cache.close();
        }
        return replacement;
    }

    @NotNull
    private static VertexBuffer buildVertexBuffer(@NotNull CAReactorBlockEntity reactor) {
        int layerCount = countLayers(reactor.getReactorRank());
        int vertexSize = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
        int expectedBufferSize = Math.multiplyExact(
                Math.multiplyExact(layerCount, reactor.getReactorGlowPositions().size()),
                VERTICES_PER_BOX * vertexSize);

        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try (ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(
                Math.max(MINIMUM_BUFFER_SIZE, expectedBufferSize))) {
            BufferBuilder builder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.TRIANGLE_STRIP,
                    DefaultVertexFormat.POSITION_COLOR);
            appendGlowGeometry(reactor, builder);
            vertexBuffer.bind();
            try {
                vertexBuffer.upload(builder.buildOrThrow());
            } finally {
                VertexBuffer.unbind();
            }
            return vertexBuffer;
        } catch (RuntimeException | Error error) {
            vertexBuffer.close();
            throw error;
        }
    }

    private static void appendGlowGeometry(@NotNull CAReactorBlockEntity reactor, @NotNull BufferBuilder builder) {
        double rank = reactor.getReactorRank();
        BlockPos controllerPosition = reactor.getBlockPos();
        PoseStack identityPose = new PoseStack();

        for (int layer = 1; layer < rank + 0.01D; layer++) {
            double remainingRank = rank - layer;
            double ratio = layer / rank;
            double inflate = 0.01D * (Math.pow(remainingRank, 1.6D) + 1.0D);
            float blue = (float) (0.3D + 0.05D * (2.0D * ratio - ratio * ratio) * layer);

            for (BlockPos glowPosition : reactor.getReactorGlowPositions()) {
                BlockPos relativePosition = glowPosition.subtract(controllerPosition);
                LevelRenderer.addChainedFilledBoxVertices(identityPose, builder,
                        relativePosition.getX() - inflate,
                        relativePosition.getY() - inflate,
                        relativePosition.getZ() - inflate,
                        relativePosition.getX() + 1.0D + inflate,
                        relativePosition.getY() + 1.0D + inflate,
                        relativePosition.getZ() + 1.0D + inflate,
                        RED, GREEN, blue, ALPHA);
            }
        }
    }

    private static int countLayers(double rank) {
        int layers = 0;
        for (int layer = 1; layer < rank + 0.01D; layer++) {
            layers++;
        }
        return layers;
    }

    private static void discardCache(@NotNull CAReactorBlockEntity reactor) {
        RenderCache cache = CACHES.remove(reactor);
        if (cache != null) {
            cache.close();
        }
    }

    private static void purgeRemovedCaches() {
        Iterator<Map.Entry<CAReactorBlockEntity, RenderCache>> iterator = CACHES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CAReactorBlockEntity, RenderCache> entry = iterator.next();
            if (entry.getKey().isRemoved()) {
                entry.getValue().close();
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        for (RenderCache cache : CACHES.values()) {
            cache.close();
        }
        CACHES.clear();
    }

    private static final class RenderCache implements AutoCloseable {

        private final int revision;
        @NotNull
        private final VertexBuffer vertexBuffer;

        private RenderCache(int revision, @NotNull VertexBuffer vertexBuffer) {
            this.revision = revision;
            this.vertexBuffer = vertexBuffer;
        }

        @Override
        public void close() {
            this.vertexBuffer.close();
        }
    }
}
