package me.whereareiam.keystone;

import me.whereareiam.keystone.recipient.Recipient;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

/**
 * Placeholders of another plugin or of the plugin using Keystone, filled in for each recipient.
 * <p>
 * They apply to what a translator or an admin wrote only: the literal parts of a text, never the values a plugin
 * passes in. So {@code %player_name%} in a translated text is replaced, while the same characters typed by a player
 * into a chat message that becomes a value stay as typed.
 * <p>
 * Keystone ships PlaceholderAPI and PAPIProxyBridge as {@link Text}, and MiniPlaceholders as {@link Tags}; a plugin
 * adds its own through the builder:
 * <pre>{@code
 * PaperKeystone.builder(plugin)
 *         .placeholders(placeholders -> placeholders.add((Placeholders.Text) (recipient, text) ->
 *                 text.replace("{server}", serverName)))
 * }</pre>
 */
public sealed interface Placeholders {
	/**
	 * Placeholders written into the text, such as PlaceholderAPI's {@code %player_name%}: each literal part is
	 * handed over before it is read as MiniMessage.
	 */
	non-sealed interface Text extends Placeholders {
		/**
		 * Fills the placeholders of one literal part for a recipient.
		 *
		 * @param recipient the recipient the text is rendered for
		 * @param text      a literal part of the text, MiniMessage not yet read
		 * @return the part with the placeholders filled in
		 */
		@NotNull String fill(@NotNull Recipient recipient, @NotNull String text);
	}

	/**
	 * Placeholders written as MiniMessage tags, such as MiniPlaceholders' {@code <player_name>}.
	 */
	non-sealed interface Tags extends Placeholders {
		/**
		 * Returns the tags of a recipient.
		 *
		 * @param recipient the recipient the text is rendered for
		 * @return the resolver of the tags; it applies only to literal parts
		 */
		@NotNull TagResolver resolver(@NotNull Recipient recipient);
	}
}
