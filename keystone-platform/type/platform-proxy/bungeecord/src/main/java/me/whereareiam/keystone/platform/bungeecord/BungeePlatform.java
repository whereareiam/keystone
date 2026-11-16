package me.whereareiam.keystone.platform.bungeecord;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * BungeeCord: senders reach players through adventure-platform, and later tasks run on the proxy's scheduler.
 */
@RequiredArgsConstructor
final class BungeePlatform implements Platform {
	private final Plugin plugin;
	private final BungeeAudiences audiences;

	/**
	 * Returns the recipient of a command sender.
	 *
	 * @param sender the sender
	 * @return its recipient
	 */
	@NotNull Recipient recipient(@NotNull CommandSender sender) {
		return new BungeeRecipient(sender, audiences.sender(sender), sender == plugin.getProxy().getConsole());
	}

	@NotNull Plugin plugin() {
		return plugin;
	}

	@Override
	public @NotNull Recipient console() {
		return recipient(plugin.getProxy().getConsole());
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
