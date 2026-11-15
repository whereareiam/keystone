package me.whereareiam.keystone.common.delivery;

import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.type.Gate;
import me.whereareiam.semantica.Content;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatekeeperTest {
	private final TestPlatform platform = new TestPlatform();
	private final TestSender alice = TestSender.player("Alice", Locale.ENGLISH);
	private final Recipient player = TestKeystone.create(platform, null).recipients().of(alice);
	private final Recipient console = TestKeystone.create(platform, null).console();
	private final Gatekeeper gatekeeper = new Gatekeeper();

	@Test
	void sendsByDefault() {
		assertFalse(gatekeeper.hold(player, display("one"), Map.of()));
	}

	@Test
	void holdsInOrderUntilReleased() {
		gatekeeper.gate(recipient -> Gate.HOLD);

		assertTrue(gatekeeper.hold(player, display("one"), Map.of()));
		assertTrue(gatekeeper.hold(player, display("two"), Map.of("n", 2)));

		assertEquals(
				java.util.List.of(new Gatekeeper.Held(display("one"), Map.of()), new Gatekeeper.Held(display("two"), Map.of("n", 2))),
				gatekeeper.release(alice.id())
		);
		assertTrue(gatekeeper.release(alice.id()).isEmpty());
	}

	@Test
	void neverHoldsSendersWithoutId() {
		gatekeeper.gate(recipient -> Gate.HOLD);

		assertFalse(gatekeeper.hold(console, display("one"), Map.of()));
	}

	@Test
	void dropsTheOldestPastTheLimit() {
		gatekeeper.gate(recipient -> Gate.HOLD);
		for (int index = 0; index <= Gatekeeper.LIMIT; index++) gatekeeper.hold(player, display("text " + index), Map.of());

		var held = gatekeeper.release(alice.id());
		assertEquals(Gatekeeper.LIMIT, held.size());
		assertEquals(display("text 1"), held.getFirst().display());
	}

	@Test
	void discardDropsWhatWasHeld() {
		gatekeeper.gate(recipient -> Gate.HOLD);
		gatekeeper.hold(player, display("one"), Map.of());

		gatekeeper.discard(alice.id());

		assertTrue(gatekeeper.release(alice.id()).isEmpty());
		assertTrue(gatekeeper.release(UUID.randomUUID()).isEmpty());
	}

	private static Display display(String text) {
		return Display.chat(Content.of(text));
	}
}
