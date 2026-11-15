package me.whereareiam.keystone.common.render;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import me.whereareiam.semantica.message.ResolvedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Renders a text for one recipient: Semantica resolves it along the recipient's languages, the placeholders of that
 * recipient fill its literal parts, and the platform's Adventure format reads it with the plugin's tag policy.
 */
@RequiredArgsConstructor
public final class TextRenderer {
	private final Semantica semantica;
	private final AdventureFormat format;
	private final TagPolicy policy;
	private final List<Placeholders> placeholders;

	/**
	 * Renders a text for a recipient.
	 *
	 * @param recipient the recipient
	 * @param content   the text, which may reference translated messages
	 * @param values    the values its arguments and references see
	 * @return the component
	 */
	public @NotNull Component render(@NotNull Recipient recipient, @NotNull Content content, @NotNull Map<String, ?> values) {
		ResolvedMessage resolved = semantica.resolve(content, values, recipient.languages());
		List<TagResolver> tags = new ArrayList<>();
		UnaryOperator<String> text = UnaryOperator.identity();

		for (Placeholders source : placeholders) {
			switch (source) {
				case Placeholders.Text fill -> text = then(text, fill, recipient);
				case Placeholders.Tags resolver -> tags.add(resolver.resolver(recipient));
			}
		}

		return format.render(policy, resolved, text, tags);
	}

	private static UnaryOperator<String> then(UnaryOperator<String> previous, Placeholders.Text fill, Recipient recipient) {
		return literal -> fill.fill(recipient, previous.apply(literal));
	}
}
