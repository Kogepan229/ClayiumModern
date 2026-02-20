package net.kogepan.clayium.blocks;

import net.kogepan.clayium.worldgen.ClayTreeGrower;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import com.mojang.serialization.MapCodec;

/**
 * Clay Sapling block. Grows into Clay Tree (clay log + clay leaves) like in ClayiumOriginal:
 * two-stage growth (STAGE 0 → 1 then tree), random tick when light ≥ 9 with 1/7 chance,
 * bonemeal with 45% success. Placed on dirt/grass.
 */
public class ClaySaplingBlock extends BushBlock implements BonemealableBlock {

    public static final MapCodec<ClaySaplingBlock> CODEC = simpleCodec(ClaySaplingBlock::new);

    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;

    public ClaySaplingBlock() {
        super(BlockBehaviour.Properties.of()
                .noCollission()
                .strength(0.0f)
                .sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .randomTicks());
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    public ClaySaplingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            advanceStageOrGrow(level, pos, state, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return (double) level.random.nextFloat() < 0.45D;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        advanceStageOrGrow(level, pos, state, random);
    }

    /**
     * First time: set STAGE to 1. Second time: try to grow tree (like Original growUp/growTree).
     */
    private void advanceStageOrGrow(Level level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.setValue(STAGE, 1), 4);
        } else {
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 4);
            if (!ClayTreeGrower.grow(level, pos, random)) {
                level.setBlock(pos, state, 4);
            }
        }
    }
}
