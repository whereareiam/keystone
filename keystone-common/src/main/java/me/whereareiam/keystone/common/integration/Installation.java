package me.whereareiam.keystone.common.integration;

import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Placeholders;
import org.jetbrains.annotations.NotNull;

/**
 * Where an {@link Integration} adds what it offers.
 */
public interface Installation {
	/**
	 * Adds placeholders, filled before those the plugin adds itself.
	 *
	 * @param placeholders the placeholders
	 */
	void placeholders(@NotNull Placeholders placeholders);

	/**
	 * Adds a language resolver, asked before those the plugin adds itself and before the client's language.
	 *
	 * @param resolver the resolver
	 */
	void resolver(@NotNull LanguageResolver resolver);
}
