package me.whereareiam.keystone.platform.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.Duration;

/**
 * Velocity: command sources are audiences themselves, the console's lines go to the plugin's logger, and later
 * tasks run on Velocity's scheduler.
 */
@RequiredArgsConstructor
final class VelocityPlatform implements Platform {
	private final ProxyServer server;
	private final Object plugin;
	private final Logger logger;

	@Override
	public @NotNull Recipient console() {
		return new VelocityRecipient(server.getConsoleCommandSource());
	}

	@Override
	public void log(@NotNull String line) {
		logger.info(line);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		server.getScheduler().buildTask(plugin, task).delay(delay).schedule();
	}
}
