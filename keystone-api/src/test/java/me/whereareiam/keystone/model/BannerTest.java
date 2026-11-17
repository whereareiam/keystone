package me.whereareiam.keystone.model;

import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BannerTest {
	@Test
	void keepsItsLinesAndValues() {
		Banner banner = Banner.of(Content.of("one"), Content.of("two {n}"));
		Banner withValues = banner.with("n", 2);

		assertEquals(List.of(Content.of("one"), Content.of("two {n}")), withValues.getLines());
		assertEquals(Map.of(), banner.getValues());
		assertEquals(Map.of("n", Value.number(2)), withValues.getValues());
	}
}
