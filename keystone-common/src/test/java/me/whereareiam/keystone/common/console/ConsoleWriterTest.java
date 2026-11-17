package me.whereareiam.keystone.common.console;

import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.TestMessenger;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsoleWriterTest {
	private static final String GOLD = "\u001B[38;2;255;170;0m";
	private static final String RESET = AnsiSerializer.RESET;

	private final TestPlatform platform = new TestPlatform();
	private final Recipient console = platform.console();
	private final ConsoleWriter writer = new ConsoleWriter(new TextRenderer(TestMessenger.options()), platform);

	@Test
	void writesInTheFallbackLanguageWithAnsi() {
		writer.write(console, Display.chat(Content.of(Message.of("greeting"))).with("player", "Alice"));

		assertEquals(List.of(RESET + GOLD + "Hello, Alice!" + RESET), platform.log());
	}

	@Test
	void writesEachLineOfATextApart() {
		writer.write(console, Display.chat(Content.of("one\ntwo")));

		assertEquals(List.of("one", "two"), platform.log());
	}

	@Test
	void writesTheTextsOfADisplayAndPlaysNoSound() {
		writer.write(console, Display.of(
				ActionBar.of(Content.of("bar")),
				Title.builder().title(Content.of("Big")).subtitle(Content.of("small")).build(),
				Title.of(Content.of("Only")),
				BossBar.of(Content.of("boss {n}")),
				Sound.of(Key.key("block.note_block.pling"))
		).with("n", 1));

		assertEquals(List.of("bar", "Big", "small", "Only", "boss 1"), platform.log());
	}

	@Test
	void printsABannerBetweenEmptyLines() {
		writer.print(Banner.of(Content.of("Socialismus v{version}"), Content.of("{@prefix}")).with("version", "2.0.0"));

		assertEquals(List.of("", "Socialismus v2.0.0", "[S]", ""), platform.log());
	}
}
