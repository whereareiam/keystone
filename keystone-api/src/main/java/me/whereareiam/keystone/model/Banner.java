package me.whereareiam.keystone.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.whereareiam.semantica.Content;
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
public final class Banner {
	private final List<Content> lines;
	private final Map<String, Object> values;

	private Banner(List<Content> lines, Map<String, Object> values) {
		this.lines = lines;
		this.values = values;
	}

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
	 * Returns this banner with one more value its lines see, replacing a value of the same name.
	 *
	 * @param name  the value's name, as the lines write it: {@code {version}}
	 * @param value the value
	 * @return a new banner
	 */
	public @NotNull Banner with(@NotNull String name, @NotNull Object value) {
		Map<String, Object> copy = new LinkedHashMap<>(values);
		copy.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value"));

		return new Banner(lines, Collections.unmodifiableMap(copy));
	}
}
