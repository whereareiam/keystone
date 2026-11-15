package me.whereareiam.keystone.model;

import me.whereareiam.semantica.Content;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BannerTest {
	@Test
	void keepsItsLinesAndValues() {
		Banner banner = Banner.of(Content.of("one"), Content.of("two {n}"));
		Banner withValues = banner.values(Values.of("n", 2));

		assertEquals(List.of(Content.of("one"), Content.of("two {n}")), withValues.getLines());
		assertEquals(Values.none(), banner.getValues());
		assertEquals(Values.of("n", 2), withValues.getValues());
	}
}
