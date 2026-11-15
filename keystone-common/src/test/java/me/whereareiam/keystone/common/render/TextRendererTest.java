package me.whereareiam.keystone.common.render;

import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.fixture.TestFormat;
import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextRendererTest {
	private final TestPlatform platform = new TestPlatform();
	private final Recipient german = TestKeystone.create(platform, null).recipients().of(TestSender.player("Alice", Locale.GERMAN));
	private final Recipient english = TestKeystone.create(platform, null).recipients().of(TestSender.player("Bob", Locale.ENGLISH));

	@Test
	void rendersInTheRecipientsLanguage() {
		TextRenderer renderer = renderer(List.of());

		assertEquals("Hallo, Alice!", plain(renderer.render(german, Content.of("{@greeting}"), Map.of("player", "Alice"))));
		assertEquals("Hello, Bob!", plain(renderer.render(english, Content.of("{@greeting}"), Map.of("player", "Bob"))));
	}

	@Test
	void fallsBackAlongTheChain() {
		Component rendered = renderer(List.of()).render(german, Content.of("{@cooldown}"), Map.of("time", 3));

		assertEquals("Wait 3 seconds.", plain(rendered));
	}

	@Test
	void readsTheMarkupOfLiteralPartsOnly() {
		Component rendered = renderer(List.of()).render(english, Content.of("{@greeting}"), Map.of("player", "<red>Eve"));

		assertEquals("Hello, <red>Eve!", plain(rendered));
		assertEquals(NamedTextColor.GOLD, rendered.color());
	}

	@Test
	void textPlaceholdersFillLiteralPartsInOrder() {
		Placeholders.Text server = (recipient, text) -> text.replace("%server%", "Lobby of " + recipient.name());
		Placeholders.Text upper = (recipient, text) -> text.replace("Lobby", "LOBBY");
		TextRenderer renderer = renderer(List.of(server, upper));

		Component rendered = renderer.render(english, Content.of("%server% {player}"), Map.of("player", "%server%"));

		assertEquals("LOBBY of Bob %server%", plain(rendered));
	}

	@Test
	void tagPlaceholdersAreTagsOfLiteralParts() {
		Placeholders.Tags online = recipient -> Placeholder.unparsed("online", "12");
		Component rendered = renderer(List.of(online)).render(english, Content.of("<online> online, {player}"), Map.of("player", "<online>"));

		assertEquals("12 online, <online>", plain(rendered));
	}

	private static TextRenderer renderer(List<Placeholders> placeholders) {
		return new TextRenderer(TestSemantica.create(), new TestFormat(), TagPolicy.defaults(), placeholders);
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
