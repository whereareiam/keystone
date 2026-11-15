package me.whereareiam.keystone.platform.paper;

import me.whereareiam.keystone.common.platform.Sender;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPlatform;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Paper's senders and scheduler.
 */
final class PaperPlatform extends BukkitPlatform {
	PaperPlatform(@NotNull Plugin plugin) {
		super(plugin);
	}

	@Override
	public @NotNull Sender sender(@NotNull CommandSender sender) {
		return new PaperSender(sender);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduled -> task.run(), delay.toMillis(), TimeUnit.MILLISECONDS);
	}
}
