package net.kogepan.clayium;

import net.kogepan.clayium.items.tools.ClaySteelToolData;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue PROGRESSION_RATE = COMMON_BUILDER
            .gameRestart()
            .comment("Progression speed multiplier. Values above 1 speed up progression; values below 1 slow it down.")
            .defineInRange("progressionRate", 1.0D, 0.001D, 9999.0D);

    public static final ModConfigSpec.IntValue LASER_QUALITY = COMMON_BUILDER
            .comment("Visual quality of clay laser beam. 2 is recommended for low-end computers.")
            .defineInRange("laserQuality", 8, 1, 32);

    public static final ModConfigSpec.IntValue MAX_CLAY_LASER_LENGTH = COMMON_BUILDER
            .comment("Maximum length of clay laser beam in blocks.")
            .defineInRange("maxClayLaserLength", 32, 1, 256);

    public static final ModConfigSpec.BooleanValue INTERFACE_TARGET_HIGHLIGHT_OUTLINE = COMMON_BUILDER
            .comment("Whether Clay Interface target highlight renders a box outline.")
            .define("interfaceTargetHighlightOutline", true);

    public static final ModConfigSpec.IntValue CLAY_STEEL_TOOL_RANGE = SERVER_BUILDER
            .worldRestart()
            .comment("Maximum Clay Steel tool mining range and the mode number used for custom mining shapes.")
            .defineInRange("claySteelToolRange", 2, 0, ClaySteelToolData.MAX_RANGE);

    public static final ModConfigSpec.BooleanValue UTILITY_MODE = SERVER_BUILDER
            .worldRestart()
            .comment("Whether free Clay Block placement by Clay Steel tools is disabled.")
            .define("utilityMode", false);

    static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();
    static final ModConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    private Config() {}
}
