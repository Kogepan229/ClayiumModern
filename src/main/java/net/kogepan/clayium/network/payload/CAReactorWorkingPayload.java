package net.kogepan.clayium.network.payload;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.blockentities.machine.CAReactorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Lightweight client-bound update for the CA Reactor's visual working state. */
public record CAReactorWorkingPayload(BlockPos reactorPosition, boolean working) implements CustomPacketPayload {

    public static final Type<CAReactorWorkingPayload> TYPE = new Type<>(Clayium.id("ca_reactor_working"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CAReactorWorkingPayload> STREAM_CODEC = StreamCodec
            .composite(
                    BlockPos.STREAM_CODEC, CAReactorWorkingPayload::reactorPosition,
                    ByteBufCodecs.BOOL, CAReactorWorkingPayload::working,
                    CAReactorWorkingPayload::new);

    public static void handle(CAReactorWorkingPayload payload, IPayloadContext context) {
        if (context.player().level()
                .getBlockEntity(payload.reactorPosition()) instanceof CAReactorBlockEntity reactor) {
            reactor.applyClientWorkingState(payload.working());
        }
    }

    @Override
    public Type<CAReactorWorkingPayload> type() {
        return TYPE;
    }
}
