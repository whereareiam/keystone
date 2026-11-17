package me.whereareiam.keystone.common.render;

import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.MessengerOptions;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Value;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import me.whereareiam.semantica.message.ResolvedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Renders a text for recipients: Semantica resolves it along each recipient's languages, the text placeholders of
 * that recipient fill its literal parts, and the platform's Adventure format reads it with the plugin's tag policy.
 * <p>
 * A text is resolved once per language chain and rendered once per distinct filled result, so recipients who share
 * their languages and whose placeholders fill in alike share one component. The result is the same as rendering
 * for each recipient alone: recipients share a component only when everything the format reads is equal.
 */
public final class TextRenderer {
	private final Semantica semantica;
	private final AdventureFormat format;
	private final TagPolicy policy;
	private final LanguageChain languages;
	private final List<Placeholders.Text> texts = new ArrayList<>();
	private final List<Placeholders.Tags> tags = new ArrayList<>();

	/**
	 * Creates the renderer.
	 *
	 * @param options the messenger's options
	 */
	public TextRenderer(@NotNull MessengerOptions options) {
		this.semantica = options.getSemantica();
		this.format = options.getFormat();
		this.policy = options.getPolicy();
		this.languages = new LanguageChain(options.getResolvers(), options.getFallback());

		for (Placeholders placeholders : options.getPlaceholders()) {
			switch (placeholders) {
				case Placeholders.Text text -> texts.add(text);
				case Placeholders.Tags tag -> tags.add(tag);
			}
		}
	}

	/**
	 * Renders a text for each recipient.
	 *
	 * @param content    the text, which may reference translated messages
	 * @param values     the values its arguments and references see
	 * @param recipients the recipients
	 * @return the component of each recipient, in the recipients' order
	 */
	public @NotNull List<Component> render(@NotNull Content content, @NotNull Map<String, ? extends Value> values, @NotNull List<Recipient> recipients) {
		List<TagResolver> resolvers = tags.stream().map(Placeholders.Tags::resolver).toList();
		Map<List<Locale>, ResolvedMessage> resolved = new HashMap<>();
		Map<ResolvedMessage, Component> rendered = new HashMap<>();

		List<Component> components = new ArrayList<>(recipients.size());
		for (Recipient recipient : recipients) {
			ResolvedMessage message = resolved.computeIfAbsent(languages.resolve(recipient), chain -> semantica.resolve(content, values, chain));
			ResolvedMessage filled = fill(message, recipient);
			components.add(rendered.computeIfAbsent(filled, shown -> format.render(policy, shown, UnaryOperator.identity(), resolvers)));
		}

		return components;
	}

	/**
	 * Renders a text for one recipient.
	 *
	 * @param content   the text, which may reference translated messages
	 * @param values    the values its arguments and references see
	 * @param recipient the recipient
	 * @return the component
	 */
	public @NotNull Component render(@NotNull Content content, @NotNull Map<String, ? extends Value> values, @NotNull Recipient recipient) {
		return render(content, values, List.of(recipient)).getFirst();
	}

	private ResolvedMessage fill(ResolvedMessage message, Recipient recipient) {
		if (texts.isEmpty()) return message;

		List<ResolvedMessage.Part> parts = new ArrayList<>(message.getParts().size());
		for (ResolvedMessage.Part part : message.getParts()) {
			if (part instanceof ResolvedMessage.Literal literal) parts.add(new ResolvedMessage.Literal(fill(literal.text(), recipient)));
			else parts.add(part);
		}

		return new ResolvedMessage(message.getKey(), message.getLocale(), parts, message.isFound());
	}

	private String fill(String literal, Recipient recipient) {
		String text = literal;
		for (Placeholders.Text placeholders : texts) text = placeholders.fill(recipient, text);

		return text;
	}
}
