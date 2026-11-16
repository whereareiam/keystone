package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.type.part.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * A title as written: its text alone, or a map with {@code title}, {@code subtitle} and {@code times} in ticks.
 * <pre>{@code
 * title:
 *   title: "<gold>Vote"
 *   subtitle: "<gray>for rewards"
 *   times: { in: 10, stay: 70, out: 20 }
 * }</pre>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"title", "subtitle", "times"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TitleDocument {
	private static final Title DEFAULTS = Title.of(me.whereareiam.semantica.Content.of(""));

	private final @Nullable PartText title;
	private final @Nullable PartText subtitle;
	private final @Nullable Times times;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	static @NotNull TitleDocument read(
			@JsonProperty("title") @Nullable PartText title,
			@JsonProperty("subtitle") @Nullable PartText subtitle,
			@JsonProperty("times") @Nullable Times times
	) {
		return new TitleDocument(title, subtitle, times);
	}

	@JsonCreator
	static @NotNull TitleDocument written(@NotNull String title) {
		return new TitleDocument(PartText.written(title), null, null);
	}

	@JsonCreator
	static @NotNull TitleDocument shown(boolean shown) {
		return new TitleDocument(PartText.shown(shown), null, null);
	}

	static @NotNull TitleDocument of(@NotNull Title title) {
		Times times = Times.of(title);
		PartText subtitle = title.getSubtitle().getSource().isEmpty() ? null : PartText.of(title.getSubtitle());

		return new TitleDocument(PartText.of(title.getTitle()), subtitle, times.equals(Times.of(DEFAULTS)) ? null : times);
	}

	@JsonAnySetter
	void unknown(String name, JsonNode value) {
		throw new IllegalArgumentException("unknown field " + name + "; a title has title, subtitle and times");
	}

	@Nullable Title toPart(@Nullable String key) {
		if (title != null && !title.isShown()) return null;

		Title.TitleBuilder part = Title.builder().title(PartText.content(title, key, "a title"));
		if (subtitle != null) part.subtitle(subtitle.content(key));
		if (times != null) times.apply(part);

		return part.build();
	}

	/**
	 * How long a title fades in, stays and fades out, in ticks.
	 */
	@Getter
	@EqualsAndHashCode
	@JsonPropertyOrder({"in", "stay", "out"})
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Times {
		private static final long TICK_MILLIS = 50;

		private final int in;
		private final int stay;
		private final int out;

		@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
		static @NotNull Times read(
				@JsonProperty("in") @Nullable Integer in,
				@JsonProperty("stay") @Nullable Integer stay,
				@JsonProperty("out") @Nullable Integer out
		) {
			Times defaults = of(DEFAULTS);

			return new Times(
					ticks(in, defaults.in),
					ticks(stay, defaults.stay),
					ticks(out, defaults.out)
			);
		}

		static @NotNull Times of(@NotNull Title title) {
			return new Times(ticks(title.getFadeIn()), ticks(title.getStay()), ticks(title.getFadeOut()));
		}

		@JsonAnySetter
		void unknown(String name, JsonNode value) {
			throw new IllegalArgumentException("unknown field " + name + "; times are in, stay and out");
		}

		void apply(Title.TitleBuilder title) {
			title.fadeIn(duration(in)).stay(duration(stay)).fadeOut(duration(out));
		}

		private static int ticks(@Nullable Integer written, int defaults) {
			if (written == null) return defaults;
			if (written < 0) throw new IllegalArgumentException("times are ticks, 0 or more: " + written);

			return written;
		}

		private static int ticks(Duration duration) {
			return Math.toIntExact(duration.toMillis() / TICK_MILLIS);
		}

		private static Duration duration(int ticks) {
			return Duration.ofMillis(ticks * TICK_MILLIS);
		}
	}
}
