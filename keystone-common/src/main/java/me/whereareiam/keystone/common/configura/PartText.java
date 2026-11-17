package me.whereareiam.keystone.common.configura;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.whereareiam.semantica.Content;
import me.whereareiam.semantica.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The text of a part as written: a string, or in a display file {@code true} for the message's own text and
 * {@code false} for no part at all.
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PartText {
	static final PartText OWN = new PartText(null, true);
	static final PartText OFF = new PartText(null, false);

	private final @Nullable Content content;
	private final boolean shown;

	@JsonCreator
	static @NotNull PartText written(@NotNull String source) {
		return of(Content.of(source));
	}

	@JsonCreator
	static @NotNull PartText shown(boolean shown) {
		return shown ? OWN : OFF;
	}

	static @NotNull PartText of(@NotNull Content content) {
		return new PartText(content, true);
	}

	@JsonValue
	String source() {
		if (content == null) throw new IllegalStateException("Only written texts are written back");
		return content.getSource();
	}

	/**
	 * Tells whether the part is shown at all.
	 *
	 * @return {@code false} for {@code false}
	 */
	boolean isShown() {
		return shown;
	}

	/**
	 * Returns the text.
	 *
	 * @param key the key of the display file's entry, or {@code null} in a config
	 * @return the written text, or the message's own text for {@code true}
	 * @throws IllegalArgumentException for {@code true} in a config, which has no message of its own
	 */
	@NotNull Content content(@Nullable String key) {
		if (content != null) return content;
		if (key == null) throw new IllegalArgumentException("true stands for a message's own text, which only an entry of a display file has");

		return Content.of(Message.of(key));
	}

	/**
	 * Returns the text of a part that may leave its text out.
	 *
	 * @param text the text as written, or {@code null} when left out
	 * @param key  the key of the display file's entry, or {@code null} in a config
	 * @param part the part's name, for the error
	 * @return the text, the message's own when left out in an entry
	 */
	static @NotNull Content content(@Nullable PartText text, @Nullable String key, @NotNull String part) {
		if (text != null) return text.content(key);
		if (key == null) throw new IllegalArgumentException(part + " needs its text");

		return Content.of(Message.of(key));
	}
}
