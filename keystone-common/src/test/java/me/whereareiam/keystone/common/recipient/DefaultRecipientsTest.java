package me.whereareiam.keystone.common.recipient;

import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.common.fixture.TestKeystone;
import me.whereareiam.keystone.common.fixture.TestPlatform;
import me.whereareiam.keystone.common.fixture.TestSender;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.model.Values;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultRecipientsTest {
	private final TestSender alice = TestSender.player("Alice", Locale.GERMAN, "staff");
	private final TestSender bob = TestSender.player("Bob", Locale.ENGLISH);
	private final TestPlatform platform = new TestPlatform().join(alice).join(bob);
	private final Keystone<TestSender> keystone = TestKeystone.create(platform, null);

	@Test
	void theConsoleSenderIsTheConsole() {
		assertSame(keystone.console(), keystone.recipients().of(platform.consoleSender()));
	}

	@Test
	void aPlayerIsARecipientOfTheirOwn() {
		Recipient recipient = keystone.recipients().of(alice);

		assertEquals("Alice", recipient.name());
		assertEquals(alice.uniqueId(), recipient.uniqueId());
		assertEquals(List.of(Locale.GERMAN, Locale.ENGLISH), recipient.languages());
		assertSame(alice.audience(), recipient.audience());
		assertTrue(recipient.hasPermission("staff"));
	}

	@Test
	void anotherSenderGetsTheFallback() {
		Recipient recipient = keystone.recipients().of(TestSender.commandBlock());

		assertTrue(recipient.uniqueId().isEmpty());
		assertEquals(List.of(Locale.ENGLISH), recipient.languages());
	}

	@Test
	void findsPlayersById() {
		assertEquals("Bob", keystone.recipients().player(bob.id()).orElseThrow().name());
		assertTrue(keystone.recipients().player(UUID.randomUUID()).isEmpty());
	}

	@Test
	void aGroupSendsToEachInTheirOwnLanguage() {
		keystone.recipients().players().send(Message.of("greeting").with("player", "you"));

		assertEquals(List.of("chat: Hallo, you!"), alice.audience().shown());
		assertEquals(List.of("chat: Hello, you!"), bob.audience().shown());
	}

	@Test
	void permissionPicksThePlayersWhoHaveIt() {
		var staff = keystone.recipients().permission("staff");
		staff.send(Content.of("{@prefix} joined"), Values.none());
		staff.send(Display.chat(Content.of("again")), Values.none());

		assertEquals(List.of("Alice"), staff.members().stream().map(Recipient::name).toList());
		assertEquals(List.of("chat: [S] joined", "chat: again"), alice.audience().shown());
		assertTrue(bob.audience().shown().isEmpty());
	}
}
