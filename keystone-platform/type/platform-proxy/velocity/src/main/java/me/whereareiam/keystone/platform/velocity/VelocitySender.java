package me.whereareiam.keystone.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Sender;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A Velocity command source, which is an audience itself.
 */
@RequiredArgsConstructor
final class VelocitySender implements Sender {
	private final CommandSource source;

	@Override
	public @NotNull String name() {
		if (source instanceof Player player) return player.getUsername();
		return isConsole() ? "CONSOLE" : source.toString();
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return source instanceof Player player ? Optional.of(player.getUniqueId()) : Optional.empty();
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return source instanceof Player player ? Optional.ofNullable(player.getEffectiveLocale()) : Optional.empty();
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return source.hasPermission(permission);
	}

	@Override
	public @NotNull Audience audience() {
		return source;
	}

	@Override
	public boolean isConsole() {
		return source instanceof ConsoleCommandSource;
	}
}
