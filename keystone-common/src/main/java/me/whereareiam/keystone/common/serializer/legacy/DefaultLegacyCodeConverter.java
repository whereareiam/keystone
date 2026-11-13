package me.whereareiam.keystone.common.serializer.legacy;

import me.whereareiam.keystone.serializer.LegacyCodeConverter;
import me.whereareiam.keystone.type.LegacyCode;
import me.whereareiam.keystone.type.LegacyInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * The {@link LegacyCodeConverter} of Keystone; it keeps no state, so one instance serves every caller.
 */
public final class DefaultLegacyCodeConverter implements LegacyCodeConverter {
	@Override
	@NotNull
	public String toMiniMessage(
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
	@Nullable
	public String convert(
			@NotNull String text,
			@NotNull LegacyInput input,
			@NotNull Predicate<? super LegacyCode> allowed
	) {
		return new LegacyConversion(text, input, allowed).run();
	}
}
