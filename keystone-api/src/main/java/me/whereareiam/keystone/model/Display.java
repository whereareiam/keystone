package me.whereareiam.keystone.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The parts one text is shown as: chat, action bar, title, boss bar and sound. It is immutable.
 * <p>
 * The display file, {@code display.yml}, holds one for each translated message shown as more than chat; a config
 * holds one wherever an admin decides how a text is shown, such as an announcement. Both are written the same way,
 * through Keystone's Configura module:
 * <pre>{@code
 * announcements:
 *   vote:
 *     chat: "{@chirper.vote}"
 *     bossbar:
 *       text: "<green>/vote"
 *       color: green
 *       seconds: 10
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
public final class Display {
	private static final Display NONE = new Display(List.of());

	private final List<Part> parts;

	private Display(List<Part> parts) {
		this.parts = parts;
	}

	/**
	 * Returns a display that shows nothing.
	 *
	 * @return the empty display
	 */
	public static @NotNull Display none() {
		return NONE;
	}

	/**
	 * Returns a display of parts, shown in the order given.
	 *
	 * @param parts the parts
	 * @return the display
	 */
	public static @NotNull Display of(@NotNull Part... parts) {
		return of(List.of(parts));
	}

	/**
	 * Returns a display of parts, shown in the order given.
	 *
	 * @param parts the parts
	 * @return the display
	 */
	public static @NotNull Display of(@NotNull List<? extends Part> parts) {
		return new Display(List.copyOf(parts));
	}

	/**
	 * Returns a display of the chat alone.
	 *
	 * @param text the chat's text
	 * @return the display
	 */
	public static @NotNull Display chat(@NotNull Content text) {
		return of(Chat.of(text));
	}

	/**
	 * Tells whether the display shows nothing.
	 *
	 * @return {@code true} when it has no part
	 */
	public boolean isEmpty() {
		return parts.isEmpty();
	}
}
