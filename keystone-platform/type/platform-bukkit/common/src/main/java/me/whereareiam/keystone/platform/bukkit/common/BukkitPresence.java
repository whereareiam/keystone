package me.whereareiam.keystone.platform.bukkit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Tells whether another plugin is enabled on a Bukkit or Paper server.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitPresence {
	/**
	 * Tells whether a plugin is enabled.
	 *
	 * @param plugin the plugin using Keystone
	 * @param name   the other plugin's name, compared without regard to case
	 * @return {@code true} when it is enabled
	 */
	public static boolean enabled(@NotNull Plugin plugin, @NotNull String name) {
		return Arrays.stream(plugin.getServer().getPluginManager().getPlugins())
				.anyMatch(other -> other.getName().equalsIgnoreCase(name) && other.isEnabled());
	}
}
