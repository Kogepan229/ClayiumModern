package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.items.tools.ClayPickaxe;
import net.kogepan.clayium.items.tools.ClayShovel;
import net.kogepan.clayium.items.tools.ClayToolTiers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClayiumItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Clayium.MODID);

    public static final DeferredItem<Item> CLAY_STICK = ITEMS.registerItem("clay_stick", Item::new);

    public static final DeferredItem<Item> CLAY_PICKAXE = ITEMS.registerItem("clay_pickaxe", ClayPickaxe::new);
    public static final DeferredItem<Item> CLAY_STEEL_PICKAXE = ITEMS.registerItem("clay_steel_pickaxe",
            p -> new PickaxeItem(ClayToolTiers.CLAY_STEEL_PICKAXE,
                    p.attributes(PickaxeItem.createAttributes(ClayToolTiers.CLAY_STEEL_PICKAXE, 4.0f, -2.8f))));
    public static final DeferredItem<ShovelItem> CLAY_SHOVEL = ITEMS.registerItem("clay_shovel", ClayShovel::new);
    public static final DeferredItem<ShovelItem> CLAY_STEEL_SHOVEL = ITEMS.registerItem("clay_steel_shovel",
            p -> new ShovelItem(ClayToolTiers.CLAY_STEEL_SHOVEL,
                    p.attributes(ShovelItem.createAttributes(ClayToolTiers.CLAY_STEEL_SHOVEL, 3.0f, -3.0f))));
}
