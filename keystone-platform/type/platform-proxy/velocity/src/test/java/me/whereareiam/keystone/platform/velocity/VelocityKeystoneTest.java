package me.whereareiam.keystone.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VelocityKeystoneTest {
	private final ProxyServer server = mock(ProxyServer.class);
	private final Logger logger = mock(Logger.class);
	private final Object plugin = new Object();
	private final VelocityPlatform platform = new VelocityPlatform(server, plugin, logger);

	@Test
	void aPlayerHasTheirEffectiveLocale() {
		Player player = mock(Player.class);
		UUID id = UUID.randomUUID();
		when(player.getUsername()).thenReturn("Alice");
		when(player.getUniqueId()).thenReturn(id);
		when(player.getEffectiveLocale()).thenReturn(Locale.GERMAN);

		Recipient sender = new VelocityRecipient(player);

		assertEquals("Alice", sender.name());
		assertEquals(Optional.of(id), sender.uniqueId());
		assertEquals(Optional.of(Locale.GERMAN), sender.clientLanguage());
		assertSame(player, sender.audience());
		assertFalse(sender.isConsole());
	}

	@Test
	void theConsoleIsNamedConsole() {
		ConsoleCommandSource console = mock(ConsoleCommandSource.class);
		when(server.getConsoleCommandSource()).thenReturn(console);

		assertEquals("CONSOLE", platform.console().name());
		assertTrue(platform.console().isConsole());
		assertTrue(new VelocityRecipient(mock(CommandSource.class)).uniqueId().isEmpty());
	}

	@Test
	void logsAndSchedules() {
		Scheduler scheduler = mock(Scheduler.class);
		Scheduler.TaskBuilder task = mock(Scheduler.TaskBuilder.class, RETURNS_SELF);
		when(task.schedule()).thenReturn(mock(ScheduledTask.class));
		Runnable runnable = () -> {
		};
		when(server.getScheduler()).thenReturn(scheduler);
		when(scheduler.buildTask(plugin, runnable)).thenReturn(task);

		platform.log("hello");
		platform.later(Duration.ofSeconds(3), runnable);

		verify(logger).info("hello");
		verify(task).delay(Duration.ofSeconds(3));
		verify(task).schedule();
	}

	@Test
	void aPluginIsPresentByIdOrName() {
		PluginManager manager = mock(PluginManager.class);
		PluginContainer container = mock(PluginContainer.class);
		PluginDescription description = mock(PluginDescription.class);
		when(server.getPluginManager()).thenReturn(manager);
		when(manager.getPlugins()).thenReturn(List.of(container));
		when(container.getDescription()).thenReturn(description);
		when(description.getId()).thenReturn("papiproxybridge");
		when(description.getName()).thenReturn(Optional.of("PAPIProxyBridge"));

		VelocityKeystone.Builder<CommandSource> builder = VelocityKeystone.messenger(server, plugin, logger).semantica(TestSemantica.create());

		assertTrue(builder.present("PAPIProxyBridge"));
		assertFalse(builder.present("MiniPlaceholders"));
		assertEquals(3, builder.integrations().size());
	}
}
