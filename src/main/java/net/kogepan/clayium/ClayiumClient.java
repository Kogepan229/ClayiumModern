package net.kogepan.clayium;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.machine.LaserReflectorBlockEntity;
import net.kogepan.clayium.capability.IClayLaserSource;
import net.kogepan.clayium.client.model.ModelTextures;
import net.kogepan.clayium.client.model.PipeOverlayQuads;
import net.kogepan.clayium.client.model.block.ClayContainerModelLoader;
import net.kogepan.clayium.client.renderer.ClayContainerRenderer;
import net.kogepan.clayium.client.renderer.ClayLaserRenderer;
import net.kogepan.clayium.client.renderer.LaserReflectorBEWLR;
import net.kogepan.clayium.client.renderer.LaserReflectorRenderer;
import net.kogepan.clayium.registries.ClayiumBlockEntityTypes;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.utils.CEUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import org.jetbrains.annotations.NotNull;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Clayium.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with
// @SubscribeEvent
@EventBusSubscriber(modid = Clayium.MODID, value = Dist.CLIENT)
public class ClayiumClient {

    public ClayiumClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Clayium.LOGGER.info("HELLO FROM CLIENT SETUP");
        Clayium.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        event.enqueueWork(() -> {
            ItemColors itemColors = Minecraft.getInstance().getItemColors();
            for (var holder : ColoredIngotTints.getRegisteredHolders()) {
                itemColors.register(
                        (stack, tintIndex) -> ColoredIngotTints.getColor(holder, tintIndex),
                        holder.get());
            }
            for (var holder : ColoredPlateTints.getRegisteredHolders()) {
                itemColors.register(
                        (stack, tintIndex) -> ColoredPlateTints.getColor(holder, tintIndex),
                        holder.get());
            }
            for (var holder : ColoredDustTints.getRegisteredHolders()) {
                itemColors.register(
                        (stack, tintIndex) -> ColoredDustTints.getColor(holder, tintIndex),
                        holder.get());
            }
            for (var holder : ColoredMatterTints.getRegisteredHolders()) {
                itemColors.register(
                        (stack, tintIndex) -> ColoredMatterTints.getColor(holder, tintIndex),
                        holder.get());
            }
        });
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ClayContainerModelLoader.ID, ClayContainerModelLoader.INSTANCE);
    }

    @SuppressWarnings({ "deprecation" })
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAtlasStitched(TextureAtlasStitchedEvent event) {
        TextureAtlas atlas = event.getAtlas();
        if (atlas.location() == TextureAtlas.LOCATION_BLOCKS) {
            ModelTextures.initSprites(atlas);
            PipeOverlayQuads.initialize(atlas);
        }
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {

            private final LaserReflectorBEWLR renderer = new LaserReflectorBEWLR(
                    Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                    Minecraft.getInstance().getEntityModels());

            @Override
            @NotNull
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, ClayiumBlocks.LASER_REFLECTOR.get().asItem());
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (DeferredHolder<BlockEntityType<?>, ?> holder : ClayiumBlockEntityTypes.CLAY_CONTAINER_BLOCK_ENTITY_TYPES) {
            event.registerBlockEntityRenderer(
                    (BlockEntityType<? extends ClayContainerBlockEntity>) holder.get(),
                    ClayContainerRenderer::new);
        }
        event.registerBlockEntityRenderer(
                ClayiumBlockEntityTypes.LASER_REFLECTOR_BLOCK_ENTITY.get(),
                LaserReflectorRenderer::new);
    }

    /**
     * Renders lasers at AFTER_BLOCK_ENTITIES stage (no depth write, so they are visible through
     * translucent blocks like water).
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }
        var levelRenderer = event.getLevelRenderer();
        var poseStack = event.getPoseStack();
        var camera = event.getCamera().getPosition();
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        levelRenderer.iterateVisibleBlockEntities(blockEntity -> {
            var level = blockEntity.getLevel();
            if (level == null) return;

            boolean drawLaser = blockEntity instanceof IClayLaserSource source && source.getIrradiatingLaser() != null;
            boolean drawReflector = blockEntity instanceof LaserReflectorBlockEntity;
            if (!drawLaser && !drawReflector) return;

            BlockPos pos = blockEntity.getBlockPos();
            int packedLight = LevelRenderer.getLightColor(level, pos);
            int packedOverlay = OverlayTexture.NO_OVERLAY;

            poseStack.pushPose();
            poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
            if (drawLaser) {
                ClayLaserRenderer.renderLaser(
                        (IClayLaserSource) blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
            }
            if (drawReflector) {
                LaserReflectorRenderer.renderLaserReflector(
                        (LaserReflectorBlockEntity) blockEntity, poseStack, bufferSource, packedLight, packedOverlay);
            }
            poseStack.popPose();
        });
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        long ce = CEUtils.getItemEnergy(stack);

        if (ce > 0) {
            event.getToolTip().add(
                    Component.literal("CE: " + CEUtils.formatCE(ce))
                            .withStyle(ChatFormatting.GRAY));
        }
    }
}
