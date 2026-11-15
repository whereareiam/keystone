package me.whereareiam.keystone.common.render;

import me.whereareiam.semantica.format.adventure.TagPolicy;
import me.whereareiam.semantica.message.ResolvedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Semantica's Adventure format of the Adventure a platform has: {@code format-adventure-v5} on Paper and Velocity,
 * {@code format-adventure-v4} on Bukkit and BungeeCord. Keystone's own classes use only what both have.
 */
public interface AdventureFormat {
	/**
	 * Renders a resolved message with the tags a policy allows.
	 *
	 * @param policy  the tags its literal parts may use
	 * @param message the message
	 * @param text    applied to each literal part before it is read, such as text placeholders
	 * @param tags    further tags for literal parts, such as tag placeholders
	 * @return the component
	 */
	@NotNull Component render(
			@NotNull TagPolicy policy,
			@NotNull ResolvedMessage message,
			@NotNull UnaryOperator<String> text,
			@NotNull List<TagResolver> tags
	);

	/**
	 * Reads MiniMessage with only the tags a policy allows; any other tag stays as written.
	 *
	 * @param policy the tags the text may use
	 * @param text   the text
	 * @return the component
	 */
	@NotNull Component read(@NotNull TagPolicy policy, @NotNull String text);
}
