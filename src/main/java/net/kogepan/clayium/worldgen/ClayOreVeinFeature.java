package net.kogepan.clayium.worldgen;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

/**
 * Generates a vanilla-shaped Clay Ore vein and overlays it with a smaller
 * Dense Clay Ore vein at the same origin, matching the generation order used
 * by the original Clayium ore generator.
 */
public class ClayOreVeinFeature extends Feature<ClayOreVeinConfiguration> {

    public ClayOreVeinFeature() {
        super(ClayOreVeinConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<ClayOreVeinConfiguration> context) {
        ClayOreVeinConfiguration configuration = context.config();
        OreConfiguration normalOre = new OreConfiguration(
                new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                ClayiumBlocks.CLAY_ORE.get().defaultBlockState(),
                configuration.normalSize());
        boolean placedAny = Feature.ORE.place(
                normalOre,
                context.level(),
                context.chunkGenerator(),
                context.random(),
                context.origin());

        OreConfiguration denseOre = new OreConfiguration(
                new BlockMatchTest(ClayiumBlocks.CLAY_ORE.get()),
                ClayiumBlocks.DENSE_CLAY_ORE.get().defaultBlockState(),
                configuration.denseSize());
        Feature.ORE.place(
                denseOre,
                context.level(),
                context.chunkGenerator(),
                context.random(),
                context.origin());

        if (context.level().getBlockState(context.origin()).is(ClayiumBlocks.DENSE_CLAY_ORE.get()) &&
                context.random().nextDouble() < configuration.largeDenseChance()) {
            context.level().setBlock(
                    context.origin(),
                    ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get().defaultBlockState(),
                    2);
        }

        return placedAny;
    }
}
