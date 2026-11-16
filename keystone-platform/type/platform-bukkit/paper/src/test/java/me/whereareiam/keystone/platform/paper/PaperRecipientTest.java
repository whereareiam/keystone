package me.whereareiam.keystone.platform.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaperRecipientTest {
	@Test
	void aPlayerHasAnIdAndTheLanguageOfTheirClient() {
		Player player = mock(Player.class);
		UUID id = UUID.randomUUID();
		when(player.getName()).thenReturn("Alice");
		when(player.getUniqueId()).thenReturn(id);
		when(player.locale()).thenReturn(Locale.GERMANY);

		PaperRecipient sender = new PaperRecipient(player);

		assertEquals("Alice", sender.name());
		assertEquals(Optional.of(id), sender.uniqueId());
		assertEquals(Optional.of(Locale.GERMANY), sender.clientLanguage());
		assertSame(player, sender.audience());
		assertFalse(sender.isConsole());
	}

	@Test
	void theConsoleHasNeither() {
		PaperRecipient sender = new PaperRecipient(mock(ConsoleCommandSender.class));

		assertTrue(sender.uniqueId().isEmpty());
		assertTrue(sender.clientLanguage().isEmpty());
		assertTrue(sender.isConsole());
		assertFalse(new PaperRecipient(mock(CommandSender.class)).isConsole());
	}
}
