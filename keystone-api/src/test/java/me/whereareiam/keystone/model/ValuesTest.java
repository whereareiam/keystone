package me.whereareiam.keystone.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValuesTest {
	@Test
	void keepsTheValuesInOrder() {
		Values values = Values.of("player", "Alice").and("count", 3);

		assertEquals(Map.of("player", "Alice", "count", 3), values.asMap());
		assertEquals("{player=Alice, count=3}", values.toString());
	}

	@Test
	void isImmutable() {
		Values first = Values.of("a", 1);
		Values second = first.and("a", 2);

		assertEquals(Map.of("a", 1), first.asMap());
		assertEquals(Map.of("a", 2), second.asMap());
		assertThrows(UnsupportedOperationException.class, () -> second.asMap().put("b", 3));
	}

	@Test
	void noneIsEmptyAndRefusesNulls() {
		assertTrue(Values.none().asMap().isEmpty());
		assertThrows(NullPointerException.class, () -> Values.of("a", null));
		assertThrows(NullPointerException.class, () -> Values.none().and(null, 1));
	}
}
