package net.kogepan.clayium.api.configuration;

/** Item capability exposed by tools that request machine configuration operations. */
public interface IConfigurationTool {

    /** Returns the operation requested by this tool for the current use gesture. */
    ConfigurationToolAction getAction(boolean secondaryUse);

    /** Returns whether holding the tool should render machine I/O overlays. */
    default boolean rendersMachineIOOverlay() {
        return false;
    }
}
