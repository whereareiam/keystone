package me.whereareiam.keystone.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.whereareiam.keystone.type.part.Chat;
import me.whereareiam.keystone.type.part.Part;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The parts one text is shown as: chat, action bar, title, boss bar and sound, with the values its texts see. It is
 * immutable: {@link #with(String, Object)} returns a new display.
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
 * <pre>{@code
 * messenger.send(player, announcement.getVote().with("player", name));
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Display {
	private static final Display NONE = new Display(List.of(), Map.of());

	private final List<Part> parts;
	private final Map<String, Object> values;

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
		return new Display(List.copyOf(parts), Map.of());
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
	 * Returns this display with one more value its texts see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the texts write it: {@code {player}}
	 * @param value the value; never read as markup
	 * @return a new display
	 */
	public @NotNull Display with(@NotNull String name, @NotNull Object value) {
		return with(Map.of(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value")));
	}

	/**
	 * Returns this display with more values its texts see, replacing values of the same names.
	 *
	 * @param values the values by name
	 * @return a new display
	 */
	public @NotNull Display with(@NotNull Map<String, ?> values) {
		Map<String, Object> copy = new LinkedHashMap<>(this.values);
		values.forEach((name, value) -> copy.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, name)));

		return new Display(parts, Collections.unmodifiableMap(copy));
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
