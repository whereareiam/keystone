package me.whereareiam.keystone.platform.bungeecord;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A BungeeCord command sender and its audience.
 */
@RequiredArgsConstructor
final class BungeeRecipient implements Recipient {
	private final CommandSender sender;
	private final Audience audience;
	private final boolean console;

	@Override
	public @NotNull String name() {
		return sender.getName();
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return sender instanceof ProxiedPlayer player ? Optional.of(player.getUniqueId()) : Optional.empty();
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return sender instanceof ProxiedPlayer player ? Optional.ofNullable(player.getLocale()) : Optional.empty();
	}

	@Override
	public @NotNull Audience audience() {
		return audience;
	}

	@Override
	public boolean isConsole() {
		return console;
	}
}
