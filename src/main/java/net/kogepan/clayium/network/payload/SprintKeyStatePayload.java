package net.kogepan.clayium.network.payload;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.registries.ClayiumAttachments;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.jetbrains.annotations.NotNull;

public record SprintKeyStatePayload(boolean active) implements CustomPacketPayload {

    public static final Type<SprintKeyStatePayload> TYPE = new Type<>(Clayium.id("sprint_key_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SprintKeyStatePayload> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.BOOL, SprintKeyStatePayload::active,
                    SprintKeyStatePayload::new);

    public static void handle(SprintKeyStatePayload payload, IPayloadContext context) {
        context.player().setData(ClayiumAttachments.SPRINT_KEY_ACTIVE, payload.active());
    }

    @Override
    public @NotNull Type<SprintKeyStatePayload> type() {
        return TYPE;
    }
}
