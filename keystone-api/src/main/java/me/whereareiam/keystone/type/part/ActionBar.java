package me.whereareiam.keystone.type.part;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.whereareiam.semantica.Content;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A text in the action bar, above the hotbar.
 * <pre>{@code
 * actionbar: "<gold>{@chirper.vote}"
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActionBar implements Part {
	private final Content text;

	/**
	 * Returns the part.
	 *
	 * @param text the text, which may reference translated messages with {@code {@key}}
	 * @return the part
	 */
	public static @NotNull ActionBar of(@NotNull Content text) {
		return new ActionBar(Objects.requireNonNull(text, "text"));
	}
}
