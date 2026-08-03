package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.api.configuration.ConfigurationToolItem;
import net.kogepan.clayium.items.ClayConfigurationToolItem;
import net.kogepan.clayium.items.RawClayToolItem;
import net.kogepan.clayium.items.TieredItem;
import net.kogepan.clayium.items.tools.ClayPickaxe;
import net.kogepan.clayium.items.tools.ClayShovel;
import net.kogepan.clayium.items.tools.ClayToolMaterials;
import net.kogepan.clayium.utils.ProgressionRates;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ClayiumItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Clayium.MODID);

    public static final DeferredItem<Item> CLAY_PICKAXE = ITEMS.registerItem("clay_pickaxe", ClayPickaxe::new);
    public static final DeferredItem<Item> CLAY_STEEL_PICKAXE = ITEMS.registerItem(
            "clay_steel_pickaxe",
            properties -> new Item(properties.pickaxe(ClayToolMaterials.CLAY_STEEL, 5.0F, -2.8F)));
    public static final DeferredItem<ShovelItem> CLAY_SHOVEL = ITEMS.registerItem("clay_shovel", ClayShovel::new);
    public static final DeferredItem<ShovelItem> CLAY_STEEL_SHOVEL = ITEMS.registerItem(
            "clay_steel_shovel",
            properties -> new ShovelItem(ClayToolMaterials.CLAY_STEEL, 4.0F, -3.0F, properties));

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

    public static final DeferredItem<Item> CLAY_PLATE = ITEMS.registerItem(
            "clay_plate",
            properties -> new TieredItem(properties, 1));

    private ClayiumItems() {}
}
