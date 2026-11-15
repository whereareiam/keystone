package me.whereareiam.keystone.platform.bukkit;

import me.whereareiam.keystone.common.fixture.RecordingAudience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BukkitAudiencePlatformTest {
	private final Plugin plugin = mock(Plugin.class);
	private final BukkitAudiences audiences = mock(BukkitAudiences.class);
	private final BukkitAudiencePlatform platform = new BukkitAudiencePlatform(plugin, audiences);

	@Test
	void aSendersAudienceComesFromThePluginsAudiences() {
		Player player = mock(Player.class);
		RecordingAudience audience = new RecordingAudience();
		when(audiences.sender(player)).thenReturn(audience);

		assertSame(audience, platform.sender(player).audience());
	}

	@Test
	void runsLaterTasksAfterWholeTicks() {
		Server server = mock(Server.class);
		BukkitScheduler scheduler = mock(BukkitScheduler.class);
		when(plugin.getServer()).thenReturn(server);
		when(server.getScheduler()).thenReturn(scheduler);
		Runnable task = () -> {
		};

		platform.later(Duration.ofMillis(120), task);
		platform.later(Duration.ZERO, task);

		verify(scheduler).runTaskLater(plugin, task, 3L);
		verify(scheduler).runTaskLater(plugin, task, 1L);
	}
}
