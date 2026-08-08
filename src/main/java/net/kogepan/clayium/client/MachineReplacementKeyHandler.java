package net.kogepan.clayium.client;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.network.payload.SprintKeyStatePayload;
import net.kogepan.clayium.registries.ClayiumAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Clayium.MODID, value = Dist.CLIENT)
public final class MachineReplacementKeyHandler {

    private static LocalPlayer trackedPlayer;
    private static boolean lastSentState;

    private MachineReplacementKeyHandler() {}

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        synchronize(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() == Minecraft.getInstance().player) {
            synchronize(false);
        }
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        trackedPlayer = null;
        lastSentState = false;
    }

    @SubscribeEvent
    public static void onClientClone(ClientPlayerNetworkEvent.Clone event) {
        trackedPlayer = null;
        synchronize(true);
    }

    private static void synchronize(boolean force) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || player.connection == null) {
            return;
        }

        boolean active = minecraft.options.keySprint.isDown();
        player.setData(ClayiumAttachments.SPRINT_KEY_ACTIVE, active);
        if (force || player != trackedPlayer || active != lastSentState) {
            trackedPlayer = player;
            lastSentState = active;
            PacketDistributor.sendToServer(new SprintKeyStatePayload(active));
        }
    }
}
