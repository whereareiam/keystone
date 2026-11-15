package me.whereareiam.keystone.common.integration;

import org.jetbrains.annotations.NotNull;

/**
 * Another plugin Keystone works with when it is present, such as PlaceholderAPI. The builder asks the platform
 * whether {@link #plugin()} is loaded and only then calls {@link #install(Installation)}, so an integration's class
 * names none of the other plugin's types; the class it installs does.
 */
public interface Integration {
	/**
	 * Returns the plugin this integration needs, as the platform names it, such as {@code PlaceholderAPI}; it is
	 * compared without regard to case.
	 *
	 * @return the plugin's name
	 */
	@NotNull String plugin();

	/**
	 * Adds what the integration offers.
	 *
	 * @param installation where placeholders and language resolvers are added
	 */
	void install(@NotNull Installation installation);
}
