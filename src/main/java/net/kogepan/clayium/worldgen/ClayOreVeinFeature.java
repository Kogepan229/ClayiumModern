package net.kogepan.clayium.worldgen;

import net.kogepan.clayium.registries.ClayiumBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Custom feature that generates Clay Ore veins and overlays them with
 * Dense and Large Dense Clay Ore in a way that closely matches the
 * original Clayium ClayOreGenerator behaviour.
 */
public class ClayOreVeinFeature extends Feature<ClayOreVeinConfiguration> {

    public ClayOreVeinFeature() {
        super(ClayOreVeinConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<ClayOreVeinConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource randomSource = context.random();
        BlockPos origin = context.origin();
        ClayOreVeinConfiguration cfg = context.config();

        boolean placedAny = false;

        // Use java.util.Random for simple symmetric offsets based on the worldgen RNG
        Random random = new Random(randomSource.nextLong());

        // Keep track of all positions where normal Clay Ore was actually placed
        List<BlockPos> normalOrePositions = new ArrayList<>(cfg.normalSize());

        // 1. Generate normal Clay Ore in stone
        for (int i = 0; i < cfg.normalSize(); i++) {
            BlockPos pos = offsetAround(origin, random, 4);
            if (level.getBlockState(pos).is(Blocks.STONE)) {
                level.setBlock(pos, ClayiumBlocks.CLAY_ORE.get().defaultBlockState(), 2);
                normalOrePositions.add(pos.immutable());
                placedAny = true;
            }
        }

        // 2. Overlay with Dense Clay Ore by replacing previously placed Clay Ore
        // This guarantees that dense ore always sits on top of existing clay ore.
        if (!normalOrePositions.isEmpty()) {
            for (int i = 0; i < cfg.denseSize(); i++) {
                BlockPos pos = normalOrePositions.get(random.nextInt(normalOrePositions.size()));
                if (level.getBlockState(pos).is(ClayiumBlocks.CLAY_ORE.get())) {
                    level.setBlock(pos, ClayiumBlocks.DENSE_CLAY_ORE.get().defaultBlockState(), 2);
                }
            }
        }

        // 3. Upgrade a specific offset position to Large Dense Clay Ore with some probability
        BlockPos largePos = origin.offset(8, 0, 8);
        if (level.getBlockState(largePos).is(ClayiumBlocks.DENSE_CLAY_ORE.get()) &&
                random.nextDouble() < cfg.largeDenseChance()) {
            level.setBlock(largePos, ClayiumBlocks.LARGE_DENSE_CLAY_ORE.get().defaultBlockState(), 2);
        }

        return placedAny;
    }

    private static BlockPos offsetAround(BlockPos origin, Random random, int radius) {
        int dx = random.nextInt(radius * 2 + 1) - radius;
        int dy = random.nextInt(radius * 2 + 1) - radius;
        int dz = random.nextInt(radius * 2 + 1) - radius;
        return origin.offset(dx, dy, dz);
    }
}
