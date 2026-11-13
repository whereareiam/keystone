package me.whereareiam.keystone.common.serializer.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.ParsingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pins what players see of legacy codes the MiniMessage adapter reads: each run of text with the colour and
 * decorations it ends up with.
 */
class MiniMessageAdapterLegacyTest {
	private final MiniMessageAdapter adapter = new MiniMessageAdapter();

	@Test
	void namedColours() {
		assertEquals("[green]Enabled", runs("&aEnabled"));
		assertEquals("[green]Enabled", runs("&AEnabled"));
		assertEquals("[black]0 |[white]f", runs("&00 &ff"));
	}

	@Test
	void coloursAfterWhitespace() {
		assertEquals("[green]Hello |[red]World", runs("&aHello &cWorld"));
	}

	@Test
	void decorations() {
		assertEquals("[obfuscated]k |[obfuscated,bold]l", runs("&kk &ll"));
		assertEquals("[strikethrough]m |[strikethrough,underlined]n |[strikethrough,underlined,italic]o", runs("&mm &nn &oo"));
	}

	@Test
	void coloursEndTheDecorationsBeforeThem() {
		assertEquals("[bold]Bold |[red]Red", runs("&lBold &cRed"));
	}

	@Test
	void codesRightAfterCodes() {
		assertEquals("[green,bold]Hi", runs("&a&lHi"));
		assertEquals("[#ff0000,bold]Hi", runs("&#ff0000&lHi"));
	}

	@Test
	void hexColours() {
		assertEquals("[#ff0000]Red", runs("&#ff0000Red"));
		assertEquals("[#ff0000]Red", runs("&#FF0000Red"));
		assertEquals("[#ff0000]Red", runs("&x&f&f&0&0&0&0Red"));
	}

	@Test
	void incompleteHexColoursStayAsTyped() {
		assertEquals("&#ff00Red", runs("&#ff00Red"));
		assertEquals("&#gg0000Red", runs("&#gg0000Red"));
		assertEquals("&x&f&f&0&0Red", runs("&x&f&f&0&0Red"));
	}

	@Test
	void resetReturnsToTheSurroundingStyle() {
		assertEquals("[green]Hello |Plain", runs("&aHello &rPlain"));
		assertEquals("[blue]x |[green]y |[blue]z", runs("<blue>x &ay &rz"));
	}

	@Test
	void codesInsideWordsStayAsTyped() {
		assertEquals("R&D", runs("R&D"));
		assertEquals("a&b", runs("a&b"));
		assertEquals("[green]Hi&cthere", runs("&aHi&cthere"));
		assertEquals("& alone", runs("& alone"));
		assertEquals("ends with &", runs("ends with &"));
	}

	@Test
	void unknownCodesStayAsTyped() {
		assertEquals("&zText &g", runs("&zText &g"));
	}

	@Test
	void textThatLooksLikeTagsStaysText() {
		assertEquals("[green]Hello <player>", runs("&aHello <player>"));
		assertEquals("[green]a \\ b", runs("&aa \\ b"));
	}

	@Test
	void miniMessageTagsAroundCodes() {
		assertEquals("[green]Hi |[red]there", runs("<green>Hi &cthere"));
		assertEquals("[bold]x |[red,bold]y", runs("<bold>x &cy"));
	}

	@Test
	void sectionCodesAreRejectedByMiniMessage() {
		assertThrows(ParsingException.class, () -> adapter.deserialize("§aEnabled", true));
	}

	@Test
	void withoutLegacyInputCodesStayText() {
		assertEquals("&aHello", runs(adapter.deserialize("&aHello", false)));
	}

	private String runs(String input) {
		return runs(adapter.deserialize(input, true));
	}

	private static String runs(Component component) {
		List<String> runs = new ArrayList<>();
		collect(component, Style.empty(), runs);

		return String.join("|", runs);
	}

	private static void collect(Component component, Style parent, List<String> runs) {
		Style style = component.style().merge(parent, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
		if (component instanceof TextComponent text && !text.content().isEmpty())
			runs.add(describe(style) + text.content());

		for (Component child : component.children())
			collect(child, style, runs);
	}

	private static String describe(Style style) {
		List<String> parts = new ArrayList<>();
		TextColor color = style.color();
		if (color != null) parts.add(colorName(color));

		for (TextDecoration decoration : TextDecoration.values()) {
			if (style.decoration(decoration) == TextDecoration.State.TRUE)
				parts.add(TextDecoration.NAMES.key(decoration));
		}

		return parts.isEmpty() ? "" : "[" + String.join(",", parts) + "]";
	}

	private static String colorName(TextColor color) {
		if (color instanceof NamedTextColor named) return NamedTextColor.NAMES.key(named);

		return color.asHexString();
	}
}
