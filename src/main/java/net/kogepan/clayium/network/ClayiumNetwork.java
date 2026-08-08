package net.kogepan.clayium.network;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.network.payload.CAReactorWorkingPayload;
import net.kogepan.clayium.network.payload.SprintKeyStatePayload;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Registers Clayium's custom network payloads. */
@EventBusSubscriber(modid = Clayium.MODID)
public final class ClayiumNetwork {

    private static final String PROTOCOL_VERSION = "2";

    private ClayiumNetwork() {}

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(CAReactorWorkingPayload.TYPE, CAReactorWorkingPayload.STREAM_CODEC,
                CAReactorWorkingPayload::handle);
        registrar.playToServer(SprintKeyStatePayload.TYPE, SprintKeyStatePayload.STREAM_CODEC,
                SprintKeyStatePayload::handle);
    }
}
