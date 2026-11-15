package me.whereareiam.keystone.platform.bukkit;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Sender;
import me.whereareiam.semantica.format.adventure.MinecraftLocales;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A Bukkit command sender and its audience; a player's client language is read from Bukkit's locale code, such as
 * {@code en_us}.
 */
@RequiredArgsConstructor
final class BukkitSender implements Sender {
	private final CommandSender sender;
	private final Audience audience;

	@Override
	public @NotNull String name() {
		return sender.getName();
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return sender instanceof Player player ? Optional.of(player.getUniqueId()) : Optional.empty();
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		if (!(sender instanceof Player player) || player.getLocale().isBlank()) return Optional.empty();

		return Optional.of(MinecraftLocales.toLocale(player.getLocale()));
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public @NotNull Audience audience() {
		return audience;
	}

	@Override
	public boolean isConsole() {
		return sender instanceof ConsoleCommandSender;
	}
}
