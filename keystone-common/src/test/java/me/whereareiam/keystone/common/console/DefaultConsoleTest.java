package me.whereareiam.keystone.common.console;

import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.model.Values;
import me.whereareiam.keystone.recipient.Console;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultConsoleTest {
	private static final String GOLD = "\u001B[38;2;255;170;0m";
	private static final String RESET = AnsiSerializer.RESET;

	private final TestPlatform platform = new TestPlatform();
	private final Console console = TestKeystone.create(platform, null).console();

	@Test
	void writesAMessageInTheFallbackLanguageWithAnsi() {
		console.send(Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of(RESET + GOLD + "Hello, Alice!" + RESET), platform.log());
		assertTrue(platform.consoleSender().audience().shown().isEmpty());
	}

	@Test
	void writesEachLineOfATextApart() {
		console.send(Content.of("one\ntwo"), Values.none());

		assertEquals(List.of("one", "two"), platform.log());
	}

	@Test
	void writesTheTextsOfADisplayAndPlaysNoSound() {
		console.send(Display.of(
				ActionBar.of(Content.of("bar")),
				Title.builder().title(Content.of("Big")).subtitle(Content.of("small")).build(),
				Title.of(Content.of("Only")),
				BossBar.of(Content.of("boss {n}")),
				Sound.of(Key.key("block.note_block.pling"))
		), Values.of("n", 1));

		assertEquals(List.of("bar", "Big", "small", "Only", "boss 1"), platform.log());
	}

	@Test
	void printsABannerBetweenEmptyLines() {
		console.print(Banner.of(Content.of("Socialismus v{version}"), Content.of("{@prefix}")).values(Values.of("version", "2.0.0")));

		assertEquals(List.of("", "Socialismus v2.0.0", "[S]", ""), platform.log());
	}

	@Test
	void isTheFallbackLanguageAndHasEveryPermission() {
		assertEquals(List.of(Locale.ENGLISH), console.languages());
		assertTrue(console.hasPermission("anything"));
		assertTrue(console.uniqueId().isEmpty() && console.clientLanguage().isEmpty());
		assertEquals("CONSOLE", console.name());
	}
}
