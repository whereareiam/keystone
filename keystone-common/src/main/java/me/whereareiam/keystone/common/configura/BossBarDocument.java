package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import me.whereareiam.keystone.type.part.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * A boss bar as written: its text alone, or a map with {@code text}, {@code color}, {@code overlay},
 * {@code progress} and {@code seconds}. Settings left out keep a boss bar's defaults.
 * <pre>{@code
 * bossbar: { text: "<green>/vote", color: green, seconds: 10 }
 * }</pre>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"text", "color", "overlay", "progress", "seconds"})
@Builder(access = AccessLevel.PRIVATE)
public final class BossBarDocument {
	private static final BossBar DEFAULTS = BossBar.of(me.whereareiam.semantica.Content.of(""));

	private final @Nullable PartText text;
	@JsonIgnore
	private final @Nullable Color color;
	@JsonIgnore
	private final @Nullable Overlay overlay;
	private final @Nullable Float progress;
	private final @Nullable Double seconds;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	static @NotNull BossBarDocument read(
			@JsonProperty("text") @Nullable PartText text,
			@JsonProperty("color") @Nullable String color,
			@JsonProperty("overlay") @Nullable String overlay,
			@JsonProperty("progress") @Nullable Float progress,
			@JsonProperty("seconds") @Nullable Double seconds
	) {
		return builder()
				.text(text)
				.color(color == null ? null : Names.of(color, Color.NAMES))
				.overlay(overlay == null ? null : Names.of(overlay, Overlay.NAMES))
				.progress(progress)
				.seconds(seconds)
				.build();
	}

	@JsonCreator
	static @NotNull BossBarDocument written(@NotNull String text) {
		return builder().text(PartText.written(text)).build();
	}

	@JsonCreator
	static @NotNull BossBarDocument shown(boolean shown) {
		return builder().text(PartText.shown(shown)).build();
	}

	static @NotNull BossBarDocument of(@NotNull BossBar bar) {
		return builder()
				.text(PartText.of(bar.getText()))
				.color(bar.getColor() == DEFAULTS.getColor() ? null : bar.getColor())
				.overlay(bar.getOverlay() == DEFAULTS.getOverlay() ? null : bar.getOverlay())
				.progress(bar.getProgress() == DEFAULTS.getProgress() ? null : bar.getProgress())
				.seconds(bar.getDuration().equals(DEFAULTS.getDuration()) ? null : bar.getDuration().toMillis() / 1000D)
				.build();
	}

	@JsonProperty("color")
	@Nullable String colorName() {
		return color == null ? null : Color.NAMES.key(color);
	}

	@JsonProperty("overlay")
	@Nullable String overlayName() {
		return overlay == null ? null : Overlay.NAMES.key(overlay);
	}

	@JsonAnySetter
	void unknown(String name, JsonNode value) {
		throw new IllegalArgumentException("unknown field " + name + "; a boss bar has text, color, overlay, progress and seconds");
	}

	@Nullable BossBar toPart(@Nullable String key) {
		if (text != null && !text.isShown()) return null;

		BossBar.BossBarBuilder bar = BossBar.builder().text(PartText.content(text, key, "a boss bar"));
		if (color != null) bar.color(color);
		if (overlay != null) bar.overlay(overlay);
		if (progress != null) bar.progress(progress);
		if (seconds != null) bar.duration(Duration.ofMillis(Math.round(seconds * 1000D)));

		return bar.build();
	}
}
