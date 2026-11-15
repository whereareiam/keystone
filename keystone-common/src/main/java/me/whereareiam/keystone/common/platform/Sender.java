package me.whereareiam.keystone.common.platform;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A command sender of the platform: a player, the console or another sender.
 */
public interface Sender {
	/**
	 * Returns the sender's name.
	 *
	 * @return the name
	 */
	@NotNull String name();

	/**
	 * Returns the unique id of a player.
	 *
	 * @return the id, or empty when the sender is no player
	 */
	@NotNull Optional<UUID> uniqueId();

	/**
	 * Returns the language the sender's client reports.
	 *
	 * @return the language, or empty when the sender has no client
	 */
	@NotNull Optional<Locale> clientLanguage();

	/**
	 * Tells whether the sender has a permission.
	 *
	 * @param permission the permission
	 * @return {@code true} when they have it
	 */
	boolean hasPermission(@NotNull String permission);

	/**
	 * Returns the audience texts are shown to.
	 *
	 * @return the audience
	 */
	@NotNull Audience audience();

	/**
	 * Tells whether the sender is the server's console.
	 *
	 * @return {@code true} for the console
	 */
	boolean isConsole();
}
