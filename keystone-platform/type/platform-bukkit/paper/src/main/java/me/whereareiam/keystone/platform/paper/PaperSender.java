package me.whereareiam.keystone.platform.paper;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Sender;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A Paper command sender, which is an audience itself.
 */
@RequiredArgsConstructor
final class PaperSender implements Sender {
	private final CommandSender sender;

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
		return sender instanceof Player player ? Optional.of(player.locale()) : Optional.empty();
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public @NotNull Audience audience() {
		return sender;
	}

	@Override
	public boolean isConsole() {
		return sender instanceof ConsoleCommandSender;
	}
}
