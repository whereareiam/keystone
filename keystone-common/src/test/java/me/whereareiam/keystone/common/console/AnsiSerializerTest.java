package me.whereareiam.keystone.common.console;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnsiSerializerTest {
	private static final String RED = "\u001B[38;2;255;85;85m";
	private static final String GOLD = "\u001B[38;2;255;170;0m";
	private static final String BOLD = "\u001B[1m";
	private static final String RESET = AnsiSerializer.RESET;

	private final AnsiSerializer serializer = new AnsiSerializer();

	@Test
	void plainTextStaysPlain() {
		assertEquals("Hello", serializer.serialize(Component.text("Hello")));
	}

	@Test
	void writesColoursAndDecorations() {
		Component text = MiniMessage.miniMessage().deserialize("<red>Hello <bold>world</bold></red>!");

		assertEquals(RESET + RED + "Hello " + RESET + RED + BOLD + "world" + RESET + "!" + RESET, serializer.serialize(text));
	}

	@Test
	void aChildKeepsItsParentsStyleUnlessItSetsItsOwn() {
		Component text = Component.text("a", NamedTextColor.GOLD, TextDecoration.BOLD)
				.append(Component.text("b").decoration(TextDecoration.BOLD, false))
				.append(Component.text("c", NamedTextColor.RED));

		assertEquals(RESET + GOLD + BOLD + "a" + RESET + GOLD + "b" + RESET + RED + BOLD + "c" + RESET, serializer.serialize(text));
	}

	@Test
	void eachLineStartsWithItsStyle() {
		Component text = Component.text("one\ntwo", NamedTextColor.RED);

		assertEquals(RESET + RED + "one" + RESET + "\n" + RED + "two" + RESET, serializer.serialize(text));
	}

	@Test
	void obfuscatedTextIsWrittenPlainly() {
		assertEquals("x", serializer.serialize(Component.text("x").decorate(TextDecoration.OBFUSCATED)));
	}
}
