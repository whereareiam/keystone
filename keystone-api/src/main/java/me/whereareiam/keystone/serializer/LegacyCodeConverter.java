package me.whereareiam.keystone.serializer;

import me.whereareiam.keystone.type.LegacyCode;
import me.whereareiam.keystone.type.LegacyInput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Turns the legacy formatting codes of a text into MiniMessage tags.
 * <p>
 * It reads codes written with {@code &} or {@code §}: the colours {@code &0} to {@code &f}, the decorations
 * {@code &k} to {@code &o}, the reset {@code &r} and the hex colours {@code &#rrggbb} and
 * {@code &x&r&r&g&g&b&b}, in either case. A code counts only at the start of the text, after whitespace, right
 * after another code or, in MiniMessage text, right after a tag, so {@code R&D} and {@code ?a=1&b=2} stay as
 * typed.
 * <p>
 * Each code is handed to a filter as a {@link LegacyCode}. A code the filter accepts becomes a tag; any other stays
 * exactly as typed and changes nothing around it. The tags follow the vanilla client:
 * <ul>
 *   <li>a decoration adds to the colour and decorations before it;</li>
 *   <li>a colour ends every colour and decoration of the codes before it, so {@code &lBold &cRed} is red and not
 *   bold;</li>
 *   <li>{@code &r} ends them as well, returning to the style around the text rather than resetting it, so tags
 *   outside the converted text still apply;</li>
 *   <li>the result closes every tag it opens, so it can be placed inside other MiniMessage text.</li>
 * </ul>
 * <pre>{@code
 * LegacyCodeConverter converter = Serializers.legacyCodes();
 *
 * converter.toMiniMessage("&cHello &lworld", LegacyInput.PLAIN_TEXT);
 * // <red>Hello <bold>world</bold></red>
 *
 * converter.toMiniMessage("&c&kHello", LegacyInput.MINIMESSAGE,
 *         code -> !(code instanceof LegacyCode.Decoration));
 * // <red>&kHello</red>
 * }</pre>
 */
public interface LegacyCodeConverter {
	/**
	 * Turns every legacy code of a text into MiniMessage tags.
	 *
	 * @param text  the text with legacy codes
	 * @param input what the text around the codes is, which decides whether it is escaped
	 * @return the text as MiniMessage
	 */
	@NotNull
	default String toMiniMessage(@NotNull String text, @NotNull LegacyInput input) {
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
	@NotNull
	String toMiniMessage(
			@NotNull String text,
			@NotNull LegacyInput input,
			@NotNull Predicate<? super LegacyCode> allowed
	);
}
