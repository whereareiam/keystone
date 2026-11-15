package me.whereareiam.keystone.platform.bukkit.common;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.platform.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * What Bukkit and Paper share: the online players and the plugin's log. Bukkit and Paper differ in how a sender
 * becomes an audience and in scheduling.
 */
@RequiredArgsConstructor
public abstract class BukkitPlatform implements Platform<CommandSender> {
	/**
	 * The plugin using Keystone.
	 */
	protected final Plugin plugin;

	@Override
	public @NotNull Sender console() {
		return sender(plugin.getServer().getConsoleSender());
	}

	@Override
	public @NotNull Collection<Sender> players() {
		return plugin.getServer().getOnlinePlayers().stream().map(player -> sender((CommandSender) player)).toList();
	}

	@Override
	public @NotNull Optional<Sender> player(@NotNull UUID uniqueId) {
		Player player = plugin.getServer().getPlayer(uniqueId);
		return Optional.ofNullable(player).map(this::sender);
	}

	@Override
	public void log(@NotNull String line) {
		plugin.getLogger().info(line);
	}
}
