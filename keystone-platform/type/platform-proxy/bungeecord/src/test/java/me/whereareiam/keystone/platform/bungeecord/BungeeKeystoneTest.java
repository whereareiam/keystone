package me.whereareiam.keystone.platform.bungeecord;

import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.fixture.RecordingAudience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BungeeKeystoneTest {
	private final Plugin plugin = mock(Plugin.class);
	private final ProxyServer proxy = mock(ProxyServer.class);
	private final BungeeAudiences audiences = mock(BungeeAudiences.class);
	private final CommandSender console = mock(CommandSender.class);
	private final BungeePlatform platform = new BungeePlatform(plugin, audiences);

	BungeeKeystoneTest() {
		when(plugin.getProxy()).thenReturn(proxy);
		when(proxy.getConsole()).thenReturn(console);
	}

	@Test
	void aPlayerHasTheirLocaleAndAnAudienceOfThePlugins() {
		ProxiedPlayer player = mock(ProxiedPlayer.class);
		UUID id = UUID.randomUUID();
		RecordingAudience audience = new RecordingAudience();
		when(player.getName()).thenReturn("Alice");
		when(player.getUniqueId()).thenReturn(id);
		when(player.getLocale()).thenReturn(Locale.GERMAN);
		when(audiences.sender(player)).thenReturn(audience);

		Recipient sender = platform.recipient(player);

		assertEquals("Alice", sender.name());
		assertEquals(Optional.of(id), sender.uniqueId());
		assertEquals(Optional.of(Locale.GERMAN), sender.clientLanguage());
		assertSame(audience, sender.audience());
		assertFalse(sender.isConsole());
	}

	@Test
	void theProxysConsoleIsTheConsole() {
		assertTrue(platform.console().isConsole());
		assertTrue(platform.console().clientLanguage().isEmpty());
	}

	@Test
	void logsAndSchedules() {
		TaskScheduler scheduler = mock(TaskScheduler.class);
		when(proxy.getScheduler()).thenReturn(scheduler);
		java.util.logging.Logger logger = mock(java.util.logging.Logger.class);
		when(plugin.getLogger()).thenReturn(logger);
		Runnable task = () -> {
		};

		platform.log("hello");
		platform.later(Duration.ofSeconds(1), task);

		verify(logger).info("hello");
		verify(scheduler).schedule(plugin, task, 1000L, TimeUnit.MILLISECONDS);
	}

	@Test
	void aPluginIsPresentByName() {
		PluginManager manager = mock(PluginManager.class);
		Plugin bridge = mock(Plugin.class);
		PluginDescription description = new PluginDescription();
		description.setName("PAPIProxyBridge");
		when(proxy.getPluginManager()).thenReturn(manager);
		when(manager.getPlugins()).thenReturn(List.of(bridge));
		when(bridge.getDescription()).thenReturn(description);

		BungeeKeystone.Builder<CommandSender> builder = BungeeKeystone.messenger(plugin, audiences);

		assertTrue(builder.present("papiproxybridge"));
		assertFalse(builder.present("Intercept"));
		assertEquals(1, builder.integrations().size());
	}
}
