package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.items.TieredItem;
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

    public static final DeferredItem<Item> CLAY_PICKAXE = ITEMS.registerItem("clay_pickaxe", ClayPickaxe::new);
    public static final DeferredItem<Item> CLAY_STEEL_PICKAXE = ITEMS.registerItem("clay_steel_pickaxe",
            p -> new PickaxeItem(ClayToolTiers.CLAY_STEEL_PICKAXE,
                    p.attributes(PickaxeItem.createAttributes(ClayToolTiers.CLAY_STEEL_PICKAXE, 4.0f, -2.8f))));
    public static final DeferredItem<ShovelItem> CLAY_SHOVEL = ITEMS.registerItem("clay_shovel", ClayShovel::new);
    public static final DeferredItem<ShovelItem> CLAY_STEEL_SHOVEL = ITEMS.registerItem("clay_steel_shovel",
            p -> new ShovelItem(ClayToolTiers.CLAY_STEEL_SHOVEL,
                    p.attributes(ShovelItem.createAttributes(ClayToolTiers.CLAY_STEEL_SHOVEL, 3.0f, -3.0f))));

    // spotless:off

    // Clay Tools
    public static final DeferredItem<Item> CLAY_ROLLING_PIN = ITEMS.registerItem("clay_rolling_pin", p -> new Item(p.durability(60)));
    public static final DeferredItem<Item> CLAY_SPATULA = ITEMS.registerItem("clay_spatula", p -> new Item(p.durability(36)));
    public static final DeferredItem<Item> CLAY_SLICER = ITEMS.registerItem("clay_slicer", p -> new Item(p.durability(60)));

    // Circuits and boards
    public static final DeferredItem<Item> CLAY_CIRCUIT_BOARD = ITEMS.registerItem("clay_circuit_board", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> CEE_BOARD = ITEMS.registerItem("cee_board", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> CLAY_CIRCUIT = ITEMS.registerItem("clay_circuit", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> SIMPLE_CIRCUIT = ITEMS.registerItem("simple_circuit", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> CEE_CIRCUIT = ITEMS.registerItem("cee_circuit", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> BASIC_CIRCUIT = ITEMS.registerItem("basic_circuit", p -> new TieredItem(p, 4));
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = ITEMS.registerItem("advanced_circuit", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> PRECISION_CIRCUIT = ITEMS.registerItem("precision_circuit", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> INTEGRATED_CIRCUIT = ITEMS.registerItem("integrated_circuit", p -> new TieredItem(p, 7));
    public static final DeferredItem<Item> CLAY_CORE = ITEMS.registerItem("clay_core", p -> new TieredItem(p, 8));
    public static final DeferredItem<Item> CLAY_BRAIN = ITEMS.registerItem("clay_brain", p -> new TieredItem(p, 9));
    public static final DeferredItem<Item> CLAY_SPIRIT = ITEMS.registerItem("clay_spirit", p -> new TieredItem(p, 10));
    public static final DeferredItem<Item> CLAY_SOUL = ITEMS.registerItem("clay_soul", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> CLAY_ANIMA = ITEMS.registerItem("clay_anima", p -> new TieredItem(p, 12));
    public static final DeferredItem<Item> CLAY_PSYCHE = ITEMS.registerItem("clay_psyche", p -> new TieredItem(p, 13));

    // Raw Clay Tools
    public static final DeferredItem<Item> RAW_CLAY_ROLLING_PIN = ITEMS.registerItem("raw_clay_rolling_pin", Item::new);
    public static final DeferredItem<Item> RAW_CLAY_SPATULA = ITEMS.registerItem("raw_clay_spatula", Item::new);
    public static final DeferredItem<Item> RAW_CLAY_SLICER = ITEMS.registerItem("raw_clay_slicer", Item::new);

    // Clay Materials
    public static final DeferredItem<Item> CLAY_PLATE = ITEMS.registerItem("clay_plate", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_STICK = ITEMS.registerItem("clay_stick", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> SHORT_CLAY_STICK = ITEMS.registerItem("short_clay_stick", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_RING = ITEMS.registerItem("clay_ring", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> SMALL_CLAY_RING = ITEMS.registerItem("small_clay_ring", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_GEAR = ITEMS.registerItem("clay_gear", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_BLADE = ITEMS.registerItem("clay_blade", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_NEEDLE = ITEMS.registerItem("clay_needle", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_DISC = ITEMS.registerItem("clay_disc", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> SMALL_CLAY_DISC = ITEMS.registerItem("small_clay_disc", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_CYLINDER = ITEMS.registerItem("clay_cylinder", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_PIPE = ITEMS.registerItem("clay_pipe", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> LARGE_CLAY_BALL = ITEMS.registerItem("large_clay_ball", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> LARGE_CLAY_PLATE = ITEMS.registerItem("large_clay_plate", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_GRINDING_HEAD = ITEMS.registerItem("clay_grinding_head", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_BEARING = ITEMS.registerItem("clay_bearing", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_SPINDLE = ITEMS.registerItem("clay_spindle", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_CUTTING_HEAD = ITEMS.registerItem("clay_cutting_head", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_WHEEL = ITEMS.registerItem("clay_wheel", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> CLAY_DUST = ITEMS.registerItem("clay_dust", p -> new TieredItem(p, 1));

    // Dense Clay Materials
    public static final DeferredItem<Item> DENSE_CLAY_PLATE = ITEMS.registerItem("dense_clay_plate", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_STICK = ITEMS.registerItem("dense_clay_stick", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_SHORT_CLAY_STICK = ITEMS.registerItem("dense_short_clay_stick", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_RING = ITEMS.registerItem("dense_clay_ring", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_SMALL_CLAY_RING = ITEMS.registerItem("dense_small_clay_ring", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_GEAR = ITEMS.registerItem("dense_clay_gear", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_BLADE = ITEMS.registerItem("dense_clay_blade", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_NEEDLE = ITEMS.registerItem("dense_clay_needle", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_DISC = ITEMS.registerItem("dense_clay_disc", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_SMALL_CLAY_DISC = ITEMS.registerItem("dense_small_clay_disc", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_CYLINDER = ITEMS.registerItem("dense_clay_cylinder", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_PIPE = ITEMS.registerItem("dense_clay_pipe", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_LARGE_CLAY_PLATE = ITEMS.registerItem("dense_large_clay_plate", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_GRINDING_HEAD = ITEMS.registerItem("dense_clay_grinding_head", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_BEARING = ITEMS.registerItem("dense_clay_bearing", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_SPINDLE = ITEMS.registerItem("dense_clay_spindle", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_CUTTING_HEAD = ITEMS.registerItem("dense_clay_cutting_head", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_WHEEL = ITEMS.registerItem("dense_clay_wheel", p -> new TieredItem(p, 2));
    public static final DeferredItem<Item> DENSE_CLAY_DUST = ITEMS.registerItem("dense_clay_dust", p -> new TieredItem(p, 2));

    public static final DeferredItem<Item> COMPRESSED_CLAY_SHARD = ITEMS.registerItem("compressed_clay_shard", p -> new TieredItem(p, 1));
    public static final DeferredItem<Item> INDUSTRIAL_CLAY_SHARD = ITEMS.registerItem("industrial_clay_shard", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> ADVANCED_INDUSTRIAL_CLAY_SHARD = ITEMS.registerItem("advanced_industrial_clay_shard", p -> new TieredItem(p, 4));

    public static final DeferredItem<Item> INDUSTRIAL_CLAY_DUST = ITEMS.registerItem("industrial_clay_dust", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> ADVANCED_INDUSTRIAL_CLAY_DUST = ITEMS.registerItem("advanced_industrial_clay_dust", p -> new TieredItem(p, 4));
    public static final DeferredItem<Item> ENERGIZED_CLAY_DUST = ITEMS.registerItem("energized_clay_dust", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem("industrial_clay_plate", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> LARGE_INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem("large_industrial_clay_plate", p -> new TieredItem(p, 3));
    public static final DeferredItem<Item> ADVANCED_INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem("advanced_industrial_clay_plate", p -> new TieredItem(p, 4));
    public static final DeferredItem<Item> LARGE_ADVANCED_INDUSTRIAL_CLAY_PLATE = ITEMS.registerItem("large_advanced_industrial_clay_plate", p -> new TieredItem(p, 4));

    // Material dusts (3-layer tinted; grinder output from ingots)
    public static final DeferredItem<Item> IMPURE_SILICON_DUST = ITEMS.registerItem("impure_silicon_dust", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> SILICON_DUST = ITEMS.registerItem("silicon_dust", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> SILICONE_DUST = ITEMS.registerItem("silicone_dust", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> ALUMINIUM_DUST = ITEMS.registerItem("aluminium_dust", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> IMPURE_ALUMINIUM_DUST = ITEMS.registerItem("impure_aluminium_dust", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> CLAY_STEEL_DUST = ITEMS.registerItem("clay_steel_dust", p -> new TieredItem(p, 7));
    public static final DeferredItem<Item> CLAYIUM_DUST = ITEMS.registerItem("clayium_dust", p -> new TieredItem(p, 8));
    public static final DeferredItem<Item> ULTIMATE_ALLOY_DUST = ITEMS.registerItem("ultimate_alloy_dust", p -> new TieredItem(p, 9));
    public static final DeferredItem<Item> AZ91D_ALLOY_DUST = ITEMS.registerItem("az91d_alloy_dust", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> ZK60A_ALLOY_DUST = ITEMS.registerItem("zk60a_alloy_dust", p -> new TieredItem(p, 6));

    // Antimatter dust (3-layer tinted)
    public static final DeferredItem<Item> ANTIMATTER_DUST = ITEMS.registerItem("antimatter_dust", Item::new);
    public static final DeferredItem<Item> PURE_ANTIMATTER_DUST = ITEMS.registerItem("pure_antimatter_dust", Item::new);
    public static final DeferredItem<Item> OCTUPLE_PURE_ANTIMATTER_DUST = ITEMS.registerItem("octuple_pure_antimatter_dust", Item::new);

    public static final DeferredItem<Item> IMPURE_SILICON_INGOT = ITEMS.registerItem("impure_silicon_ingot", Item::new);
    public static final DeferredItem<Item> SILICON_INGOT = ITEMS.registerItem("silicon_ingot", Item::new);

    // Additional ingots (3-layer tinted)
    public static final DeferredItem<Item> SILICONE_INGOT = ITEMS.registerItem("silicone_ingot", Item::new);
    public static final DeferredItem<Item> ALUMINIUM_INGOT = ITEMS.registerItem("aluminium_ingot", Item::new);
    public static final DeferredItem<Item> IMPURE_ALUMINIUM_INGOT = ITEMS.registerItem("impure_aluminium_ingot", Item::new);
    public static final DeferredItem<Item> CLAY_STEEL_INGOT = ITEMS.registerItem("clay_steel_ingot", Item::new);
    public static final DeferredItem<Item> CLAYIUM_INGOT = ITEMS.registerItem("clayium_ingot", Item::new);
    public static final DeferredItem<Item> ULTIMATE_ALLOY_INGOT = ITEMS.registerItem("ultimate_alloy_ingot", Item::new);
    public static final DeferredItem<Item> IMPURE_ULTIMATE_ALLOY_INGOT = ITEMS.registerItem("impure_ultimate_alloy_ingot", Item::new);
    public static final DeferredItem<Item> AZ91D_ALLOY_INGOT = ITEMS.registerItem("az91d_alloy_ingot", Item::new);
    public static final DeferredItem<Item> ZK60A_ALLOY_INGOT = ITEMS.registerItem("zk60a_alloy_ingot", Item::new);

    // Antimatter matter (gem)
    public static final DeferredItem<Item> ANTIMATTER_MATTER = ITEMS.registerItem("antimatter_matter", p -> new TieredItem(p, 10));
    public static final DeferredItem<Item> PURE_ANTIMATTER_MATTER = ITEMS.registerItem("pure_antimatter_matter", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_1 = ITEMS.registerItem("compressed_pure_antimatter_1", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_2 = ITEMS.registerItem("compressed_pure_antimatter_2", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_3 = ITEMS.registerItem("compressed_pure_antimatter_3", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_4 = ITEMS.registerItem("compressed_pure_antimatter_4", p -> new TieredItem(p, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_5 = ITEMS.registerItem("compressed_pure_antimatter_5", p -> new TieredItem(p, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_6 = ITEMS.registerItem("compressed_pure_antimatter_6", p -> new TieredItem(p, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_7 = ITEMS.registerItem("compressed_pure_antimatter_7", p -> new TieredItem(p, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_8 = ITEMS.registerItem("compressed_pure_antimatter_8", p -> new TieredItem(p, 13));

    // Plates with dedicated texture (Impure Silicon)
    public static final DeferredItem<Item> IMPURE_SILICON_PLATE = ITEMS.registerItem("impure_silicon_plate", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> LARGE_IMPURE_SILICON_PLATE = ITEMS.registerItem("large_impure_silicon_plate", p -> new TieredItem(p, 5));

    // Plates with 3-layer tint (plate_base/dark/light or largeplate_*)
    public static final DeferredItem<Item> SILICONE_PLATE = ITEMS.registerItem("silicone_plate", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> LARGE_SILICONE_PLATE = ITEMS.registerItem("large_silicone_plate", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> SILICON_PLATE = ITEMS.registerItem("silicon_plate", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> LARGE_SILICON_PLATE = ITEMS.registerItem("large_silicon_plate", p -> new TieredItem(p, 5));
    public static final DeferredItem<Item> ALUMINIUM_PLATE = ITEMS.registerItem("aluminium_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> LARGE_ALUMINIUM_PLATE = ITEMS.registerItem("large_aluminium_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> IMPURE_ALUMINIUM_PLATE = ITEMS.registerItem("impure_aluminium_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> LARGE_IMPURE_ALUMINIUM_PLATE = ITEMS.registerItem("large_impure_aluminium_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> CLAY_STEEL_PLATE = ITEMS.registerItem("clay_steel_plate", p -> new TieredItem(p, 7));
    public static final DeferredItem<Item> LARGE_CLAY_STEEL_PLATE = ITEMS.registerItem("large_clay_steel_plate", p -> new TieredItem(p, 7));
    public static final DeferredItem<Item> CLAYIUM_PLATE = ITEMS.registerItem("clayium_plate", p -> new TieredItem(p, 8));
    public static final DeferredItem<Item> LARGE_CLAYIUM_PLATE = ITEMS.registerItem("large_clayium_plate", p -> new TieredItem(p, 8));
    public static final DeferredItem<Item> ULTIMATE_ALLOY_PLATE = ITEMS.registerItem("ultimate_alloy_plate", p -> new TieredItem(p, 9));
    public static final DeferredItem<Item> LARGE_ULTIMATE_ALLOY_PLATE = ITEMS.registerItem("large_ultimate_alloy_plate", p -> new TieredItem(p, 9));
    // Antimatter tier (10): plate, large plate
    public static final DeferredItem<Item> ANTIMATTER_PLATE = ITEMS.registerItem("antimatter_plate", p -> new TieredItem(p, 10));
    public static final DeferredItem<Item> LARGE_ANTIMATTER_PLATE = ITEMS.registerItem("large_antimatter_plate", p -> new TieredItem(p, 10));
    // Pure Antimatter tier (11): plate, large plate
    public static final DeferredItem<Item> PURE_ANTIMATTER_PLATE = ITEMS.registerItem("pure_antimatter_plate", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> LARGE_PURE_ANTIMATTER_PLATE = ITEMS.registerItem("large_pure_antimatter_plate", p -> new TieredItem(p, 11));
    public static final DeferredItem<Item> OCTUPLE_ENERGETIC_CLAY_PLATE = ITEMS.registerItem("octuple_energetic_clay_plate", p -> new TieredItem(p, 12));
    public static final DeferredItem<Item> LARGE_OCTUPLE_ENERGETIC_CLAY_PLATE = ITEMS.registerItem("large_octuple_energetic_clay_plate", p -> new TieredItem(p, 12));
    // Octuple Pure Antimatter (OPA) tier (13): plate, large plate
    public static final DeferredItem<Item> OCTUPLE_PURE_ANTIMATTER_PLATE = ITEMS.registerItem("octuple_pure_antimatter_plate", p -> new TieredItem(p, 13));
    public static final DeferredItem<Item> LARGE_OCTUPLE_PURE_ANTIMATTER_PLATE = ITEMS.registerItem("large_octuple_pure_antimatter_plate", p -> new TieredItem(p, 13));
    public static final DeferredItem<Item> AZ91D_ALLOY_PLATE = ITEMS.registerItem("az91d_alloy_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> LARGE_AZ91D_ALLOY_PLATE = ITEMS.registerItem("large_az91d_alloy_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> ZK60A_ALLOY_PLATE = ITEMS.registerItem("zk60a_alloy_plate", p -> new TieredItem(p, 6));
    public static final DeferredItem<Item> LARGE_ZK60A_ALLOY_PLATE = ITEMS.registerItem("large_zk60a_alloy_plate", p -> new TieredItem(p, 6));

    // spotless:on
}
