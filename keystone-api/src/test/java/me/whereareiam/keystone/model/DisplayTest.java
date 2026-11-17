package me.whereareiam.keystone.model;

import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisplayTest {
	@Test
	void keepsItsPartsInOrder() {
		Display display = Display.of(ActionBar.of(Content.of("a")), Chat.of(Content.of("b")));

		assertEquals(List.of(ActionBar.of(Content.of("a")), Chat.of(Content.of("b"))), display.getParts());
		assertFalse(display.isEmpty());
	}

	@Test
	void chatIsADisplayOfTheChatAlone() {
		assertEquals(Display.of(Chat.of(Content.of("x"))), Display.chat(Content.of("x")));
		assertTrue(Display.none().isEmpty());
	}

	@Test
	void withAddsValuesWithoutChangingTheDisplay() {
		Display display = Display.chat(Content.of("{player}"));
		Display shown = display.with("player", "Alice").with(Map.of("count", Value.number(3)));

		assertEquals(Map.of(), display.getValues());
		assertEquals(Map.of("player", Value.text("Alice"), "count", Value.number(3)), shown.getValues());
		assertEquals(display.getParts(), shown.getParts());
		assertThrows(NullPointerException.class, () -> display.with("player", (String) null));
	}
}
