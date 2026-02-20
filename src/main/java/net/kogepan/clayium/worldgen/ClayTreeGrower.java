package net.kogepan.clayium.worldgen;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

/**
 * Grows a Clay Tree at the given position using the standard {@link Feature#TREE}
 * with {@link TreeConfiguration}. Leaves get correct distance-from-log for decay.
 */
public final class ClayTreeGrower {

    /** Base trunk height. */
    private static final int MIN_HEIGHT = 5;
    /** Random extra height (0..2); total height = 5..7. */
    private static final int HEIGHT_RANDOM = 2;

    /** TreeConfiguration for clay tree: height 5–7, blob foliage, no decorators. */
    private static final TreeConfiguration TREE_CONFIG = new TreeConfiguration.TreeConfigurationBuilder(
            SimpleStateProvider.simple(ClayiumBlocks.CLAY_LOG.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y)),
            new StraightTrunkPlacer(MIN_HEIGHT, 0, HEIGHT_RANDOM), // height = 5 + random(0..2) = 5..7
            SimpleStateProvider.simple(
                    ClayiumBlocks.CLAY_LEAVES.get().defaultBlockState().setValue(LeavesBlock.PERSISTENT, false)),
            new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
            new TwoLayersFeatureSize(1, 0, 1))
            .dirt(SimpleStateProvider.simple(Blocks.DIRT.defaultBlockState()))
            .ignoreVines()
            .build();

    /**
     * Tries to grow a clay tree at the given position. Sapling block must already
     * be removed by the caller. Only works on {@link ServerLevel}; returns false otherwise.
     *
     * @return true if the tree was generated successfully
     */
    public static boolean grow(LevelAccessor level, BlockPos pos, RandomSource random) {
        if (pos.getY() < level.getMinBuildHeight() + 1 ||
                pos.getY() + MIN_HEIGHT + HEIGHT_RANDOM + 1 > level.getMaxBuildHeight()) {
            return false;
        }
        if (!level.getBlockState(pos.below()).is(BlockTags.DIRT)) {
            return false;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        var chunkSource = serverLevel.getChunkSource();
        var generator = chunkSource.getGenerator();
        FeaturePlaceContext<TreeConfiguration> context = new FeaturePlaceContext<>(
                java.util.Optional.empty(),
                serverLevel,
                generator,
                random,
                pos,
                TREE_CONFIG);
        return Feature.TREE.place(context);
    }
}
