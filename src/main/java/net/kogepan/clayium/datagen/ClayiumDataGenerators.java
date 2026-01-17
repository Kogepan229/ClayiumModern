package net.kogepan.clayium.datagen;

import net.kogepan.clayium.Clayium;
import net.kogepan.clayium.datagen.loot.ClayiumLootTableProvider;
import net.kogepan.clayium.datagen.models.ClayiumBlockModelProvider;
import net.kogepan.clayium.datagen.models.ClayiumItemModelProvider;
import net.kogepan.clayium.datagen.recipes.ClayWorkTableRecipeProvider;
import net.kogepan.clayium.datagen.tags.ClayiumBlockTagsProvider;
import net.kogepan.clayium.datagen.tags.ClayiumItemTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Clayium.MODID)
public class ClayiumDataGenerators {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        PackOutput output = generator.getPackOutput();

        // Tags
        ClayiumBlockTagsProvider blockTagsProvider = generator.addProvider(
                event.includeServer(),
                new ClayiumBlockTagsProvider(output, registries, existingFileHelper));
        generator.addProvider(
                event.includeServer(),
                new ClayiumItemTagsProvider(output, registries, blockTagsProvider.contentsGetter(),
                        existingFileHelper));

        // Models
        generator.addProvider(event.includeClient(),
                new ClayiumBlockModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(),
                new ClayiumItemModelProvider(output, existingFileHelper));

        // Loot
        generator.addProvider(event.includeServer(), new ClayiumLootTableProvider(output, registries));

        // Recipes
        generator.addProvider(event.includeServer(), new ClayWorkTableRecipeProvider(output, registries));
    }
}
