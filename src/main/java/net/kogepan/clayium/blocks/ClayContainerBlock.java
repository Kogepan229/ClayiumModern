package net.kogepan.clayium.blocks;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;
import net.kogepan.clayium.blockentities.TestClayContainerBlockEntity;
import net.kogepan.clayium.registries.ClayiumItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.lowdragmc.lowdraglib2.gui.factory.BlockUIMenuType;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClayContainerBlock extends Block implements EntityBlock, BlockUIMenuType.BlockUI {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty PIPE = BooleanProperty.create("pipe");
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    public static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    public static final VoxelShape ARM_NORTH = Block.box(5, 5, 0, 11, 11, 5);
    public static final VoxelShape ARM_SOUTH = Block.box(5, 5, 11, 11, 11, 16);
    public static final VoxelShape ARM_WEST = Block.box(0, 5, 5, 5, 11, 11);
    public static final VoxelShape ARM_EAST = Block.box(11, 5, 5, 16, 11, 11);
    public static final VoxelShape ARM_UP = Block.box(5, 11, 5, 11, 16, 11);
    public static final VoxelShape ARM_DOWN = Block.box(5, 0, 5, 11, 5, 11);

    public int tier;

    public ClayContainerBlock(int tier) {
        super(BlockBehaviour.Properties.of().dynamicShape());
        this.tier = tier;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PIPE, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new TestClayContainerBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(PIPE);
        for (Direction direction : Direction.values()) {
            builder.add(getProperty(direction));
        }
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ClayContainerBlockEntity container) {
                container.onPlacedByServer(placer, stack);
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @NotNull
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @NotNull
    public BlockState mirror(@NotNull BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    @NotNull
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                              @NotNull BlockPos pos, @NotNull Player player,
                                              @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (stack.is(ClayiumItems.CLAY_ROLLING_PIN)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ClayContainerBlockEntity container) {
                container.cycleInputMode(this.getHitDirection(state, pos, hitResult));
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (stack.is(ClayiumItems.CLAY_SLICER)) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ClayContainerBlockEntity container) {
                container.cycleOutputMode(this.getHitDirection(state, pos, hitResult));
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (stack.is(ClayiumItems.CLAY_SPATULA)) {
            this.togglePipe(level, pos, state);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                               @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            BlockUIMenuType.openUI(serverPlayer, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ModularUI createUI(BlockUIMenuType.BlockUIHolder holder) {
        if (holder.player.level().getBlockEntity(holder.pos) instanceof ClayContainerBlockEntity entity) {
            return entity.createUI(holder);
        }

        return null;
    }

    @NotNull
    private Direction getHitDirection(@NotNull BlockState state, @NotNull BlockPos pos,
                                      @NotNull BlockHitResult hitResult) {
        if (!state.getValue(PIPE)) {
            return hitResult.getDirection();
        }

        Direction armDirection = getHitArm(hitResult.getLocation(), pos);
        if (armDirection != null) {
            return armDirection;
        }

        return hitResult.getDirection();
    }

    private void togglePipe(@NotNull Level level,
                            @NotNull BlockPos pos, BlockState state) {
        if (state.getValue(PIPE)) {
            state = state.setValue(PIPE, false);
            for (Direction direction : Direction.values()) {
                state = state.setValue(
                        ClayContainerBlock.getProperty(direction),
                        false);
            }
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        } else {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ClayContainerBlockEntity container) {
                state = state.setValue(PIPE, true);
                state = container.updatePipeConnectionState(state);

                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
        }
    }

    @Override
    @NotNull
    protected BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction,
                                     @NotNull BlockState neighborState, @NotNull LevelAccessor level,
                                     @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (level.isClientSide() || !state.getValue(PIPE)) return state;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ClayContainerBlockEntity container) {
            return state.setValue(
                    getProperty(direction),
                    container.canConnectTo(direction));
        }

        return state;
    }

    @Override
    @NotNull
    protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                  @NotNull CollisionContext context) {
        if (!state.getValue(PIPE)) {
            return super.getShape(state, level, pos, context);
        }

        VoxelShape shape = CORE;
        for (Direction direction : Direction.values()) {
            if (state.getValue(getProperty(direction))) {
                shape = Shapes.or(shape, getArmShape(direction));
            }
        }
        return shape;
    }

    @Override
    @NotNull
    protected VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos) {
        return state.getValue(PIPE) ? Shapes.empty() : super.getOcclusionShape(state, level, pos);
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return state.getValue(PIPE);
    }

    @Nullable
    private Direction getHitArm(@NotNull Vec3 hit, @NotNull BlockPos pos) {
        Vec3 local = hit.subtract(pos.getX(), pos.getY(), pos.getZ());

        if (ARM_NORTH.bounds().contains(local)) return Direction.NORTH;
        if (ARM_SOUTH.bounds().contains(local)) return Direction.SOUTH;
        if (ARM_WEST.bounds().contains(local)) return Direction.WEST;
        if (ARM_EAST.bounds().contains(local)) return Direction.EAST;
        if (ARM_UP.bounds().contains(local)) return Direction.UP;
        if (ARM_DOWN.bounds().contains(local)) return Direction.DOWN;

        return null; // core
    }

    public static BooleanProperty getProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private static VoxelShape getArmShape(Direction dir) {
        return switch (dir) {
            case NORTH -> ARM_NORTH;
            case SOUTH -> ARM_SOUTH;
            case WEST -> ARM_WEST;
            case EAST -> ARM_EAST;
            case UP -> ARM_UP;
            case DOWN -> ARM_DOWN;
        };
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
                                                                                                         @NotNull Level level,
                                                                                                         @NotNull BlockEntityType<A> givenType,
                                                                                                         @NotNull BlockEntityType<E> expectedType,
                                                                                                         @Nullable BlockEntityTicker<? super E> serverTicker,
                                                                                                         @Nullable BlockEntityTicker<? super E> clientTicker) {
        return givenType == expectedType ?
                level.isClientSide() ? (BlockEntityTicker<A>) clientTicker : (BlockEntityTicker<A>) serverTicker : null;
    }
}
