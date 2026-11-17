package me.whereareiam.keystone.common;

import me.whereareiam.keystone.Messenger;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.RecordingAudience;
import me.whereareiam.keystone.common.fixture.TestMessenger;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestRecipient;
import me.whereareiam.keystone.model.Banner;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultMessengerTest {
	@TempDir
	Path folder;

	private final TestPlatform platform = new TestPlatform();
	private final TestRecipient alice = TestRecipient.player("Alice", Locale.GERMAN);
	private final TestRecipient bob = TestRecipient.player("Bob", Locale.ENGLISH);

	@Test
	void aMessageFollowsItsDisplayFile() throws IOException {
		Path file = folder.resolve("display.yml");
		Files.writeString(file, "cooldown:\n  chat: false\n  actionbar: true\n");
		Messenger<TestRecipient> messenger = TestMessenger.create(platform);
		messenger.displays().add("plugin", file);

		messenger.send(alice, Message.of("cooldown").with("time", 3));
		messenger.send(alice, Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of("actionbar: Wait 3 seconds.", "chat: Hallo, Alice!"), alice.audience().shown());
	}

	@Test
	void sendsToSeveralSendersEachInTheirLanguage() {
		Messenger<TestRecipient> messenger = TestMessenger.create(platform);

		messenger.send(List.of(alice, bob, platform.console()), Message.of("greeting").with("player", "all"));
		messenger.send(List.of(alice, bob), Display.chat(Content.of("{@prefix} {n}")).with("n", 1));

		assertEquals(List.of("chat: Hallo, all!", "chat: [S] 1"), alice.audience().shown());
		assertEquals(List.of("chat: Hello, all!", "chat: [S] 1"), bob.audience().shown());
		assertTrue(platform.log().getFirst().contains("Hello, all!"));
	}

	@Test
	void takesThePluginsOwnSenderType() {
		Messenger<Player> messenger = new DefaultMessenger<>(platform, Player::recipient, TestMessenger.options());
		Player player = new Player("Carol", new RecordingAudience());

		messenger.send(player, Message.of("greeting").with("player", "Carol"));

		assertEquals(List.of("chat: Hello, Carol!"), player.audience().shown());
	}

	@Test
	void holdsUntilReleaseInTheLanguageOfThen() {
		AtomicBoolean loggedIn = new AtomicBoolean();
		Messenger<TestRecipient> messenger = TestMessenger.create(platform, recipient -> !loggedIn.get());

		messenger.send(alice, Message.of("greeting").with("player", "one"));
		messenger.send(alice, Message.of("greeting").with("player", "two"));
		messenger.send(platform.console(), Message.of("prefix"));

		assertTrue(alice.audience().shown().isEmpty());
		assertEquals(List.of("[S]"), platform.log());

		loggedIn.set(true);
		messenger.release(alice);
		assertEquals(List.of("chat: Hallo, one!", "chat: Hallo, two!"), alice.audience().shown());
	}

	@Test
	void discardDropsWhatWasHeld() {
		Messenger<TestRecipient> messenger = TestMessenger.create(platform, recipient -> true);

		messenger.send(alice, Message.of("prefix"));
		messenger.discard(alice);
		messenger.release(alice);

		assertTrue(alice.audience().shown().isEmpty());
	}

	@Test
	void printsABanner() {
		TestMessenger.create(platform).print(Banner.of(Content.of("v{version}")).with("version", "2.0.0"));

		assertEquals(List.of("", "v2.0.0", ""), platform.log());
	}

	private record Player(String name, RecordingAudience audience) {
		private Recipient recipient() {
			Player player = this;
			return new Recipient() {
				@Override
				public @NotNull String name() {
					return player.name;
				}

				@Override
				public @NotNull Optional<UUID> uniqueId() {
					return Optional.empty();
				}

				@Override
				public @NotNull Optional<Locale> clientLanguage() {
					return Optional.empty();
				}

				@Override
				public @NotNull Audience audience() {
					return player.audience;
				}
			};
		}
	}
}
