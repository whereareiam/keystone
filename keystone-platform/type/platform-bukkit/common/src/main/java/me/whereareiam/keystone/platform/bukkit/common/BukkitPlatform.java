package me.whereareiam.keystone.platform.bukkit.common;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * What Bukkit and Paper share: the console and the plugin's log. Bukkit and Paper differ in how a sender becomes a
 * recipient and in scheduling.
 */
@RequiredArgsConstructor
public abstract class BukkitPlatform implements Platform {
	/**
	 * The plugin using Keystone.
	 */
	protected final Plugin plugin;

	/**
	 * Returns the recipient of a command sender.
	 *
	 * @param sender the sender
	 * @return its recipient
	 */
	public abstract @NotNull Recipient recipient(@NotNull CommandSender sender);

	/**
	 * Returns the plugin using Keystone.
	 *
	 * @return the plugin
	 */
	public @NotNull Plugin plugin() {
		return plugin;
	}

	@Override
	public @NotNull Recipient console() {
		return recipient(plugin.getServer().getConsoleSender());
	}

	@Override
	public void log(@NotNull String line) {
		plugin.getLogger().info(line);
	}
}
