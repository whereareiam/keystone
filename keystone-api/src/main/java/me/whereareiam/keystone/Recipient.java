package me.whereareiam.keystone;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * How Keystone sees one receiver of texts: a player, the console or another command sender.
 * <p>
 * A {@link Messenger} turns each of the plugin's senders into a recipient. A platform's messenger does so for the
 * platform's own senders; a plugin that keeps its own player type gives the messenger a function that returns a
 * recipient for it. Language resolvers and placeholders receive the recipient a text is rendered for.
 * <pre>{@code
 * Messenger<SocialismusPlayer> messenger = PaperKeystone.messenger(plugin, player -> new Recipient() {
 *     public String name() { return player.getUsername(); }
 *     public Optional<UUID> uniqueId() { return Optional.of(player.getUniqueId()); }
 *     public Optional<Locale> clientLanguage() { return Optional.ofNullable(player.getLocale()); }
 *     public Audience audience() { return player.getAudience(); }
 * }).semantica(semantica).build();
 * }</pre>
 */
public interface Recipient {
	/**
	 * Returns the recipient's name, such as a player's name or {@code CONSOLE}.
	 *
	 * @return the name
	 */
	@NotNull String name();

	/**
	 * Returns the unique id of a player. Texts are held only for recipients with an id.
	 *
	 * @return the id, or empty for the console and other senders that are no player
	 */
	@NotNull Optional<UUID> uniqueId();

	/**
	 * Returns the language the recipient's client reports.
	 *
	 * @return the language, or empty for the console and senders without a client
	 */
	@NotNull Optional<Locale> clientLanguage();

	/**
	 * Returns the audience the recipient's texts are shown to.
	 *
	 * @return the audience
	 */
	@NotNull Audience audience();

	/**
	 * Returns whether the recipient is the server's console. Texts to the console are written to the plugin's log,
	 * with ANSI colours, instead of being shown to its audience.
	 *
	 * @return {@code true} for the console
	 */
	default boolean isConsole() {
		return false;
	}
}
