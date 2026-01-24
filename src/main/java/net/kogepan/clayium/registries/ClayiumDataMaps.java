package net.kogepan.clayium.registries;

import net.kogepan.clayium.Clayium;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

import com.mojang.serialization.Codec;

public class ClayiumDataMaps {

    public static final DataMapType<Item, Long> CLAY_ENERGY = DataMapType.builder(
            Clayium.id("clay_energy"),
            Registries.ITEM,
            Codec.LONG).synced(Codec.LONG, false).build();
}
