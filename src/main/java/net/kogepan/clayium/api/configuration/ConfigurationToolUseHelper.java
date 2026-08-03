package net.kogepan.clayium.api.configuration;

import net.kogepan.clayium.capability.ClayiumCapabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Coordinates configuration-tool capability lookup and use. */
public final class ConfigurationToolUseHelper {

    private static final List<Direction> FALLBACK_ROTATION_AXES = List.of(
            Direction.UP,
            Direction.DOWN,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST);

    private ConfigurationToolUseHelper() {}

    /**
     * Handles a configuration tool's {@code onItemUseFirst} call.
     *
     * <p>
     * The target capability is consulted on both sides so an accepted operation can return {@link
     * InteractionResult#SUCCESS} before normal block interaction opens a GUI. {@link IMachineConfigurable#configure}
     * is called on the logical server only. Rotation falls back to generic block-state rotation only when the target
     * capability does not handle it.
     */
    @NotNull
    public static InteractionResult useConfigurationTool(@NotNull ItemStack stack,
                                                         @NotNull UseOnContext context) {
        IConfigurationTool tool = stack.getCapability(ClayiumCapabilities.CONFIGURATION_TOOL);
        if (tool == null) {
            return InteractionResult.PASS;
        }

        ConfigurationToolAction action = tool.getAction(context.isSecondaryUseActive());
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        IMachineConfigurable configurable = ClayiumCapabilities.MACHINE_CONFIGURABLE.getCapability(
                level, pos, state, level.getBlockEntity(pos), null);
        if (configurable != null && configurable.canConfigure(action, context)) {
            if (!level.isClientSide()) {
                configurable.configure(action, context);
            }
            return InteractionResult.SUCCESS;
        }

        if (action == ConfigurationToolAction.ROTATION) {
            return rotateGenericBlock(context);
        }
        return InteractionResult.PASS;
    }

    /** Rotates a block using its exposed block-state orientation. */
    @NotNull
    public static InteractionResult rotateGenericBlock(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        BlockState rotatedState = rotateGenericBlock(state, level, pos, context.getClickedFace());
        if (rotatedState == state) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, rotatedState, Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    /** Applies the Clay machine clicked-face orientation rule. */
    @NotNull
    public static BlockState rotateFacingFromSide(@NotNull BlockState state,
                                                  @NotNull DirectionProperty facingProperty,
                                                  @NotNull Direction clickedSide) {
        if (!facingProperty.getPossibleValues().contains(clickedSide)) {
            return state;
        }

        Direction currentFacing = state.getValue(facingProperty);
        Direction newFacing = currentFacing == clickedSide ? clickedSide.getOpposite() : clickedSide;
        if (!facingProperty.getPossibleValues().contains(newFacing)) {
            return state;
        }
        return state.setValue(facingProperty, newFacing);
    }

    @NotNull
    private static BlockState rotateGenericBlock(@NotNull BlockState state, @NotNull LevelAccessor level,
                                                 @NotNull BlockPos pos, @NotNull Direction clickedSide) {
        DirectionProperty facingProperty = findDirectionProperty(state);
        if (facingProperty != null) {
            return rotateDirectionProperty(state, facingProperty, clickedSide);
        }

        if (state.hasProperty(BlockStateProperties.AXIS)) {
            Direction.Axis currentAxis = state.getValue(BlockStateProperties.AXIS);
            Direction.Axis rotationAxis = clickedSide.getAxis();
            if (currentAxis == rotationAxis) {
                return state;
            }

            for (Direction.Axis axis : Direction.Axis.values()) {
                if (axis != currentAxis && axis != rotationAxis) {
                    return state.setValue(BlockStateProperties.AXIS, axis);
                }
            }
        }

        Rotation rotation = clickedSide == Direction.DOWN ?
                Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
        return state.rotate(level, pos, rotation);
    }

    @NotNull
    private static BlockState rotateDirectionProperty(@NotNull BlockState state,
                                                      @NotNull DirectionProperty facingProperty,
                                                      @NotNull Direction clickedSide) {
        Direction rotationAxis = supportsRotationAxis(facingProperty, clickedSide) ? clickedSide : null;
        if (rotationAxis == null) {
            for (Direction fallbackAxis : FALLBACK_ROTATION_AXES) {
                if (supportsRotationAxis(facingProperty, fallbackAxis)) {
                    rotationAxis = fallbackAxis;
                    break;
                }
            }
        }
        if (rotationAxis == null) {
            return state;
        }

        Direction rotatedFacing = rotateAroundAxis(state.getValue(facingProperty), rotationAxis);
        if (!facingProperty.getPossibleValues().contains(rotatedFacing)) {
            return state;
        }
        return state.setValue(facingProperty, rotatedFacing);
    }

    private static boolean supportsRotationAxis(@NotNull DirectionProperty facingProperty,
                                                @NotNull Direction rotationAxis) {
        for (Direction facing : facingProperty.getPossibleValues()) {
            if (!facingProperty.getPossibleValues().contains(rotateAroundAxis(facing, rotationAxis))) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    private static Direction rotateAroundAxis(@NotNull Direction facing, @NotNull Direction rotationAxis) {
        return rotationAxis.getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                facing.getClockWise(rotationAxis.getAxis()) : facing.getCounterClockWise(rotationAxis.getAxis());
    }

    @Nullable
    private static DirectionProperty findDirectionProperty(@NotNull BlockState state) {
        DirectionProperty fallback = null;
        for (Property<?> property : state.getProperties()) {
            if (property instanceof DirectionProperty directionProperty) {
                if ("facing".equals(directionProperty.getName())) {
                    return directionProperty;
                }
                if (fallback == null) {
                    fallback = directionProperty;
                }
            }
        }
        return fallback;
    }
}
