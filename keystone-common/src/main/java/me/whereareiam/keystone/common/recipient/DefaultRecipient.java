package me.whereareiam.keystone.common.recipient;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.delivery.Delivery;
import me.whereareiam.keystone.common.display.DisplayFile;
import me.whereareiam.keystone.common.platform.Sender;
import me.whereareiam.keystone.model.Display;
import me.whereareiam.keystone.model.Values;
import me.whereareiam.keystone.recipient.Recipient;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A player or another sender that is not the console.
 */
@RequiredArgsConstructor
public final class DefaultRecipient implements Recipient {
	private final Sender sender;
	private final LanguageChain languages;
	private final Delivery delivery;
	private final DisplayFile displays;

	@Override
	public @NotNull String name() {
		return sender.name();
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return sender.uniqueId();
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return sender.clientLanguage();
	}

	@Override
	public @NotNull List<Locale> languages() {
		return languages.resolve(this);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public @NotNull Audience audience() {
		return sender.audience();
	}

	@Override
	public void send(@NotNull Message message) {
		delivery.send(this, displays.of(message.getKey()), message.getValues());
	}

	@Override
	public void send(@NotNull Content content, @NotNull Values values) {
		delivery.send(this, Display.chat(content), values.asMap());
	}

	@Override
	public void send(@NotNull Display display, @NotNull Values values) {
		delivery.send(this, display, values.asMap());
	}

	@Override
	public String toString() {
		return "Recipient[" + sender.name() + "]";
	}
}
