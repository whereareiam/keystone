package me.whereareiam.keystone.common;

import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.exception.DisplayFileException;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.type.Gate;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultKeystoneTest {
	@TempDir
	Path folder;

	private final TestSender alice = TestSender.player("Alice", Locale.GERMAN);
	private final TestPlatform platform = new TestPlatform().join(alice);

	@Test
	void aMessageFollowsTheDisplayFile() throws IOException {
		Files.writeString(folder.resolve("display.yml"), "cooldown:\n  chat: false\n  actionbar: true\n");
		Keystone<TestSender> keystone = TestKeystone.create(platform, folder.resolve("display.yml"));

		keystone.recipients().of(alice).send(Message.of("cooldown").with("time", 3));
		keystone.recipients().of(alice).send(Message.of("greeting").with("player", "Alice"));

		assertEquals(List.of("actionbar: Wait 3 seconds.", "chat: Hallo, Alice!"), alice.audience().shown());
	}

	@Test
	void reloadReadsTheDisplayFileAgain() throws IOException {
		Path display = folder.resolve("display.yml");
		Keystone<TestSender> keystone = TestKeystone.create(platform, display);
		Files.writeString(display, "greeting:\n  chat: false\n  title: true\n");

		keystone.reload();
		keystone.recipients().of(alice).send(Message.of("greeting").with("player", "Alice"));

		assertTrue(alice.audience().shown().getFirst().startsWith("title: Hallo, Alice! | "));
	}

	@Test
	void anInvalidDisplayFileFailsTheStart() throws IOException {
		Files.writeString(folder.resolve("display.yml"), "greeting: [");

		assertThrows(DisplayFileException.class, () -> TestKeystone.create(platform, folder.resolve("display.yml")));
	}

	@Test
	void theGateHoldsUntilReleaseInTheLanguageOfThen() {
		Keystone<TestSender> keystone = TestKeystone.create(platform, null);
		Recipient recipient = keystone.recipients().of(alice);
		keystone.gate(ignored -> Gate.HOLD);

		recipient.send(Message.of("greeting").with("player", "one"));
		recipient.send(Message.of("greeting").with("player", "two"));
		keystone.console().send(Message.of("prefix"));
		assertTrue(alice.audience().shown().isEmpty());
		assertEquals(List.of("[S]"), platform.log());

		keystone.gate(ignored -> Gate.SEND);
		keystone.release(recipient);

		assertEquals(List.of("chat: Hallo, one!", "chat: Hallo, two!"), alice.audience().shown());
	}

	@Test
	void discardDropsWhatWasHeld() {
		Keystone<TestSender> keystone = TestKeystone.create(platform, null);
		Recipient recipient = keystone.recipients().of(alice);
		keystone.gate(ignored -> Gate.HOLD);
		recipient.send(Message.of("prefix"));

		keystone.discard(recipient);
		keystone.release(recipient);

		assertTrue(alice.audience().shown().isEmpty());
	}

	@Test
	void markupReadsWithItsOwnPolicy() {
		Keystone<TestSender> keystone = TestKeystone.create(platform, null);

		assertEquals("<red>x", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
				.serialize(keystone.markup(TagPolicy.of()).read("<red>x")));
	}
}
