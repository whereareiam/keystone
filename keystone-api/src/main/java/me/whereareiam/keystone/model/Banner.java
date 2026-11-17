package me.whereareiam.keystone.model;

import java.time.temporal.TemporalAccessor;
import java.util.Date;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import me.whereareiam.semantica.Value;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The lines a plugin writes to the console when it starts, such as its name, version and platform. Each line is a
 * text that may hold MiniMessage tags, arguments and references to translated messages; it is immutable.
 * <pre>{@code
 * Banner banner = Banner.of(
 *                 Content.of("<gold>  █▀ █▀▀   <white>Socialismus v<gray>{version}"),
 *                 Content.of("<gold>  ▄█ █▄▄   <white>Platform: <gray>{platform}"))
 *         .with("version", version)
 *         .with("platform", "Paper");
 *
 * messenger.print(banner);
 * }</pre>
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Banner {
	private final List<Content> lines;
	private final Map<String, Value> values;

	/**
	 * Returns a banner of lines.
	 *
	 * @param lines the lines, in order
	 * @return the banner, with no values
	 */
	public static @NotNull Banner of(@NotNull Content... lines) {
		return of(List.of(lines));
	}

	/**
	 * Returns a banner of lines.
	 *
	 * @param lines the lines, in order
	 * @return the banner, with no values
	 */
	public static @NotNull Banner of(@NotNull List<Content> lines) {
		return new Banner(List.copyOf(lines), Map.of());
	}

	/**
	 * Returns this banner with one more text its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the text, never read as markup
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull String value) {
		return with(name, Value.text(value));
	}

	/**
	 * Returns this banner with one more number its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the number
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull Number value) {
		return with(name, Value.number(value));
	}

	/**
	 * Returns this banner with one more date or time its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value an instant, a zoned, offset or local date-time, a local date or a local time
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull TemporalAccessor value) {
		return with(name, Value.time(value));
	}

	/**
	 * Returns this banner with one more date or time its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the date, read as an instant
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull Date value) {
		return with(name, Value.time(value));
	}

	/**
	 * Returns this banner with one more boolean its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the boolean
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, boolean value) {
		return with(name, Value.flag(value));
	}

	/**
	 * Returns this banner with one more enum constant its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the constant
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull Enum<?> value) {
		return with(name, Value.choice(value));
	}

	/**
	 * Returns this banner with one more message its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the message, resolved in the reader's language
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull Message value) {
		return with(name, Value.nested(value));
	}

	/**
	 * Returns this banner with one more value its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {player}}
	 * @param value the value
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull Value value) {
		return with(Map.of(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value")));
	}

	/**
	 * Returns this banner with more values its lines see, replacing values of the same names.
	 *
	 * @param values the values by name
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull Map<String, ? extends Value> values) {
		Map<String, Value> copy = new LinkedHashMap<>(this.values);
		values.forEach((name, value) -> copy.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, name)));

		return new Banner(lines, Collections.unmodifiableMap(copy));
	}
}
