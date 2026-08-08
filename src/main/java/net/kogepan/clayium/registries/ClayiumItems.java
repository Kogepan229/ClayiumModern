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

    public static final DeferredItem<Item> CALCAREOUS_CLAY_DUST = ITEMS.registerItem(
            "calcareous_clay_dust",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> SALT_DUST = ITEMS.registerItem(
            "salt_dust",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> SODIUM_CARBONATE_DUST = ITEMS.registerItem(
            "sodium_carbonate_dust",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> CALCIUM_CHLORIDE_DUST = ITEMS.registerItem(
            "calcium_chloride_dust",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> QUARTZ_DUST = ITEMS.registerItem(
            "quartz_dust",
            properties -> new TieredItem(properties, 4));
    public static final DeferredItem<Item> SILICON_DUST = ITEMS.registerItem(
            "silicon_dust",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> SILICONE_DUST = ITEMS.registerItem(
            "silicone_dust",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> ALUMINIUM_DUST = ITEMS.registerItem(
            "aluminium_dust",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> MAGNESIUM_DUST = ITEMS.registerItem(
            "magnesium_dust",
            Item::new);
    public static final DeferredItem<Item> SODIUM_DUST = ITEMS.registerItem(
            "sodium_dust",
            Item::new);
    public static final DeferredItem<Item> LITHIUM_DUST = ITEMS.registerItem(
            "lithium_dust",
            Item::new);
    public static final DeferredItem<Item> ZIRCONIUM_DUST = ITEMS.registerItem(
            "zirconium_dust",
            Item::new);
    public static final DeferredItem<Item> ZINC_DUST = ITEMS.registerItem(
            "zinc_dust",
            Item::new);
    public static final DeferredItem<Item> MANGANESE_DUST = ITEMS.registerItem(
            "manganese_dust",
            Item::new);
    public static final DeferredItem<Item> POTASSIUM_DUST = ITEMS.registerItem(
            "potassium_dust",
            Item::new);
    public static final DeferredItem<Item> HAFNIUM_DUST = ITEMS.registerItem(
            "hafnium_dust",
            Item::new);
    public static final DeferredItem<Item> STRONTIUM_DUST = ITEMS.registerItem(
            "strontium_dust",
            Item::new);
    public static final DeferredItem<Item> BARIUM_DUST = ITEMS.registerItem(
            "barium_dust",
            Item::new);
    public static final DeferredItem<Item> CALCIUM_DUST = ITEMS.registerItem(
            "calcium_dust",
            Item::new);
    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerItem(
            "iron_dust",
            Item::new);
    public static final DeferredItem<Item> LEAD_DUST = ITEMS.registerItem(
            "lead_dust",
            Item::new);
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerItem(
            "copper_dust",
            Item::new);
    public static final DeferredItem<Item> NICKEL_DUST = ITEMS.registerItem(
            "nickel_dust",
            Item::new);
    public static final DeferredItem<Item> BERYLLIUM_DUST = ITEMS.registerItem(
            "beryllium_dust",
            Item::new);
    public static final DeferredItem<Item> CHROME_DUST = ITEMS.registerItem(
            "chrome_dust",
            Item::new);
    public static final DeferredItem<Item> TITANIUM_DUST = ITEMS.registerItem(
            "titanium_dust",
            Item::new);
    public static final DeferredItem<Item> CLAY_STEEL_DUST = ITEMS.registerItem(
            "clay_steel_dust",
            properties -> new TieredItem(properties, 7));
    public static final DeferredItem<Item> CLAYIUM_DUST = ITEMS.registerItem(
            "clayium_dust",
            properties -> new TieredItem(properties, 8));
    public static final DeferredItem<Item> ULTIMATE_ALLOY_DUST = ITEMS.registerItem(
            "ultimate_alloy_dust",
            properties -> new TieredItem(properties, 9));
    public static final DeferredItem<Item> AZ91D_ALLOY_DUST = ITEMS.registerItem(
            "az91d_alloy_dust",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> ZK60A_ALLOY_DUST = ITEMS.registerItem(
            "zk60a_alloy_dust",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> ANTIMATTER_DUST = ITEMS.registerItem(
            "antimatter_dust",
            properties -> new TieredItem(properties, 10));
    public static final DeferredItem<Item> PURE_ANTIMATTER_DUST = ITEMS.registerItem(
            "pure_antimatter_dust",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> OCTUPLE_PURE_ANTIMATTER_DUST = ITEMS.registerItem(
            "octuple_pure_antimatter_dust",
            properties -> new TieredItem(properties, 13));
    public static final DeferredItem<Item> IMPURE_SILICON_DUST = ITEMS.registerItem(
            "impure_silicon_dust",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> IMPURE_ALUMINIUM_DUST = ITEMS.registerItem(
            "impure_aluminium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_MAGNESIUM_DUST = ITEMS.registerItem(
            "impure_magnesium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_SODIUM_DUST = ITEMS.registerItem(
            "impure_sodium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_LITHIUM_DUST = ITEMS.registerItem(
            "impure_lithium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_ZIRCONIUM_DUST = ITEMS.registerItem(
            "impure_zirconium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_ZINC_DUST = ITEMS.registerItem(
            "impure_zinc_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_MANGANESE_DUST = ITEMS.registerItem(
            "impure_manganese_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_POTASSIUM_DUST = ITEMS.registerItem(
            "impure_potassium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_HAFNIUM_DUST = ITEMS.registerItem(
            "impure_hafnium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_STRONTIUM_DUST = ITEMS.registerItem(
            "impure_strontium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_BARIUM_DUST = ITEMS.registerItem(
            "impure_barium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_CALCIUM_DUST = ITEMS.registerItem(
            "impure_calcium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_IRON_DUST = ITEMS.registerItem(
            "impure_iron_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_LEAD_DUST = ITEMS.registerItem(
            "impure_lead_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_COPPER_DUST = ITEMS.registerItem(
            "impure_copper_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_NICKEL_DUST = ITEMS.registerItem(
            "impure_nickel_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_BERYLLIUM_DUST = ITEMS.registerItem(
            "impure_beryllium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_CHROME_DUST = ITEMS.registerItem(
            "impure_chrome_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_TITANIUM_DUST = ITEMS.registerItem(
            "impure_titanium_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_REDSTONE_DUST = ITEMS.registerItem(
            "impure_redstone_dust",
            Item::new);
    public static final DeferredItem<Item> IMPURE_GLOWSTONE_DUST = ITEMS.registerItem(
            "impure_glowstone_dust",
            Item::new);

    public static final DeferredItem<Item> ANTIMATTER_MATTER = ITEMS.registerItem(
            "antimatter_matter",
            properties -> new TieredItem(properties, 10));
    public static final DeferredItem<Item> PURE_ANTIMATTER_MATTER = ITEMS.registerItem(
            "pure_antimatter_matter",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_1 = ITEMS.registerItem(
            "compressed_pure_antimatter_1",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_2 = ITEMS.registerItem(
            "compressed_pure_antimatter_2",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_3 = ITEMS.registerItem(
            "compressed_pure_antimatter_3",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_4 = ITEMS.registerItem(
            "compressed_pure_antimatter_4",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_5 = ITEMS.registerItem(
            "compressed_pure_antimatter_5",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_6 = ITEMS.registerItem(
            "compressed_pure_antimatter_6",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_7 = ITEMS.registerItem(
            "compressed_pure_antimatter_7",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> COMPRESSED_PURE_ANTIMATTER_8 = ITEMS.registerItem(
            "compressed_pure_antimatter_8",
            properties -> new TieredItem(properties, 13));

    public static final DeferredItem<Item> IMPURE_SILICON_PLATE = ITEMS.registerItem(
            "impure_silicon_plate",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> LARGE_IMPURE_SILICON_PLATE = ITEMS.registerItem(
            "large_impure_silicon_plate",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> SILICONE_PLATE = ITEMS.registerItem(
            "silicone_plate",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> LARGE_SILICONE_PLATE = ITEMS.registerItem(
            "large_silicone_plate",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> SILICON_PLATE = ITEMS.registerItem(
            "silicon_plate",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> LARGE_SILICON_PLATE = ITEMS.registerItem(
            "large_silicon_plate",
            properties -> new TieredItem(properties, 5));
    public static final DeferredItem<Item> ALUMINIUM_PLATE = ITEMS.registerItem(
            "aluminium_plate",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> LARGE_ALUMINIUM_PLATE = ITEMS.registerItem(
            "large_aluminium_plate",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> IMPURE_ALUMINIUM_PLATE = ITEMS.registerItem(
            "impure_aluminium_plate",
            Item::new);
    public static final DeferredItem<Item> LARGE_IMPURE_ALUMINIUM_PLATE = ITEMS.registerItem(
            "large_impure_aluminium_plate",
            Item::new);
    public static final DeferredItem<Item> CLAY_STEEL_PLATE = ITEMS.registerItem(
            "clay_steel_plate",
            properties -> new TieredItem(properties, 7));
    public static final DeferredItem<Item> LARGE_CLAY_STEEL_PLATE = ITEMS.registerItem(
            "large_clay_steel_plate",
            properties -> new TieredItem(properties, 7));
    public static final DeferredItem<Item> CLAYIUM_PLATE = ITEMS.registerItem(
            "clayium_plate",
            properties -> new TieredItem(properties, 8));
    public static final DeferredItem<Item> LARGE_CLAYIUM_PLATE = ITEMS.registerItem(
            "large_clayium_plate",
            properties -> new TieredItem(properties, 8));
    public static final DeferredItem<Item> ULTIMATE_ALLOY_PLATE = ITEMS.registerItem(
            "ultimate_alloy_plate",
            properties -> new TieredItem(properties, 9));
    public static final DeferredItem<Item> LARGE_ULTIMATE_ALLOY_PLATE = ITEMS.registerItem(
            "large_ultimate_alloy_plate",
            properties -> new TieredItem(properties, 9));
    public static final DeferredItem<Item> ANTIMATTER_PLATE = ITEMS.registerItem(
            "antimatter_plate",
            properties -> new TieredItem(properties, 10));
    public static final DeferredItem<Item> LARGE_ANTIMATTER_PLATE = ITEMS.registerItem(
            "large_antimatter_plate",
            properties -> new TieredItem(properties, 10));
    public static final DeferredItem<Item> PURE_ANTIMATTER_PLATE = ITEMS.registerItem(
            "pure_antimatter_plate",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> LARGE_PURE_ANTIMATTER_PLATE = ITEMS.registerItem(
            "large_pure_antimatter_plate",
            properties -> new TieredItem(properties, 11));
    public static final DeferredItem<Item> OCTUPLE_ENERGETIC_CLAY_PLATE = ITEMS.registerItem(
            "octuple_energetic_clay_plate",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> LARGE_OCTUPLE_ENERGETIC_CLAY_PLATE = ITEMS.registerItem(
            "large_octuple_energetic_clay_plate",
            properties -> new TieredItem(properties, 12));
    public static final DeferredItem<Item> OCTUPLE_PURE_ANTIMATTER_PLATE = ITEMS.registerItem(
            "octuple_pure_antimatter_plate",
            properties -> new TieredItem(properties, 13));
    public static final DeferredItem<Item> LARGE_OCTUPLE_PURE_ANTIMATTER_PLATE = ITEMS.registerItem(
            "large_octuple_pure_antimatter_plate",
            properties -> new TieredItem(properties, 13));
    public static final DeferredItem<Item> AZ91D_ALLOY_PLATE = ITEMS.registerItem(
            "az91d_alloy_plate",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> LARGE_AZ91D_ALLOY_PLATE = ITEMS.registerItem(
            "large_az91d_alloy_plate",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> ZK60A_ALLOY_PLATE = ITEMS.registerItem(
            "zk60a_alloy_plate",
            properties -> new TieredItem(properties, 6));
    public static final DeferredItem<Item> LARGE_ZK60A_ALLOY_PLATE = ITEMS.registerItem(
            "large_zk60a_alloy_plate",
            properties -> new TieredItem(properties, 6));

    private ClayiumItems() {}
}
