package me.whereareiam.keystone.type;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

/**
 * A legacy formatting code found in a text, such as {@code &c}, {@code &l}, {@code &#ff0000} or {@code &r}.
 * <p>
 * {@link me.whereareiam.keystone.serializer.LegacyCodeConverter} hands each code it finds to a filter, which
 * decides from its kind whether the code becomes a MiniMessage tag or stays as typed:
 * <pre>{@code
 * Predicate<LegacyCode> coloursOnly = code -> code instanceof LegacyCode.NamedColor
 *         || code instanceof LegacyCode.HexColor;
 * }</pre>
 */
public sealed interface LegacyCode {
	/**
	 * The reset code, {@code &r}.
	 */
	@NotNull Reset RESET = new Reset();

	/**
	 * One of the sixteen named colours, {@code &0} to {@code &f}.
	 *
	 * @param color the colour the code stands for
	 */
	record NamedColor(@NotNull NamedTextColor color) implements LegacyCode {
	}

	/**
	 * A hex colour, written {@code &#rrggbb} or {@code &x&r&r&g&g&b&b}.
	 *
	 * @param color the colour the code stands for
	 */
	record HexColor(@NotNull TextColor color) implements LegacyCode {
	}

	/**
	 * A decoration: {@code &k} obfuscated, {@code &l} bold, {@code &m} strikethrough, {@code &n} underlined or
	 * {@code &o} italic.
	 *
	 * @param decoration the decoration the code turns on
	 */
	record Decoration(@NotNull TextDecoration decoration) implements LegacyCode {
	}

	/**
	 * The reset code, {@code &r}, which ends every colour and decoration of the codes before it; use
	 * {@link #RESET}.
	 */
	record Reset() implements LegacyCode {
	}
}
