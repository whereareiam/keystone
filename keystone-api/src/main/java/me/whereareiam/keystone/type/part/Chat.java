package me.whereareiam.keystone.type.part;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A text in the chat.
 * <pre>{@code
 * chat: "<gold>{@chirper.vote}"
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
public final class Chat implements Part {
	private final Content text;

	private Chat(Content text) {
		this.text = text;
	}

	/**
	 * Returns the part.
	 *
	 * @param text the text, which may reference translated messages with {@code {@key}}
	 * @return the part
	 */
	public static @NotNull Chat of(@NotNull Content text) {
		return new Chat(Objects.requireNonNull(text, "text"));
	}
}
