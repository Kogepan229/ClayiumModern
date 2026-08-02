package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.gui.factory.ClayCraftingBoardUIMenuType;
import net.kogepan.clayium.gui.factory.ClayInterfaceUIMenuType;
import net.kogepan.clayium.gui.menu.ClayCraftingBoardMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIContainerMenu;

public class ClayiumMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Clayium.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ModularUIContainerMenu>> CLAY_INTERFACE_UI = MENUS
            .register("clay_interface_ui",
                    () -> IMenuTypeExtension.create(ClayInterfaceUIMenuType::create));

    public static final DeferredHolder<MenuType<?>, MenuType<ClayCraftingBoardMenu>> CLAY_CRAFTING_BOARD_UI = MENUS
            .register("clay_crafting_board_ui",
                    () -> IMenuTypeExtension.create(ClayCraftingBoardUIMenuType::create));
}
