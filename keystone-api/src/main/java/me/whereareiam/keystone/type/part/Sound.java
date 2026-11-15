package me.whereareiam.keystone.type.part;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * A sound played to the recipient alone. In a config:
 * <pre>{@code
 * sound: { name: block.note_block.pling, volume: 1, pitch: 1.2 }
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public final class Sound implements Part {
	/**
	 * The sound, such as {@code minecraft:block.note_block.pling}.
	 */
	@NonNull
	private final Key name;

	/**
	 * The volume slider the sound follows on the client.
	 */
	@NonNull
	@Builder.Default
	private final net.kyori.adventure.sound.Sound.Source source = net.kyori.adventure.sound.Sound.Source.MASTER;

	/**
	 * How loud it is; {@code 1} is the sound's own volume.
	 */
	@Builder.Default
	private final float volume = 1F;

	/**
	 * How high it is, from {@code 0.5} to {@code 2}; {@code 1} is the sound's own pitch.
	 */
	@Builder.Default
	private final float pitch = 1F;

	/**
	 * Returns a sound at its own volume and pitch.
	 *
	 * @param name the sound, such as {@code Key.key("block.note_block.pling")}
	 * @return the part
	 */
	public static @NotNull Sound of(@NotNull Key name) {
		return builder().name(name).build();
	}
}
