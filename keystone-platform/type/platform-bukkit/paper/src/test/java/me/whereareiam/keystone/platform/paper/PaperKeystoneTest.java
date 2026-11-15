package me.whereareiam.keystone.platform.paper;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.common.fixture.TestSemantica;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaperKeystoneTest {
	private final Plugin plugin = mock(Plugin.class);
	private final Server server = mock(Server.class);

	PaperKeystoneTest() {
		PluginManager manager = mock(PluginManager.class);
		when(plugin.getServer()).thenReturn(server);
		when(server.getPluginManager()).thenReturn(manager);
		when(manager.getPlugins()).thenReturn(new Plugin[0]);
	}

	@Test
	void sendsToAPlayerInTheLanguageOfTheirClient() {
		Player player = mock(Player.class);
		when(player.getUniqueId()).thenReturn(UUID.randomUUID());
		when(player.locale()).thenReturn(Locale.GERMAN);
		Keystone<CommandSender> keystone = PaperKeystone.builder(plugin).semantica(TestSemantica.create()).build();

		keystone.recipients().of(player).send(Message.of("greeting").with("player", "Alice"));

		ArgumentCaptor<Component> sent = ArgumentCaptor.forClass(Component.class);
		verify(player).sendMessage(sent.capture());
		assertEquals("Hallo, Alice!", PlainTextComponentSerializer.plainText().serialize(sent.getValue()));
	}

	@Test
	void runsLaterTasksOnTheAsyncScheduler() {
		AsyncScheduler scheduler = mock(AsyncScheduler.class);
		when(server.getAsyncScheduler()).thenReturn(scheduler);

		new PaperPlatform(plugin).later(Duration.ofSeconds(2), () -> {
		});

		verify(scheduler).runDelayed(eq(plugin), any(), eq(2000L), eq(TimeUnit.MILLISECONDS));
	}
}
