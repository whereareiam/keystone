package me.whereareiam.keystone.platform.bukkit;

import me.whereareiam.keystone.common.platform.Sender;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Bukkit's senders, through adventure-platform-bukkit, and Bukkit's scheduler.
 */
final class BukkitAudiencePlatform extends BukkitPlatform {
	private static final long TICK_MILLIS = 50;

	private final BukkitAudiences audiences;

	BukkitAudiencePlatform(@NotNull Plugin plugin, @NotNull BukkitAudiences audiences) {
		super(plugin);
		this.audiences = audiences;
	}

	@Override
	public @NotNull Sender sender(@NotNull CommandSender sender) {
		return new BukkitSender(sender, audiences.sender(sender));
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		long ticks = Math.max(1, (delay.toMillis() + TICK_MILLIS - 1) / TICK_MILLIS);
		plugin.getServer().getScheduler().runTaskLater(plugin, task, ticks);
	}
}
