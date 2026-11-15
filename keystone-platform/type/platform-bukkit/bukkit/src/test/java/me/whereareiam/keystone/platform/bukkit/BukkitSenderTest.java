package me.whereareiam.keystone.platform.bukkit;

import me.whereareiam.keystone.common.fixture.RecordingAudience;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BukkitSenderTest {
	@Test
	void readsTheClientsLocaleCode() {
		Player player = mock(Player.class);
		when(player.getLocale()).thenReturn("de_de");
		RecordingAudience audience = new RecordingAudience();

		BukkitSender sender = new BukkitSender(player, audience);

		assertEquals(Optional.of(Locale.forLanguageTag("de-DE")), sender.clientLanguage());
		assertSame(audience, sender.audience());
	}

	@Test
	void aPlayerWithoutLocaleHasNoClientLanguage() {
		Player player = mock(Player.class);
		when(player.getLocale()).thenReturn("");

		assertTrue(new BukkitSender(player, new RecordingAudience()).clientLanguage().isEmpty());
	}

	@Test
	void theConsoleIsTheConsole() {
		BukkitSender sender = new BukkitSender(mock(ConsoleCommandSender.class), new RecordingAudience());

		assertTrue(sender.isConsole());
		assertTrue(sender.uniqueId().isEmpty());
	}
}
