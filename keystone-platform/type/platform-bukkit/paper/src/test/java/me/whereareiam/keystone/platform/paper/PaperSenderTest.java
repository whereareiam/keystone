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

class PaperSenderTest {
	@Test
	void aPlayerHasAnIdAndTheLanguageOfTheirClient() {
		Player player = mock(Player.class);
		UUID id = UUID.randomUUID();
		when(player.getName()).thenReturn("Alice");
		when(player.getUniqueId()).thenReturn(id);
		when(player.locale()).thenReturn(Locale.GERMANY);
		when(player.hasPermission("staff")).thenReturn(true);

		PaperSender sender = new PaperSender(player);

		assertEquals("Alice", sender.name());
		assertEquals(Optional.of(id), sender.uniqueId());
		assertEquals(Optional.of(Locale.GERMANY), sender.clientLanguage());
		assertTrue(sender.hasPermission("staff"));
		assertSame(player, sender.audience());
		assertFalse(sender.isConsole());
	}

	@Test
	void theConsoleHasNeither() {
		PaperSender sender = new PaperSender(mock(ConsoleCommandSender.class));

		assertTrue(sender.uniqueId().isEmpty());
		assertTrue(sender.clientLanguage().isEmpty());
		assertTrue(sender.isConsole());
		assertFalse(new PaperSender(mock(CommandSender.class)).isConsole());
	}
}
