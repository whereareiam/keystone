package me.whereareiam.keystone.common.fixture;

import me.whereareiam.keystone.common.platform.Sender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A sender of the test platform.
 *
 * @param name        its name
 * @param id          a player's id, or {@code null}
 * @param language    the client's language, or {@code null}
 * @param permissions the permissions it has
 * @param audience    what it is shown
 * @param isConsole   whether it is the console
 */
public record TestSender(
		@NotNull String name,
		UUID id,
		Locale language,
		@NotNull Set<String> permissions,
		@NotNull RecordingAudience audience,
		boolean isConsole
) implements Sender {
	public static @NotNull TestSender player(@NotNull String name, @NotNull Locale language, @NotNull String... permissions) {
		return new TestSender(name, UUID.nameUUIDFromBytes(name.getBytes()), language, Set.of(permissions), new RecordingAudience(), false);
	}

	public static @NotNull TestSender console() {
		return new TestSender("CONSOLE", null, null, Set.of(), new RecordingAudience(), true);
	}

	public static @NotNull TestSender commandBlock() {
		return new TestSender("@", null, null, Set.of(), new RecordingAudience(), false);
	}

	@Override
	public @NotNull Optional<UUID> uniqueId() {
		return Optional.ofNullable(id);
	}

	@Override
	public @NotNull Optional<Locale> clientLanguage() {
		return Optional.ofNullable(language);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return permissions.contains(permission);
	}
}
