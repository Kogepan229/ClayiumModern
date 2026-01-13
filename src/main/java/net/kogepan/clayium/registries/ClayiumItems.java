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

    // spotless:off
    // Clay Materials
    public static final DeferredItem<Item> CLAY_PLATE = ITEMS.registerItem("clay_plate", Item::new);
    public static final DeferredItem<Item> CLAY_STICK = ITEMS.registerItem("clay_stick", Item::new);
    public static final DeferredItem<Item> SHORT_CLAY_STICK = ITEMS.registerItem("short_clay_stick", Item::new);
    public static final DeferredItem<Item> CLAY_RING = ITEMS.registerItem("clay_ring", Item::new);
    public static final DeferredItem<Item> SMALL_CLAY_RING = ITEMS.registerItem("small_clay_ring", Item::new);
    public static final DeferredItem<Item> CLAY_GEAR = ITEMS.registerItem("clay_gear", Item::new);
    public static final DeferredItem<Item> CLAY_BLADE = ITEMS.registerItem("clay_blade", Item::new);
    public static final DeferredItem<Item> CLAY_NEEDLE = ITEMS.registerItem("clay_needle", Item::new);
    public static final DeferredItem<Item> CLAY_DISC = ITEMS.registerItem("clay_disc", Item::new);
    public static final DeferredItem<Item> SMALL_CLAY_DISC = ITEMS.registerItem("small_clay_disc", Item::new);
    public static final DeferredItem<Item> CLAY_CYLINDER = ITEMS.registerItem("clay_cylinder", Item::new);
    public static final DeferredItem<Item> CLAY_PIPE = ITEMS.registerItem("clay_pipe", Item::new);
    public static final DeferredItem<Item> LARGE_CLAY_BALL = ITEMS.registerItem("large_clay_ball", Item::new);
    public static final DeferredItem<Item> LARGE_CLAY_PLATE = ITEMS.registerItem("large_clay_plate", Item::new);
    public static final DeferredItem<Item> CLAY_GRINDING_HEAD= ITEMS.registerItem("clay_grinding_head", Item::new);
    public static final DeferredItem<Item> CLAY_BEARING= ITEMS.registerItem("clay_bearing", Item::new);
    public static final DeferredItem<Item> CLAY_SPINDLE= ITEMS.registerItem("clay_spindle", Item::new);
    public static final DeferredItem<Item> CLAY_CUTTING_HEAD= ITEMS.registerItem("clay_cutting_head", Item::new);
    public static final DeferredItem<Item> CLAY_WHEEL= ITEMS.registerItem("clay_wheel", Item::new);
    public static final DeferredItem<Item> CLAY_DUST= ITEMS.registerItem("clay_dust", Item::new);

    // Dense Clay Materials
    public static final DeferredItem<Item> DENSE_CLAY_PLATE = ITEMS.registerItem("dense_clay_plate", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_STICK = ITEMS.registerItem("dense_clay_stick", Item::new);
    public static final DeferredItem<Item> DENSE_SHORT_CLAY_STICK = ITEMS.registerItem("dense_short_clay_stick", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_RING = ITEMS.registerItem("dense_clay_ring", Item::new);
    public static final DeferredItem<Item> DENSE_SMALL_CLAY_RING = ITEMS.registerItem("dense_small_clay_ring", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_GEAR = ITEMS.registerItem("dense_clay_gear", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_BLADE = ITEMS.registerItem("dense_clay_blade", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_NEEDLE = ITEMS.registerItem("dense_clay_needle", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_DISC = ITEMS.registerItem("dense_clay_disc", Item::new);
    public static final DeferredItem<Item> DENSE_SMALL_CLAY_DISC = ITEMS.registerItem("dense_small_clay_disc", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_CYLINDER = ITEMS.registerItem("dense_clay_cylinder", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_PIPE = ITEMS.registerItem("dense_clay_pipe", Item::new);
    public static final DeferredItem<Item> DENSE_LARGE_CLAY_PLATE = ITEMS.registerItem("dense_large_clay_plate", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_GRINDING_HEAD= ITEMS.registerItem("dense_clay_grinding_head", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_BEARING= ITEMS.registerItem("dense_clay_bearing", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_SPINDLE= ITEMS.registerItem("dense_clay_spindle", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_CUTTING_HEAD= ITEMS.registerItem("dense_clay_cutting_head", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_WHEEL= ITEMS.registerItem("dense_clay_wheel", Item::new);
    public static final DeferredItem<Item> DENSE_CLAY_DUST= ITEMS.registerItem("dense_clay_dust", Item::new);
    // spotless:on

    public static final DeferredItem<Item> CLAY_ROLLING_PIN = ITEMS.registerItem("clay_rolling_pin",
            p -> new Item(p.durability(60)));
    public static final DeferredItem<Item> CLAY_SPATULA = ITEMS.registerItem("clay_spatula",
            p -> new Item(p.durability(36)));
    public static final DeferredItem<Item> CLAY_SLICER = ITEMS.registerItem("clay_slicer",
            p -> new Item(p.durability(60)));

    public static final DeferredItem<Item> CLAY_PICKAXE = ITEMS.registerItem("clay_pickaxe", ClayPickaxe::new);
    public static final DeferredItem<Item> CLAY_STEEL_PICKAXE = ITEMS.registerItem("clay_steel_pickaxe",
            p -> new PickaxeItem(ClayToolTiers.CLAY_STEEL_PICKAXE,
                    p.attributes(PickaxeItem.createAttributes(ClayToolTiers.CLAY_STEEL_PICKAXE, 4.0f, -2.8f))));
    public static final DeferredItem<ShovelItem> CLAY_SHOVEL = ITEMS.registerItem("clay_shovel", ClayShovel::new);
    public static final DeferredItem<ShovelItem> CLAY_STEEL_SHOVEL = ITEMS.registerItem("clay_steel_shovel",
            p -> new ShovelItem(ClayToolTiers.CLAY_STEEL_SHOVEL,
                    p.attributes(ShovelItem.createAttributes(ClayToolTiers.CLAY_STEEL_SHOVEL, 3.0f, -3.0f))));
}
