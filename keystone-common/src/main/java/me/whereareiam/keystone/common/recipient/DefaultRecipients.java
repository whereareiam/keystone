package me.whereareiam.keystone.common.recipient;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.platform.Sender;
import me.whereareiam.keystone.recipient.Console;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.keystone.recipient.RecipientGroup;
import me.whereareiam.keystone.recipient.Recipients;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Finds recipients through the platform; a sender that is the console is the one {@link Console}.
 *
 * @param <S> the platform's command sender
 */
@RequiredArgsConstructor
public final class DefaultRecipients<S> implements Recipients<S> {
	private final Platform<S> platform;
	private final Console console;
	private final Function<Sender, Recipient> recipients;

	@Override
	public @NotNull Recipient of(@NotNull S sender) {
		Sender found = platform.sender(sender);
		if (found.isConsole()) return console;

		return recipients.apply(found);
	}

	@Override
	public @NotNull Optional<Recipient> player(@NotNull UUID uniqueId) {
		return platform.player(uniqueId).map(recipients);
	}

	@Override
	public @NotNull RecipientGroup players() {
		return new DefaultRecipientGroup(platform.players().stream().map(recipients).toList());
	}

	@Override
	public @NotNull RecipientGroup permission(@NotNull String permission) {
		return new DefaultRecipientGroup(platform.players().stream()
				.filter(player -> player.hasPermission(permission))
				.map(recipients)
				.toList());
	}
}
