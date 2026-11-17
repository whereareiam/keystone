package me.whereareiam.keystone.integration.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.Recipient;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Fills PlaceholderAPI's placeholders for the player who receives a text; for the console and other senders, the
 * placeholders that need no player.
 */
final class PlaceholderApiPlaceholders implements Placeholders.Text {
	@Override
	public @NotNull String fill(@NotNull Recipient recipient, @NotNull String text) {
		if (!PlaceholderAPI.containsPlaceholders(text)) return text;

		OfflinePlayer player = recipient.uniqueId().map(Bukkit::getOfflinePlayer).orElse(null);
		return PlaceholderAPI.setPlaceholders(player, text);
	}
}
