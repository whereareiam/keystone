package me.whereareiam.keystone.integration.placeholderapi;

import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI on Bukkit and Paper: its {@code %placeholders%} are filled in the literal parts of each text, for
 * the player who receives it. Installed when PlaceholderAPI is enabled before the plugin builds Keystone, so the
 * plugin lists it under {@code softdepend}.
 */
public final class PlaceholderApiIntegration implements Integration {
	@Override
	public @NotNull String plugin() {
		return "PlaceholderAPI";
	}

	@Override
	public void install(@NotNull Installation installation) {
		installation.placeholders(new PlaceholderApiPlaceholders());
	}
}
