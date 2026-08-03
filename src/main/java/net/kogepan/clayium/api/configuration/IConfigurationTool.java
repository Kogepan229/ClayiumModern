package net.kogepan.clayium.api.configuration;

import org.jetbrains.annotations.NotNull;

/** Item capability exposed by tools that configure machines. */
public interface IConfigurationTool {

    /**
     * Returns the operation requested by this tool.
     *
     * @param secondaryUse whether the player is holding the secondary-use key
     */
    @NotNull
    ConfigurationToolAction getAction(boolean secondaryUse);

    /** Returns whether holding the tool should render machine IO overlays. */
    default boolean rendersMachineIOOverlay() {
        return false;
    }
}
