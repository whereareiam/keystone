package me.whereareiam.keystone.common.markup;

import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.common.fixture.TestFormat;
import me.whereareiam.keystone.type.LegacyCode;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class DefaultMarkupTest {
	private final Markup colours = new DefaultMarkup(new TestFormat(), TagPolicy.of(TagPolicy.Group.COLOR));

	@Test
	void appliesOnlyTheTagsThePolicyAllows() {
		Component read = colours.read("<red>Hi <bold>there");

		assertEquals("Hi <bold>there", plain(read));
		assertEquals(NamedTextColor.RED, styleOf(read, "Hi").color());
	}

	@Test
	void readsNoLegacyCodesUnlessAsked() {
		assertEquals("&cHi", plain(colours.read("&cHi")));
	}

	@Test
	void readsTheLegacyCodesTheFilterAccepts() {
		Markup markup = colours.legacyCodes(code -> code instanceof LegacyCode.NamedColor);

		Component read = markup.read("&c&lHi");

		assertNotSame(colours, markup);
		assertEquals("&lHi", plain(read));
		assertEquals(NamedTextColor.RED, styleOf(read, "Hi").color());
		assertFalse(styleOf(read, "Hi").hasDecoration(TextDecoration.BOLD));
	}

	private static Style styleOf(Component component, String text) {
		List<Style> found = new ArrayList<>();
		ComponentFlattener.basic().flatten(component, new FlattenerListener() {
			private final Deque<Style> styles = new ArrayDeque<>(List.of(Style.empty()));

			@Override
			public void pushStyle(@NotNull Style style) {
				styles.push(styles.peek().merge(style));
			}

			@Override
			public void component(@NotNull String piece) {
				if (piece.contains(text)) found.add(styles.peek());
			}

			@Override
			public void popStyle(@NotNull Style style) {
				styles.pop();
			}
		});

		return found.getFirst();
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
