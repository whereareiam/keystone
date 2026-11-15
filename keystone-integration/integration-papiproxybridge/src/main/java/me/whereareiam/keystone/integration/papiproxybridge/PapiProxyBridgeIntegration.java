package me.whereareiam.keystone.integration.papiproxybridge;

import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import net.william278.papiproxybridge.api.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;

/**
 * PAPIProxyBridge on Velocity and BungeeCord: PlaceholderAPI's {@code %placeholders%} are filled by the backend
 * server the receiving player is on. Installed when PAPIProxyBridge is loaded before the plugin builds Keystone.
 */
public final class PapiProxyBridgeIntegration implements Integration {
	@Override
	public @NotNull String plugin() {
		return "PAPIProxyBridge";
	}

	@Override
	public void install(@NotNull Installation installation) {
		installation.placeholders(new PapiProxyBridgePlaceholders(PlaceholderAPI.createInstance()::formatPlaceholders));
	}
}
