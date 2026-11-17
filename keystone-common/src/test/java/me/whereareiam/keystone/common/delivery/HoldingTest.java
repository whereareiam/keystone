package me.whereareiam.keystone.common.delivery;

import me.whereareiam.keystone.common.fixture.TestRecipient;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Content;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HoldingTest {
	private final TestRecipient alice = TestRecipient.player("Alice", Locale.ENGLISH);
	private final Holding holding = new Holding(recipient -> true);

	@Test
	void holdsNothingWhenTheRuleSaysNo() {
		assertFalse(new Holding(recipient -> false).hold(alice, display("one")));
	}

	@Test
	void holdsInOrderUntilReleased() {
		assertTrue(holding.hold(alice, display("one")));
		assertTrue(holding.hold(alice, display("two").with("n", 2)));

		assertEquals(List.of(display("one"), display("two").with("n", 2)), holding.release(alice.id()));
		assertTrue(holding.release(alice.id()).isEmpty());
	}

	@Test
	void neverHoldsRecipientsWithoutId() {
		assertFalse(holding.hold(TestRecipient.commandBlock(), display("one")));
	}

	@Test
	void dropsTheOldestPastTheLimit() {
		for (int index = 0; index <= Holding.LIMIT; index++) holding.hold(alice, display("text " + index));

		List<Display> held = holding.release(alice.id());
		assertEquals(Holding.LIMIT, held.size());
		assertEquals(display("text 1"), held.getFirst());
	}

	@Test
	void discardDropsWhatWasHeld() {
		holding.hold(alice, display("one"));
		holding.discard(alice.id());

		assertTrue(holding.release(alice.id()).isEmpty());
		assertTrue(holding.release(UUID.randomUUID()).isEmpty());
	}

	private static Display display(String text) {
		return Display.chat(Content.of(text));
	}
}
