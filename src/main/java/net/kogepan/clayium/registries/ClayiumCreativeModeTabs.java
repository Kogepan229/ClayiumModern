package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            Clayium.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(
            "clayium_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.clayium"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(Items.CLAY_BALL::getDefaultInstance)
                    .displayItems(ClayiumItems.ITEMS.getEntries())
                    .build());

    private ClayiumCreativeModeTabs() {}
}
