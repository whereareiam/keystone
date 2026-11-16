package me.whereareiam.keystone.common.delivery;

import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.console.ConsoleWriter;
import me.whereareiam.keystone.common.fixture.TestMessenger;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestRecipient;
import me.whereareiam.keystone.common.render.TextRenderer;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.type.part.ActionBar;
import me.whereareiam.keystone.type.part.BossBar;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Sound;
import me.whereareiam.keystone.type.part.Title;
import me.whereareiam.semantica.Content;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryTest {
	private final TestPlatform platform = new TestPlatform();
	private final TestRecipient alice = TestRecipient.player("Alice", Locale.GERMAN);
	private final TestRecipient bob = TestRecipient.player("Bob", Locale.ENGLISH);
	private final Holding holding = new Holding(recipient -> recipient.name().equals("Held"));
	private final TextRenderer renderer = new TextRenderer(TestMessenger.options());
	private final Delivery delivery = new Delivery(renderer, holding, new ConsoleWriter(renderer, platform), platform);

	@Test
	void showsEachPartInOrder() {
		Display display = Display.of(
				Chat.of(Content.of("{@greeting}")),
				ActionBar.of(Content.of("<green>bar")),
				Title.builder().title(Content.of("Big")).subtitle(Content.of("{player}")).build(),
				Sound.builder().name(Key.key("block.note_block.pling")).pitch(1.5F).build()
		).with("player", "Alice");

		delivery.send(List.of(alice), display);

		assertEquals(List.of(
				"chat: Hallo, Alice!",
				"actionbar: bar",
				"title: Big | Alice " + net.kyori.adventure.title.Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3500), Duration.ofSeconds(1)),
				"sound: minecraft:block.note_block.pling MASTER 1.0 1.5"
		), alice.audience().shown());
	}

	@Test
	void showsEachRecipientTheirOwnLanguage() {
		delivery.send(List.of(alice, bob), Display.chat(Content.of("{@greeting}")).with("player", "all"));

		assertEquals(List.of("chat: Hallo, all!"), alice.audience().shown());
		assertEquals(List.of("chat: Hello, all!"), bob.audience().shown());
	}

	@Test
	void hidesBossBarsAfterTheirDuration() {
		BossBar bar = BossBar.builder()
				.text(Content.of("Restart in {seconds}"))
				.color(net.kyori.adventure.bossbar.BossBar.Color.RED)
				.progress(0.5F)
				.duration(Duration.ofSeconds(10))
				.build();
		TestRecipient carol = TestRecipient.player("Carol", Locale.ENGLISH);

		delivery.send(List.of(bob, carol), Display.of(bar).with("seconds", 30));

		assertEquals(List.of("bossbar: Restart in 30 RED 0.5"), bob.audience().shown());
		assertSame(bob.audience().bossBars().getFirst(), carol.audience().bossBars().getFirst());
		assertEquals(Duration.ofSeconds(10), platform.later().getFirst().getKey());

		platform.runLater();
		assertTrue(bob.audience().bossBars().isEmpty());
		assertTrue(carol.audience().bossBars().isEmpty());
	}

	@Test
	void holdsForThoseTheRuleHoldsAndWritesTheConsoleToTheLog() {
		TestRecipient held = TestRecipient.player("Held", Locale.ENGLISH);
		Recipient console = platform.console();

		delivery.send(List.of(held, console, bob), Display.chat(Content.of("{@prefix}")));

		assertTrue(held.audience().shown().isEmpty());
		assertEquals(List.of("[S]"), platform.log());
		assertEquals(List.of("chat: [S]"), bob.audience().shown());
		assertEquals(List.of(Display.chat(Content.of("{@prefix}"))), holding.release(held.id()));
	}

	@Test
	void anEmptyDisplayShowsNothing() {
		delivery.send(List.of(alice), Display.none());

		assertTrue(alice.audience().shown().isEmpty());
	}
}
