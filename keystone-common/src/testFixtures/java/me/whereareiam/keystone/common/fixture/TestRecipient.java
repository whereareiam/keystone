package me.whereareiam.keystone.common.fixture;

import me.whereareiam.keystone.Recipient;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * A recipient that records what it is shown.
 *
 * @param name      its name
 * @param id        a player's id, or {@code null}
 * @param language  the client's language, or {@code null}
 * @param audience  what it is shown
 * @param isConsole whether it is the console
 */
public record TestRecipient(
		@NotNull String name,
		UUID id,
		Locale language,
		@NotNull RecordingAudience audience,
		boolean isConsole
) implements Recipient {
	public static @NotNull TestRecipient player(@NotNull String name, @NotNull Locale language) {
		return new TestRecipient(name, UUID.nameUUIDFromBytes(name.getBytes()), language, new RecordingAudience(), false);
	}

	public static @NotNull TestRecipient console() {
		return new TestRecipient("CONSOLE", null, null, new RecordingAudience(), true);
	}

	public static @NotNull TestRecipient commandBlock() {
		return new TestRecipient("@", null, null, new RecordingAudience(), false);
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return Optional.ofNullable(id);
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return Optional.ofNullable(language);
	}
}
