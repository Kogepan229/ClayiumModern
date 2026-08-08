package net.kogepan.clayium.api.machine.replacement;

import net.kogepan.clayium.blockentities.ClayContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public record MachineReplacementContext(
                                        ServerLevel level,
                                        BlockPos pos,
                                        Player player,
                                        InteractionHand hand,
                                        BlockHitResult hitResult,
                                        BlockState sourceState,
                                        BlockState targetState,
                                        ClayContainerBlockEntity source,
                                        ClayContainerBlockEntity target) {}
