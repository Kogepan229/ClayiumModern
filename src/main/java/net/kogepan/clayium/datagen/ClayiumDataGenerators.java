package net.kogepan.clayium.datagen;

import net.kogepan.clayium.datagen.recipes.ClayiumRecipeProvider;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class ClayiumDataGenerators {

    public static void gatherData(GatherDataEvent.Client event) {
        event.addProvider(new ClayiumRecipeProvider.Runner(
                event.getGenerator().getPackOutput(),
                event.getLookupProvider()));
    }

    private ClayiumDataGenerators() {}
}
