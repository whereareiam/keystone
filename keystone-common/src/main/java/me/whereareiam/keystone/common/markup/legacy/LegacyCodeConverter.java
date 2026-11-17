package me.whereareiam.keystone.common.markup.legacy;

import me.whereareiam.keystone.type.LegacyCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Turns the legacy formatting codes of a text into MiniMessage tags, by the rules {@link me.whereareiam.keystone.Markup}
 * describes: each code is handed to a filter, a code it accepts becomes a tag and any other stays as typed. The
 * result closes every tag it opens, so it can be placed inside other MiniMessage text. It keeps no state, so one
 * instance serves every caller.
 */
public final class LegacyCodeConverter {
	/**
	 * Turns every legacy code of a text into MiniMessage tags.
	 *
	 * @param text  the text with legacy codes
	 * @param input what the text around the codes is, which decides whether it is escaped
	 * @return the text as MiniMessage
	 */
	public @NotNull String toMiniMessage(@NotNull String text, @NotNull LegacyInput input) {
		return toMiniMessage(text, input, code -> true);
	}

	/**
	 * Turns the legacy codes of a text that a filter accepts into MiniMessage tags; the others stay as typed.
	 *
	 * @param text    the text with legacy codes
	 * @param input   what the text around the codes is, which decides whether it is escaped
	 * @param allowed accepts each code that becomes a tag
	 * @return the text as MiniMessage
	 */
	public @NotNull String toMiniMessage(
			@NotNull String text,
			@NotNull LegacyInput input,
			@NotNull Predicate<? super LegacyCode> allowed
	) {
		String converted = convert(text, input, allowed);
		if (converted != null) return converted;

		return LegacyConversion.literal(input, text);
	}

	/**
	 * Turns the legacy codes of a text that a filter accepts into MiniMessage tags, telling apart a text in which
	 * no code was converted.
	 *
	 * @param text    the text with legacy codes
	 * @param input   what the text around the codes is
	 * @param allowed accepts each code that becomes a tag
	 * @return the text as MiniMessage, or {@code null} when the filter accepted no code of it
	 */
	public @Nullable String convert(
			@NotNull String text,
			@NotNull LegacyInput input,
			@NotNull Predicate<? super LegacyCode> allowed
	) {
		return new LegacyConversion(text, input, allowed).run();
	}
}
