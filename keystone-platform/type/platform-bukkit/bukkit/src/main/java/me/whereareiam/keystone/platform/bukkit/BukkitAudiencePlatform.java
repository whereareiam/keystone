package me.whereareiam.keystone.platform.bukkit;

import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Bukkit and Spigot: senders reach players through adventure-platform, and later tasks run on the main thread's
 * scheduler, in whole ticks.
 */
final class BukkitAudiencePlatform extends BukkitPlatform {
	private static final long TICK_MILLIS = 50;

	private final BukkitAudiences audiences;

	BukkitAudiencePlatform(@NotNull Plugin plugin, @NotNull BukkitAudiences audiences) {
		super(plugin);
		this.audiences = audiences;
	}

	@Override
	public @NotNull Recipient recipient(@NotNull CommandSender sender) {
		return new BukkitRecipient(sender, audiences.sender(sender));
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		long ticks = Math.max(1, (delay.toMillis() + TICK_MILLIS - 1) / TICK_MILLIS);
		plugin.getServer().getScheduler().runTaskLater(plugin, task, ticks);
	}
}
