package me.whereareiam.keystone;

import me.whereareiam.keystone.type.LegacyCode;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Reads text a player wrote as MiniMessage with only the tags a {@link me.whereareiam.semantica.format.adventure.TagPolicy}
 * allows; any other tag stays as typed. It is immutable: {@link #legacyCodes(Predicate)} returns a new markup.
 * <p>
 * Legacy codes written with {@code &} or {@code §} are read when a filter accepts them: the colours {@code &0} to
 * {@code &f}, the decorations {@code &k} to {@code &o}, the reset {@code &r} and the hex colours {@code &#rrggbb}
 * and {@code &x&r&r&g&g&b&b}, in either case. A code counts only at the start of the text, after whitespace,
 * right after another code or right after a tag, so {@code R&D} and {@code ?a=1&b=2} stay as typed. The codes
 * follow the vanilla client:
 * <ul>
 *   <li>a decoration adds to the colour and decorations before it;</li>
 *   <li>a colour ends every colour and decoration of the codes before it, so {@code &lBold &cRed} is red and not
 *   bold;</li>
 *   <li>{@code &r} ends them as well, returning to the style around the text rather than resetting it.</li>
 * </ul>
 * A code the filter refuses stays exactly as typed. A code becomes a tag before the policy applies, so a filter
 * should accept only codes whose tags the policy allows.
 * <pre>{@code
 * Markup markup = keystone.markup(TagPolicy.of(TagPolicy.Group.COLOR))
 *         .legacyCodes(code -> code instanceof LegacyCode.NamedColor);
 *
 * markup.read("&cHello <bold>world");   // red "Hello <bold>world"
 * }</pre>
 */
public interface Markup {
	/**
	 * Reads a text a player wrote.
	 *
	 * @param typed the text as written
	 * @return the text with the tags and legacy codes this markup allows applied
	 */
	@NotNull Component read(@NotNull String typed);

	/**
	 * Returns a markup that also reads the legacy codes a filter accepts.
	 *
	 * @param allowed accepts each legacy code that becomes a tag
	 * @return a new markup with the same policy
	 */
	@NotNull Markup legacyCodes(@NotNull Predicate<? super LegacyCode> allowed);
}
