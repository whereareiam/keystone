package me.whereareiam.keystone.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The display file cannot be read: it is no valid YAML, or an entry is no valid display. When it happens on
 * {@link me.whereareiam.keystone.Keystone#reload()}, the displays read before stay in use.
 */
@Getter
public final class DisplayFileException extends RuntimeException {
	/**
	 * The display file.
	 */
	private final Path path;

	/**
	 * The key of the entry that cannot be read, or {@code null} when the file as a whole cannot be read.
	 */
	private final @Nullable String key;

	/**
	 * Creates the exception.
	 *
	 * @param path    the display file
	 * @param key     the key of the entry, or {@code null} for the file as a whole
	 * @param message what is wrong
	 * @param cause   the failure underneath, if any
	 */
	public DisplayFileException(@NotNull Path path, @Nullable String key, @NotNull String message, @Nullable Throwable cause) {
		super(path + (key == null ? "" : " (" + key + ")") + ": " + message, cause);
		this.path = path;
		this.key = key;
	}
}
