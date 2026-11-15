package me.whereareiam.keystone.recipient;

import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.model.Values;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * One player, the console or another command sender, to whom texts are sent.
 * <p>
 * Each text is rendered for the recipient alone: in their {@link #languages()}, with the placeholders of this
 * recipient. Adventure's global translator is not used, since it only knows the client's language.
 * <pre>{@code
 * Recipient player = keystone.recipients().of(sender);
 *
 * player.send(messages.cooldown(3.5));                          // display.yml decides its parts
 * player.send(announcement.getChat(), Values.of("player", name)); // a Content from a config
 * player.send(announcement.getDisplay(), Values.none());        // a Display from a config
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
	 * Returns the unique id of a player.
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
	 * Returns the languages texts are rendered in for this recipient, most preferred first: the languages of the
	 * resolvers, the client's language and the fallback.
	 *
	 * @return the languages, never empty
	 */
	@NotNull List<Locale> languages();

	/**
	 * Tells whether the recipient has a permission.
	 *
	 * @param permission the permission
	 * @return {@code true} when they have it
	 */
	boolean hasPermission(@NotNull String permission);

	/**
	 * Returns the Adventure audience Keystone shows texts to, for what Keystone does not show itself.
	 *
	 * @return the audience
	 */
	@NotNull Audience audience();

	/**
	 * Sends a translated message as the parts its display names: the display file's entry under the message's key,
	 * or chat alone when it has none.
	 *
	 * @param message the message and its values
	 */
	void send(@NotNull Message message);

	/**
	 * Sends a text written in a config to the chat.
	 *
	 * @param content the text, which may reference translated messages with {@code {@key}}
	 * @param values  the values its arguments and references see
	 */
	void send(@NotNull Content content, @NotNull Values values);

	/**
	 * Sends a text written in a config to the chat.
	 *
	 * @param content the text, which may reference translated messages with {@code {@key}}
	 */
	default void send(@NotNull Content content) {
		send(content, Values.none());
	}

	/**
	 * Shows each part of a display, such as one an admin wrote in a config.
	 *
	 * @param display the parts
	 * @param values  the values the texts of its parts see
	 */
	void send(@NotNull Display display, @NotNull Values values);

	/**
	 * Shows each part of a display, such as one an admin wrote in a config.
	 *
	 * @param display the parts
	 */
	default void send(@NotNull Display display) {
		send(display, Values.none());
	}
}
