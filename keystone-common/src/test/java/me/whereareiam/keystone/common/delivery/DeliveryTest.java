package me.whereareiam.keystone.common.delivery;

import me.whereareiam.keystone.common.fixture.TestFormat;
import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.type.Gate;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryTest {
	private final TestPlatform platform = new TestPlatform();
	private final TestSender alice = TestSender.player("Alice", Locale.GERMAN);
	private final Recipient recipient = TestKeystone.create(platform, null).recipients().of(alice);
	private final Gatekeeper gatekeeper = new Gatekeeper();
	private final Delivery delivery = new Delivery(
			new TextRenderer(TestSemantica.create(), new TestFormat(), TagPolicy.defaults(), List.of()),
			gatekeeper,
			platform
	);

	@Test
	void showsEachPartInOrder() {
		Display display = Display.of(
				Chat.of(Content.of("{@greeting}")),
				ActionBar.of(Content.of("<green>bar")),
				Title.builder().title(Content.of("Big")).subtitle(Content.of("{player}")).build(),
				Sound.builder().name(Key.key("block.note_block.pling")).pitch(1.5F).build()
		);

		delivery.send(recipient, display, Map.of("player", "Alice"));

		assertEquals(List.of(
				"chat: Hallo, Alice!",
				"actionbar: bar",
				"title: Big | Alice " + net.kyori.adventure.title.Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofSeconds(1)),
				"sound: minecraft:block.note_block.pling MASTER 1.0 1.5"
		), alice.audience().shown());
	}

	@Test
	void hidesABossBarAfterItsDuration() {
		BossBar bar = BossBar.builder()
				.text(Content.of("Restart in {seconds}"))
				.color(net.kyori.adventure.bossbar.BossBar.Color.RED)
				.progress(0.5F)
				.duration(Duration.ofSeconds(10))
				.build();

		delivery.send(recipient, Display.of(bar), Map.of("seconds", 30));

		assertEquals(List.of("bossbar: Restart in 30 RED 0.5"), alice.audience().shown());
		assertEquals(Duration.ofSeconds(10), platform.later().getFirst().getKey());

		platform.runLater();

		assertEquals("hide bossbar: Restart in 30", alice.audience().shown().getLast());
		assertTrue(alice.audience().bossBars().isEmpty());
	}

	@Test
	void holdsWhatTheGateHoldsAndShowsItPastTheGate() {
		gatekeeper.gate(ignored -> Gate.HOLD);
		Display display = Display.chat(Content.of("{@greeting}"));

		delivery.send(recipient, display, Map.of("player", "Alice"));
		assertTrue(alice.audience().shown().isEmpty());

		gatekeeper.release(alice.id()).forEach(held -> delivery.show(recipient, held.display(), held.values()));
		assertEquals(List.of("chat: Hallo, Alice!"), alice.audience().shown());
	}

	@Test
	void anEmptyDisplayShowsNothing() {
		delivery.send(recipient, Display.none(), Map.of());

		assertTrue(alice.audience().shown().isEmpty());
	}
}
