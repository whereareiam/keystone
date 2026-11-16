package me.whereareiam.keystone.common.render;

import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.MessengerOptions;
import me.whereareiam.keystone.common.fixture.TestFormat;
import me.whereareiam.keystone.common.fixture.TestRecipient;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import me.whereareiam.semantica.message.ResolvedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TextRendererTest {
	private final Recipient german = TestRecipient.player("Alice", Locale.GERMAN);
	private final Recipient english = TestRecipient.player("Bob", Locale.ENGLISH);
	private final CountingFormat format = new CountingFormat();

	@Test
	void rendersInTheRecipientsLanguage() {
		List<Component> rendered = renderer().render(Content.of("{@greeting}"), Map.of("player", "all"), List.of(german, english));

		assertEquals(List.of("Hallo, all!", "Hello, all!"), rendered.stream().map(TextRendererTest::plain).toList());
	}

	@Test
	void fallsBackAlongTheChain() {
		assertEquals("Wait 3 seconds.", plain(renderer().render(Content.of("{@cooldown}"), Map.of("time", 3), german)));
	}

	@Test
	void readsTheMarkupOfLiteralPartsOnly() {
		Component rendered = renderer().render(Content.of("{@greeting}"), Map.of("player", "<red>Eve"), english);

		assertEquals("Hello, <red>Eve!", plain(rendered));
		assertEquals(NamedTextColor.GOLD, rendered.color());
	}

	@Test
	void textPlaceholdersFillLiteralPartsInOrder() {
		Placeholders.Text server = (recipient, text) -> text.replace("%server%", "Lobby of " + recipient.name());
		Placeholders.Text upper = (recipient, text) -> text.replace("Lobby", "LOBBY");

		Component rendered = renderer(server, upper).render(Content.of("%server% {player}"), Map.of("player", "%server%"), english);

		assertEquals("LOBBY of Bob %server%", plain(rendered));
	}

	@Test
	void tagPlaceholdersAreTagsOfLiteralParts() {
		Placeholders.Tags online = () -> Placeholder.unparsed("online", "12");

		Component rendered = renderer(online).render(Content.of("<online> online, {player}"), Map.of("player", "<online>"), english);

		assertEquals("12 online, <online>", plain(rendered));
	}

	@Test
	void recipientsWithTheSameResultShareOneRendering() {
		Recipient carol = TestRecipient.player("Carol", Locale.ENGLISH);

		List<Component> rendered = renderer().render(Content.of("{@greeting}"), Map.of("player", "all"), List.of(english, german, carol));

		assertEquals(2, format.renders);
		assertSame(rendered.get(0), rendered.get(2));
	}

	@Test
	void placeholdersThatFillInDifferentlyRenderApart() {
		Placeholders.Text name = (recipient, text) -> text.replace("%name%", recipient.name());
		Placeholders.Text online = (recipient, text) -> text.replace("%online%", "12");
		Recipient carol = TestRecipient.player("Carol", Locale.ENGLISH);

		List<Component> named = renderer(name, online).render(Content.of("Hi %name%"), Map.of(), List.of(english, carol));
		List<Component> shared = renderer(name, online).render(Content.of("%online% online"), Map.of(), List.of(english, carol));

		assertEquals(List.of("Hi Bob", "Hi Carol"), named.stream().map(TextRendererTest::plain).toList());
		assertSame(shared.get(0), shared.get(1));
		assertEquals(3, format.renders);
	}

	private TextRenderer renderer(Placeholders... placeholders) {
		MessengerOptions.MessengerOptionsBuilder options = MessengerOptions.builder()
				.semantica(TestSemantica.create())
				.format(format)
				.policy(TagPolicy.defaults())
				.fallback(Locale.ENGLISH);
		for (Placeholders source : placeholders) options.placeholder(source);

		return new TextRenderer(options.build());
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	private static final class CountingFormat extends TestFormat {
		private int renders;

		@Override
		public @NotNull Component render(
				@NotNull TagPolicy policy,
				@NotNull ResolvedMessage message,
				@NotNull UnaryOperator<String> text,
				@NotNull List<TagResolver> tags
		) {
			renders++;
			return super.render(policy, message, text, new ArrayList<>(tags));
		}
	}
}
