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
import me.whereareiam.keystone.type.part.Sound;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A sound as written: its name alone, or a map with {@code name}, {@code source}, {@code volume} and {@code pitch}.
 * <pre>{@code
 * sound: { name: block.note_block.pling, volume: 1, pitch: 1.2 }
 * }</pre>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "source", "volume", "pitch"})
@Builder(access = AccessLevel.PRIVATE)
public final class SoundDocument {
	private static final Sound DEFAULTS = Sound.of(Key.key("ui.button.click"));

	@JsonIgnore
	private final @NotNull Key name;
	@JsonIgnore
	private final @Nullable Source source;
	private final @Nullable Float volume;
	private final @Nullable Float pitch;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	static @NotNull SoundDocument read(
			@JsonProperty(value = "name", required = true) @NotNull String name,
			@JsonProperty("source") @Nullable String source,
			@JsonProperty("volume") @Nullable Float volume,
			@JsonProperty("pitch") @Nullable Float pitch
	) {
		return builder()
				.name(key(name))
				.source(source == null ? null : Names.of(source, Source.NAMES))
				.volume(volume)
				.pitch(pitch)
				.build();
	}

	@JsonCreator
	static @NotNull SoundDocument named(@NotNull String name) {
		return builder().name(key(name)).build();
	}

	static @NotNull SoundDocument of(@NotNull Sound sound) {
		return builder()
				.name(sound.getName())
				.source(sound.getSource() == DEFAULTS.getSource() ? null : sound.getSource())
				.volume(sound.getVolume() == DEFAULTS.getVolume() ? null : sound.getVolume())
				.pitch(sound.getPitch() == DEFAULTS.getPitch() ? null : sound.getPitch())
				.build();
	}

	@JsonProperty("name")
	@NotNull String keyName() {
		return name.asMinimalString();
	}

	@JsonProperty("source")
	@Nullable String sourceName() {
		return source == null ? null : Source.NAMES.key(source);
	}

	@JsonAnySetter
	void unknown(String name, JsonNode value) {
		throw new IllegalArgumentException("unknown field " + name + "; a sound has name, source, volume and pitch");
	}

	@NotNull Sound toPart() {
		Sound.SoundBuilder sound = Sound.builder().name(name);
		if (source != null) sound.source(source);
		if (volume != null) sound.volume(volume);
		if (pitch != null) sound.pitch(pitch);

		return sound.build();
	}

	private static Key key(String name) {
		try {
			return Key.key(name);
		} catch (RuntimeException exception) {
			throw new IllegalArgumentException("no valid sound name: " + name, exception);
		}
	}
}
