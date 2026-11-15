package me.whereareiam.keystone.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * The lines a plugin writes to the console when it starts, such as its name, version and platform. Each line is a
 * text that may hold MiniMessage tags, arguments and references to translated messages; it is immutable.
 * <pre>{@code
 * Banner banner = Banner.of(
 *                 Content.of("<gold>  █▀ █▀▀   <white>Socialismus v<gray>{version}"),
 *                 Content.of("<gold>  ▄█ █▄▄   <white>Platform: <gray>{platform}"))
 *         .values(Values.of("version", version).and("platform", "Paper"));
 *
 * keystone.console().print(banner);
 * }</pre>
 */
@Getter
@EqualsAndHashCode
public final class Banner {
	private final List<Content> lines;
	private final Values values;

	private Banner(List<Content> lines, Values values) {
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
		return new Banner(List.copyOf(lines), Values.none());
	}

	/**
	 * Returns this banner with the values its lines see.
	 *
	 * @param values the values
	 * @return a new banner
	 */
	public @NotNull Banner values(@NotNull Values values) {
		return new Banner(lines, Objects.requireNonNull(values, "values"));
	}
}
