package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ClayiumAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, Clayium.MODID);

    public static final Supplier<AttachmentType<Boolean>> SPRINT_KEY_ACTIVE = ATTACHMENT_TYPES.register(
            "sprint_key_active", () -> AttachmentType.builder(() -> false).build());

    private ClayiumAttachments() {}

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
