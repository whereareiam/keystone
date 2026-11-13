package me.whereareiam.keystone.common.serializer.legacy;

import me.whereareiam.keystone.type.LegacyCode;
import me.whereareiam.keystone.type.LegacyInput;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultLegacyCodeConverterTest {
	private final DefaultLegacyCodeConverter converter = new DefaultLegacyCodeConverter();

	@Test
	void namedColours() {
		assertEquals("<black>0</black>", plain("&00"));
		assertEquals("<green>a</green>", plain("&aa"));
		assertEquals("<green>A</green>", plain("&AA"));
		assertEquals("<white>f</white>", plain("§ff"));
	}

	@Test
	void decorations() {
		assertEquals("<obfuscated>k</obfuscated>", plain("&kk"));
		assertEquals("<bold>l</bold>", plain("&ll"));
		assertEquals("<strikethrough>m</strikethrough>", plain("&mm"));
		assertEquals("<underlined>n</underlined>", plain("&nn"));
		assertEquals("<italic>O</italic>", plain("&OO"));
	}

	@Test
	void decorationsAddToTheColour() {
		assertEquals("<red>Hello <bold>world</bold></red>", plain("&cHello &lworld"));
		assertEquals("<red><bold><italic>Hi</italic></bold></red>", plain("&c&l&oHi"));
	}

	@Test
	void coloursEndTheColoursAndDecorationsBeforeThem() {
		assertEquals("<red>a </red><blue>b</blue>", plain("&ca &9b"));
		assertEquals("<bold><italic>a </italic></bold><red>b</red>", plain("&l&oa &cb"));
		assertEquals("<red><bold>a </bold></red><#00ff00>b</#00ff00>", plain("&c&la &#00ff00b"));
	}

	@Test
	void resetEndsEverythingBefore() {
		assertEquals("<red><bold>a </bold></red>b", plain("&c&la &rb"));
		assertEquals("<red>a </red>b <bold>c</bold>", plain("&ca &rb &lc"));
		assertEquals("plain", plain("&Rplain"));
	}

	@Test
	void hexColours() {
		assertEquals("<#ff0000>Red</#ff0000>", plain("&#ff0000Red"));
		assertEquals("<#ff0000>Red</#ff0000>", plain("&#FF0000Red"));
		assertEquals("<#ff0000>Red</#ff0000>", plain("§#ff0000Red"));
		assertEquals("<#ff0000>Red</#ff0000>", plain("&x&f&f&0&0&0&0Red"));
		assertEquals("<#ff0000>Red</#ff0000>", plain("§x§F§F§0§0§0§0Red"));
		assertEquals("<#ff0000>Red</#ff0000>", plain("&x§f&f§0&0§0&0Red"));
	}

	@Test
	void incompleteHexColoursStayAsTyped() {
		assertNull(converted("&#ff00"));
		assertNull(converted("&#ff00gg tail"));
		assertNull(converted("&x&f&f&0&0"));
		assertNull(converted("&x&f&f&0&0&0&zRed"));
		assertNull(converted("&xf&f&0&0&0&0Red"));
	}

	@Test
	void codesCountAtTheStartAfterWhitespaceAndAfterCodes() {
		assertEquals("a <red>b</red>", plain("a &cb"));
		assertEquals("a\t<red>b</red>", plain("a\t&cb"));
		assertEquals("<red><bold>b</bold></red>", plain("&c&lb"));
		assertEquals("<red>Hi&lthere</red>", plain("&cHi&lthere"));
	}

	@Test
	void textThatOnlyLooksLikeCodesStaysAsTyped() {
		assertNull(converted("R&D"));
		assertNull(converted("https://example.com/?a=1&b=2"));
		assertNull(converted("& alone"));
		assertNull(converted("ends with &"));
		assertNull(converted("&z &g &&"));
		assertEquals("<red>&z</red>", plain("&c&z"));
	}

	@Test
	void plainTextIsEscaped() {
		assertEquals("<red>Hello \\<player></red>", plain("&cHello <player>"));
		assertEquals("<red>\\<bold>a\\\\b</red>", plain("&c<bold>a\\b"));
		assertEquals("Hello \\<player>", converter.toMiniMessage("Hello <player>", LegacyInput.PLAIN_TEXT));
	}

	@Test
	void escapedPlainTextParsesBackToItself() {
		String text = "<bold>a\\b \\<c> <#ff0000>";
		String parsed = PlainTextComponentSerializer.plainText().serialize(MiniMessage.miniMessage().deserialize(plain("&c" + text)));

		assertEquals(text, parsed);
	}

	@Test
	void miniMessageTextIsKept() {
		assertEquals(
				"<red>Hello <bold>you</bold></red>",
				converter.toMiniMessage("&cHello <bold>you</bold>", LegacyInput.MINIMESSAGE)
		);
		assertEquals("Hello <player>", converter.toMiniMessage("Hello <player>", LegacyInput.MINIMESSAGE));
		assertEquals("<bold><red>Hi</bold></red>", converter.toMiniMessage("<bold>&cHi</bold>", LegacyInput.MINIMESSAGE));
		assertEquals("\\<bold>&cHi", plain("<bold>&cHi"));
	}

	@Test
	void filteredCodesStayAsTyped() {
		Predicate<LegacyCode> colours = code -> code instanceof LegacyCode.NamedColor
				|| code instanceof LegacyCode.HexColor;

		assertEquals("<red>&kHi</red>", miniMessage("&c&kHi", colours));
		assertEquals("&kHi &rthere", miniMessage("&kHi &rthere", colours));
		assertEquals("<#ff0000>&lHi</#ff0000>", miniMessage("&#ff0000&lHi", colours));
		assertNull(converter.convert("&kHi", LegacyInput.MINIMESSAGE, colours));
	}

	@Test
	void filteredColoursEndNothing() {
		Predicate<LegacyCode> bold = code -> code instanceof LegacyCode.Decoration decoration
				&& decoration.decoration() == TextDecoration.BOLD;

		assertEquals("<bold>a &cb</bold>", miniMessage("&la &cb", bold));
		assertEquals("<bold>a &rb</bold>", miniMessage("&la &rb", bold));
		assertEquals("&ka <bold>b</bold>", miniMessage("&ka &lb", bold));
	}

	@Test
	void filterReceivesTypedCodes() {
		List<LegacyCode> seen = new ArrayList<>();
		converter.toMiniMessage("&c&l&#00ff00&x&0&0&0&0&f&f&r &z", LegacyInput.PLAIN_TEXT, code -> seen.add(code) && false);

		assertEquals(List.of(
				new LegacyCode.NamedColor(NamedTextColor.RED),
				new LegacyCode.Decoration(TextDecoration.BOLD),
				new LegacyCode.HexColor(TextColor.color(0x00ff00)),
				new LegacyCode.HexColor(TextColor.color(0x0000ff)),
				LegacyCode.RESET
		), seen);
	}

	@Test
	void textWithoutCodes() {
		assertNull(converted("Hello"));
		assertNull(converted(""));
		assertEquals("", plain(""));
	}

	private String plain(String text) {
		return converter.toMiniMessage(text, LegacyInput.PLAIN_TEXT);
	}

	private String miniMessage(String text, Predicate<LegacyCode> allowed) {
		return converter.toMiniMessage(text, LegacyInput.MINIMESSAGE, allowed);
	}

	private String converted(String text) {
		return converter.convert(text, LegacyInput.PLAIN_TEXT, code -> true);
	}
}
