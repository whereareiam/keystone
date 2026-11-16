package me.whereareiam.keystone.platform.paper;

import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPlatform;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Paper: senders are audiences themselves, and later tasks run on the asynchronous scheduler, which Folia has too.
 */
final class PaperPlatform extends BukkitPlatform {
	PaperPlatform(@NotNull Plugin plugin) {
		super(plugin);
	}

	@Override
	public @NotNull Recipient recipient(@NotNull CommandSender sender) {
		return new PaperRecipient(sender);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduled -> task.run(), delay.toMillis(), TimeUnit.MILLISECONDS);
	}
}
