package net.kogepan.clayium.worldgen;

import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Configuration for {@link ClayOreVeinFeature}.
 *
 * <p>
 * This mirrors the behaviour of the original Clayium ClayOreGenerator by
 * allowing control over the normal ore vein size, the dense overlay size, and
 * the probability of upgrading a dense block to a large dense block at a fixed
 * offset.
 * </p>
 */
public record ClayOreVeinConfiguration(
                                       int normalSize,
                                       int denseSize,
                                       double largeDenseChance)
        implements FeatureConfiguration {

    public static final Codec<ClayOreVeinConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 64)
                    .fieldOf("normal_size")
                    .forGetter(ClayOreVeinConfiguration::normalSize),
            Codec.intRange(0, 64)
                    .fieldOf("dense_size")
                    .forGetter(ClayOreVeinConfiguration::denseSize),
            Codec.doubleRange(0.0, 1.0)
                    .fieldOf("large_dense_chance")
                    .forGetter(ClayOreVeinConfiguration::largeDenseChance))
            .apply(instance, ClayOreVeinConfiguration::new));
}
