package me.whereareiam.keystone.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.platform.Sender;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Velocity's command sources, players, logger and scheduler.
 */
@RequiredArgsConstructor
final class VelocityPlatform implements Platform<CommandSource> {
	private final ProxyServer server;
	private final Object plugin;
	private final Logger logger;

	@Override
	public @NotNull Sender sender(@NotNull CommandSource sender) {
		return new VelocitySender(sender);
	}

	@Override
	public @NotNull Sender console() {
		return sender(server.getConsoleCommandSource());
	}

	@Override
	public @NotNull Collection<Sender> players() {
		return server.getAllPlayers().stream().map(player -> sender(player)).toList();
	}

	@Override
	public @NotNull Optional<Sender> player(@NotNull UUID uniqueId) {
		return server.getPlayer(uniqueId).map(player -> sender(player));
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
