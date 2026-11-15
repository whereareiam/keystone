package me.whereareiam.keystone.type.part;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * A title in the middle of the screen, with a subtitle below it. The times default to the client's own: half a
 * second in, three and a half seconds shown and one second out. In a config the times are in ticks:
 * <pre>{@code
 * title:
 *   title: "{@chirper.restart-title}"
 *   subtitle: "<gray>in {seconds} seconds"
 *   times: { in: 10, stay: 60, out: 10 }
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public final class Title implements Part {
	/**
	 * The text in large letters.
	 */
	@NonNull
	private final Content title;

	/**
	 * The text below it; empty when not given.
	 */
	@NonNull
	@Builder.Default
	private final Content subtitle = Content.of("");

	/**
	 * How long the title fades in.
	 */
	@NonNull
	@Builder.Default
	private final Duration fadeIn = Duration.ofMillis(500);

	/**
	 * How long the title is shown.
	 */
	@NonNull
	@Builder.Default
	private final Duration stay = Duration.ofMillis(3500);

	/**
	 * How long the title fades out.
	 */
	@NonNull
	@Builder.Default
	private final Duration fadeOut = Duration.ofSeconds(1);

	/**
	 * Returns a title without subtitle, shown for the client's own times.
	 *
	 * @param title the text in large letters
	 * @return the part
	 */
	public static @NotNull Title of(@NotNull Content title) {
		return builder().title(title).build();
	}
}
