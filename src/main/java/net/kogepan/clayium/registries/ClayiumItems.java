package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.configuration.ConfigurationToolItem;
import net.kogepan.clayium.items.ClayConfigurationToolItem;
import net.kogepan.clayium.items.ClayCoreItem;
import net.kogepan.clayium.items.RawClayToolItem;
import net.kogepan.clayium.items.TieredItem;
import net.kogepan.clayium.items.tools.ClayPickaxe;
import net.kogepan.clayium.items.tools.ClayShovel;
import net.kogepan.clayium.items.tools.ClaySteelPickaxe;
import net.kogepan.clayium.items.tools.ClaySteelShovel;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Clayium.MODID);

    public static final DeferredItem<Item> CLAY_PICKAXE = ITEMS.registerItem("clay_pickaxe", ClayPickaxe::new);
    public static final DeferredItem<ClaySteelPickaxe> CLAY_STEEL_PICKAXE = ITEMS.registerItem(
            "clay_steel_pickaxe", ClaySteelPickaxe::new);
    public static final DeferredItem<ShovelItem> CLAY_SHOVEL = ITEMS.registerItem("clay_shovel", ClayShovel::new);
    public static final DeferredItem<ClaySteelShovel> CLAY_STEEL_SHOVEL = ITEMS.registerItem(
            "clay_steel_shovel", ClaySteelShovel::new);

    public static final DeferredItem<Item> CLAY_ROLLING_PIN = ITEMS.registerItem(
            "clay_rolling_pin",
            properties -> new ClayConfigurationToolItem(
                    properties.durability(ProgressionRates.multiplyInt(60))));
    public static final DeferredItem<Item> CLAY_SPATULA = ITEMS.registerItem(
            "clay_spatula",
            properties -> new ConfigurationToolItem(
                    properties.durability(ProgressionRates.multiplyInt(36))));
    public static final DeferredItem<Item> CLAY_SLICER = ITEMS.registerItem(
            "clay_slicer",
            properties -> new ClayConfigurationToolItem(
                    properties.durability(ProgressionRates.multiplyInt(60))));
    public static final DeferredItem<Item> RAW_CLAY_ROLLING_PIN = ITEMS.registerItem(
            "raw_clay_rolling_pin",
            RawClayToolItem::new);
    public static final DeferredItem<Item> RAW_CLAY_SPATULA = ITEMS.registerItem(
            "raw_clay_spatula",
            RawClayToolItem::new);
    public static final DeferredItem<Item> RAW_CLAY_SLICER = ITEMS.registerItem(
            "raw_clay_slicer",
            RawClayToolItem::new);

    public static final DeferredItem<Item> CLAY_CIRCUIT_BOARD = ITEMS.registerItem(
            "clay_circuit_board",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> CEE_BOARD = ITEMS.registerItem(
            "cee_board",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> CLAY_CIRCUIT = ITEMS.registerItem(
            "clay_circuit",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> SIMPLE_CIRCUIT = ITEMS.registerItem(
            "simple_circuit",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> CEE_CIRCUIT = ITEMS.registerItem(
            "cee_circuit",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> BASIC_CIRCUIT = ITEMS.registerItem(
            "basic_circuit",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = ITEMS.registerItem(
            "advanced_circuit",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> PRECISION_CIRCUIT = ITEMS.registerItem(
            "precision_circuit",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> INTEGRATED_CIRCUIT = ITEMS.registerItem(
            "integrated_circuit",
            properties -> new TieredItem(properties, 7));
    public static final DeferredItem<Item> CLAY_CORE = ITEMS.registerItem("clay_core", ClayCoreItem::new);
    public static final DeferredItem<Item> CLAY_BRAIN = ITEMS.registerItem(
            "clay_brain",
            properties -> new TieredItem(properties, 9));
    public static final DeferredItem<Item> CLAY_SPIRIT = ITEMS.registerItem(
            "clay_spirit",
            properties -> new TieredItem(properties, 10));
    public static final DeferredItem<Item> CLAY_SOUL = ITEMS.registerItem(
            "clay_soul",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> CLAY_ANIMA = ITEMS.registerItem(
            "clay_anima",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> CLAY_PSYCHE = ITEMS.registerItem(
            "clay_psyche",
            properties -> new TieredItem(properties, 13));

    public static final DeferredItem<Item> CLAY_PLATE = ITEMS.registerItem(
            "clay_plate",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_STICK = ITEMS.registerItem(
            "clay_stick",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> SHORT_CLAY_STICK = ITEMS.registerItem(
            "short_clay_stick",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_RING = ITEMS.registerItem(
            "clay_ring",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> SMALL_CLAY_RING = ITEMS.registerItem(
            "small_clay_ring",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_GEAR = ITEMS.registerItem(
            "clay_gear",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_BLADE = ITEMS.registerItem(
            "clay_blade",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_NEEDLE = ITEMS.registerItem(
            "clay_needle",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_DISC = ITEMS.registerItem(
            "clay_disc",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> SMALL_CLAY_DISC = ITEMS.registerItem(
            "small_clay_disc",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_CYLINDER = ITEMS.registerItem(
            "clay_cylinder",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_PIPE = ITEMS.registerItem(
            "clay_pipe",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> LARGE_CLAY_BALL = ITEMS.registerItem(
            "large_clay_ball",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> LARGE_CLAY_PLATE = ITEMS.registerItem(
            "large_clay_plate",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_GRINDING_HEAD = ITEMS.registerItem(
            "clay_grinding_head",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_BEARING = ITEMS.registerItem(
            "clay_bearing",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_SPINDLE = ITEMS.registerItem(
            "clay_spindle",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_CUTTING_HEAD = ITEMS.registerItem(
            "clay_cutting_head",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_WHEEL = ITEMS.registerItem(
            "clay_wheel",
            properties -> new TieredItem(properties, 1));
    public static final DeferredItem<Item> CLAY_DUST = ITEMS.registerItem(
            "clay_dust",
            properties -> new TieredItem(properties, 1));

    public static final DeferredItem<Item> DENSE_CLAY_PLATE = ITEMS.registerItem(
            "dense_clay_plate",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_STICK = ITEMS.registerItem(
            "dense_clay_stick",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_SHORT_CLAY_STICK = ITEMS.registerItem(
            "dense_short_clay_stick",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_RING = ITEMS.registerItem(
            "dense_clay_ring",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_SMALL_CLAY_RING = ITEMS.registerItem(
            "dense_small_clay_ring",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_GEAR = ITEMS.registerItem(
            "dense_clay_gear",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_BLADE = ITEMS.registerItem(
            "dense_clay_blade",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_NEEDLE = ITEMS.registerItem(
            "dense_clay_needle",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_DISC = ITEMS.registerItem(
            "dense_clay_disc",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_SMALL_CLAY_DISC = ITEMS.registerItem(
            "dense_small_clay_disc",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_CYLINDER = ITEMS.registerItem(
            "dense_clay_cylinder",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_PIPE = ITEMS.registerItem(
            "dense_clay_pipe",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_LARGE_CLAY_PLATE = ITEMS.registerItem(
            "dense_large_clay_plate",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_GRINDING_HEAD = ITEMS.registerItem(
            "dense_clay_grinding_head",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_BEARING = ITEMS.registerItem(
            "dense_clay_bearing",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_SPINDLE = ITEMS.registerItem(
            "dense_clay_spindle",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_CUTTING_HEAD = ITEMS.registerItem(
            "dense_clay_cutting_head",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_WHEEL = ITEMS.registerItem(
            "dense_clay_wheel",
            properties -> new TieredItem(properties, 2));
    public static final DeferredItem<Item> DENSE_CLAY_DUST = ITEMS.registerItem(
            "dense_clay_dust",
            properties -> new TieredItem(properties, 2));

    public static final DeferredItem<Item> COMPRESSED_CLAY_SHARD = ITEMS.registerItem(
            "compressed_clay_shard",
            Item::new);
    public static final DeferredItem<Item> INDUSTRIAL_CLAY_SHARD = ITEMS.registerItem(
            "industrial_clay_shard",
            Item::new);
    public static final DeferredItem<Item> ADVANCED_INDUSTRIAL_CLAY_SHARD = ITEMS.registerItem(
            "advanced_industrial_clay_shard",
            Item::new);
    public static final DeferredItem<Item> INDUSTRIAL_CLAY_DUST = ITEMS.registerItem(
            "industrial_clay_dust",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> ADVANCED_INDUSTRIAL_CLAY_DUST = ITEMS.registerItem(
            "advanced_industrial_clay_dust",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> ENERGIZED_CLAY_DUST = ITEMS.registerItem(
            "energized_clay_dust",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem(
            "industrial_clay_plate",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> LARGE_INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem(
            "large_industrial_clay_plate",
            properties -> new TieredItem(properties, 3));
    public static final DeferredItem<Item> ADVANCED_INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem(
            "advanced_industrial_clay_plate",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> LARGE_ADVANCED_INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem(
            "large_advanced_industrial_clay_plate",
            properties -> new TieredItem(properties, 4));

    private ClayiumItems() {}
}
