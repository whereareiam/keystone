package me.whereareiam.keystone.type.part;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

/**
 * A boss bar at the top of the screen, shown for a while and then hidden. By default it is white, full and shown
 * for five seconds. In a config:
 * <pre>{@code
 * bossbar:
 *   text: "<green>/vote"
 *   color: green
 *   overlay: progress
 *   progress: 1.0
 *   seconds: 10
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public final class BossBar implements Part {
	/**
	 * The text above the bar.
	 */
	private final Content text;

	/**
	 * The bar's colour.
	 */
	@Builder.Default
	private final net.kyori.adventure.bossbar.BossBar.Color color = net.kyori.adventure.bossbar.BossBar.Color.WHITE;

	/**
	 * Whether the bar is whole or split into notches.
	 */
	@Builder.Default
	private final net.kyori.adventure.bossbar.BossBar.Overlay overlay = net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS;

	/**
	 * How full the bar is, from {@code 0} to {@code 1}.
	 */
	@Builder.Default
	private final float progress = 1F;

	/**
	 * How long the bar is shown.
	 */
	@Builder.Default
	private final Duration duration = Duration.ofSeconds(5);

	private BossBar(
			@NotNull Content text,
			net.kyori.adventure.bossbar.BossBar.@NotNull Color color,
			net.kyori.adventure.bossbar.BossBar.@NotNull Overlay overlay,
			float progress,
			@NotNull Duration duration
	) {
		Objects.requireNonNull(duration, "duration");
		if (progress < 0 || progress > 1) throw new IllegalArgumentException("A boss bar's progress is from 0 to 1: " + progress);
		if (duration.isNegative() || duration.isZero()) throw new IllegalArgumentException("A boss bar is shown for a while: " + duration);

		this.text = Objects.requireNonNull(text, "text");
		this.color = Objects.requireNonNull(color, "color");
		this.overlay = Objects.requireNonNull(overlay, "overlay");
		this.progress = progress;
		this.duration = duration;
	}

	/**
	 * Returns a white, full boss bar shown for five seconds.
	 *
	 * @param text the text above the bar
	 * @return the part
	 */
	public static @NotNull BossBar of(@NotNull Content text) {
		return builder().text(text).build();
	}
}
