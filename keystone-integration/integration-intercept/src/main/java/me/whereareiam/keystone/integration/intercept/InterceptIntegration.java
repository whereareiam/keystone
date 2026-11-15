package me.whereareiam.keystone.integration.intercept;

import me.whereareiam.intercept.InterceptAPI;
import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import org.jetbrains.annotations.NotNull;

/**
 * Intercept on Paper and Velocity: the language a player chose in Intercept comes before their client's language.
 * Installed when Intercept is loaded before the plugin builds Keystone; on Paper the plugin lists Intercept under
 * {@code softdepend}, so it sees Intercept's classes. Intercept's API is asked for each text, so a choice applies once
 * Intercept has started, and nothing is chosen while it is stopped.
 * <p>
 * The plugin must not relocate {@code me.whereareiam.intercept}: the API is Intercept's own, loaded from Intercept.
 */
public final class InterceptIntegration implements Integration {
	@Override
	public @NotNull String plugin() {
		return "Intercept";
	}

	@Override
	public void install(@NotNull Installation installation) {
		installation.resolver(new InterceptLanguages(() -> InterceptAPI.find().map(InterceptAPI::languages)));
	}
}
