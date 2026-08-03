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
import net.minecraft.world.level.block.state.properties.Property;

import org.jspecify.annotations.Nullable;

import java.util.List;

/** Coordinates configuration-tool and configurable-block capability lookup. */
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
     * Resolves both capabilities before normal block interaction can open a GUI. Target mutation is server-only.
     */
    public static InteractionResult useConfigurationTool(ItemStack stack, UseOnContext context) {
        IConfigurationTool tool = stack.getCapability(ClayiumCapabilities.CONFIGURATION_TOOL);
        if (tool == null) {
            return InteractionResult.PASS;
        }

        ConfigurationToolAction action = tool.getAction(context.isSecondaryUseActive());
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        IMachineConfigurable configurable = level.getCapability(
                ClayiumCapabilities.MACHINE_CONFIGURABLE,
                pos,
                state,
                level.getBlockEntity(pos));
        if (configurable != null && configurable.canConfigure(action, context)) {
            if (!level.isClientSide()) {
                configurable.configure(action, context);
            }
            return successForSide(level);
        }

        if (action == ConfigurationToolAction.ROTATION) {
            return rotateGenericBlock(context);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult rotateGenericBlock(UseOnContext context) {
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
        return successForSide(level);
    }

    /** Applies the Clay machine clicked-face orientation rule to a direction-valued property. */
    public static BlockState rotateFacingFromSide(
                                                  BlockState state,
                                                  Property<Direction> facingProperty,
                                                  Direction clickedSide) {
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

    private static InteractionResult successForSide(Level level) {
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    private static BlockState rotateGenericBlock(
                                                 BlockState state,
                                                 LevelAccessor level,
                                                 BlockPos pos,
                                                 Direction clickedSide) {
        @Nullable
        Property<Direction> facingProperty = findDirectionProperty(state);
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

        Rotation rotation = clickedSide == Direction.DOWN ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
        return state.rotate(level, pos, rotation);
    }

    private static BlockState rotateDirectionProperty(
                                                      BlockState state,
                                                      Property<Direction> facingProperty,
                                                      Direction clickedSide) {
        @Nullable
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

    private static boolean supportsRotationAxis(Property<Direction> facingProperty, Direction rotationAxis) {
        for (Direction facing : facingProperty.getPossibleValues()) {
            if (!facingProperty.getPossibleValues().contains(rotateAroundAxis(facing, rotationAxis))) {
                return false;
            }
        }
        return true;
    }

    private static Direction rotateAroundAxis(Direction facing, Direction rotationAxis) {
        return rotationAxis.getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                facing.getClockWise(rotationAxis.getAxis()) : facing.getCounterClockWise(rotationAxis.getAxis());
    }

    private static @Nullable Property<Direction> findDirectionProperty(BlockState state) {
        @Nullable
        Property<Direction> fallback = null;
        for (Property<?> property : state.getProperties()) {
            if (property.getValueClass() == Direction.class) {
                Property<Direction> directionProperty = castDirectionProperty(property);
                if ("facing".equals(property.getName())) {
                    return directionProperty;
                }
                if (fallback == null) {
                    fallback = directionProperty;
                }
            }
        }
        return fallback;
    }

    @SuppressWarnings("unchecked")
    private static Property<Direction> castDirectionProperty(Property<?> property) {
        return (Property<Direction>) property;
    }
}
