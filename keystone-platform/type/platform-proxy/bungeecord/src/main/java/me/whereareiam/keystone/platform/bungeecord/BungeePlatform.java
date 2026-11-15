package me.whereareiam.keystone.platform.bungeecord;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.platform.Sender;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * BungeeCord's senders, through adventure-platform-bungeecord, and its logger and scheduler.
 */
@RequiredArgsConstructor
final class BungeePlatform implements Platform<CommandSender> {
	private final Plugin plugin;
	private final BungeeAudiences audiences;

	@Override
	public @NotNull Sender sender(@NotNull CommandSender sender) {
		return new BungeeSender(sender, audiences.sender(sender), sender == plugin.getProxy().getConsole());
	}

	@Override
	public @NotNull Sender console() {
		return sender(plugin.getProxy().getConsole());
	}

	@Override
	public @NotNull Collection<Sender> players() {
		return plugin.getProxy().getPlayers().stream().map(player -> sender((CommandSender) player)).toList();
	}

	@Override
	public @NotNull Optional<Sender> player(@NotNull UUID uniqueId) {
		ProxiedPlayer player = plugin.getProxy().getPlayer(uniqueId);
		return Optional.ofNullable(player).map(found -> sender((CommandSender) found));
	}

	@Override
	public void log(@NotNull String line) {
		plugin.getLogger().info(line);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		plugin.getProxy().getScheduler().schedule(plugin, task, delay.toMillis(), TimeUnit.MILLISECONDS);
	}
}
