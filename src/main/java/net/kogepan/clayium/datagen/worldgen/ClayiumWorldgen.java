package net.kogepan.clayium.datagen.worldgen;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumBlocks;
import net.kogepan.clayium.registries.ClayiumFeatures;
import net.kogepan.clayium.worldgen.ClayOreVeinConfiguration;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ClayiumWorldgen {

    private static final int CLAY_ORE_VEIN_COUNT = 8;
    private static final int CLAY_ORE_VEIN_SIZE = 24;
    private static final int DENSE_CLAY_ORE_VEIN_SIZE = 10;
    private static final double LARGE_DENSE_CLAY_ORE_CHANCE = 0.5;
    private static final int LARGE_DENSE_CLAY_ORE_VEIN_COUNT = 2;
    private static final int LARGE_DENSE_CLAY_ORE_VEIN_SIZE = 6;

    private static final ResourceKey<ConfiguredFeature<?, ?>> CLAY_ORE_VEIN_CONFIGURED = configuredFeatureKey(
            "clay_ore_vein");
    private static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_DENSE_CLAY_ORE_CONFIGURED = configuredFeatureKey(
            "large_dense_clay_ore");
    private static final ResourceKey<PlacedFeature> CLAY_ORE_VEIN_PLACED = placedFeatureKey("clay_ore_vein");
    private static final ResourceKey<PlacedFeature> LARGE_DENSE_CLAY_ORE_PLACED = placedFeatureKey(
            "large_dense_clay_ore");
    private static final ResourceKey<BiomeModifier> ADD_CLAY_ORE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            Clayium.id("add_clay_ore"));

    private ClayiumWorldgen() {}

    public static RegistrySetBuilder createRegistrySetBuilder() {
        return new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, ClayiumWorldgen::bootstrapConfiguredFeatures)
                .add(Registries.PLACED_FEATURE, ClayiumWorldgen::bootstrapPlacedFeatures)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ClayiumWorldgen::bootstrapBiomeModifiers);
    }

    private static void bootstrapConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        FeatureUtils.register(
                context,
                CLAY_ORE_VEIN_CONFIGURED,
                ClayiumFeatures.CLAY_ORE_VEIN.get(),
                new ClayOreVeinConfiguration(
                        CLAY_ORE_VEIN_SIZE,
                        DENSE_CLAY_ORE_VEIN_SIZE,
                        LARGE_DENSE_CLAY_ORE_CHANCE));

        OreConfiguration largeDenseClayOre = new OreConfiguration(
                new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get().defaultBlockState(),
                LARGE_DENSE_CLAY_ORE_VEIN_SIZE);
        FeatureUtils.register(
                context,
                LARGE_DENSE_CLAY_ORE_CONFIGURED,
                Feature.ORE,
                largeDenseClayOre);
    }

    private static void bootstrapPlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(
                context,
                CLAY_ORE_VEIN_PLACED,
                configuredFeatures.getOrThrow(CLAY_ORE_VEIN_CONFIGURED),
                CountPlacement.of(CLAY_ORE_VEIN_COUNT),
                InSquarePlacement.spread(),
                HeightRangePlacement.of(TrapezoidHeight.of(
                        VerticalAnchor.absolute(8),
                        VerticalAnchor.absolute(104),
                        64)),
                BiomeFilter.biome());
        PlacementUtils.register(
                context,
                LARGE_DENSE_CLAY_ORE_PLACED,
                configuredFeatures.getOrThrow(LARGE_DENSE_CLAY_ORE_CONFIGURED),
                CountPlacement.of(LARGE_DENSE_CLAY_ORE_VEIN_COUNT),
                InSquarePlacement.spread(),
                HeightRangePlacement.triangle(
                        VerticalAnchor.absolute(0),
                        VerticalAnchor.absolute(24)),
                BiomeFilter.biome());
    }

    private static void bootstrapBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(
                ADD_CLAY_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(CLAY_ORE_VEIN_PLACED),
                                placedFeatures.getOrThrow(LARGE_DENSE_CLAY_ORE_PLACED)),
                        GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey(String path) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Clayium.id(path));
    }

    private static ResourceKey<PlacedFeature> placedFeatureKey(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Clayium.id(path));
    }
}
