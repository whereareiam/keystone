package me.whereareiam.keystone.platform.neoforge;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.semantica.format.adventure.MinecraftLocales;
import net.kyori.adventure.audience.Audience;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A NeoForge command source and its audience. Only a source that is a player has an id and a client language.
 */
@RequiredArgsConstructor
final class NeoForgeRecipient implements Recipient {
	private final CommandSourceStack source;
	private final Audience audience;
	private final boolean console;

	@Override
	public @NotNull String name() {
		return source.getTextName();
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return player().map(ServerPlayer::getUUID);
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return player()
				.map(player -> MinecraftLocales.toLocale(player.clientInformation().language()))
				.filter(locale -> !locale.equals(Locale.ROOT));
	}

	@Override
	public @NotNull Audience audience() {
		return audience;
	}

	@Override
	public boolean isConsole() {
		return console;
	}

	private Optional<ServerPlayer> player() {
		return Optional.ofNullable(source.getPlayer());
	}
}
