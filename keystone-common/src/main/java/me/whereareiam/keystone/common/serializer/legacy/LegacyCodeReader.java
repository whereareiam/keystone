package me.whereareiam.keystone.common.serializer.legacy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.type.LegacyCode;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the legacy code that starts at a marker, {@code &} or {@code §}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LegacyCodeReader {
	private static final String COLOR_CODES = "0123456789abcdef";
	private static final List<NamedTextColor> COLORS = List.of(
			NamedTextColor.BLACK,
			NamedTextColor.DARK_BLUE,
			NamedTextColor.DARK_GREEN,
			NamedTextColor.DARK_AQUA,
			NamedTextColor.DARK_RED,
			NamedTextColor.DARK_PURPLE,
			NamedTextColor.GOLD,
			NamedTextColor.GRAY,
			NamedTextColor.DARK_GRAY,
			NamedTextColor.BLUE,
			NamedTextColor.GREEN,
			NamedTextColor.AQUA,
			NamedTextColor.RED,
			NamedTextColor.LIGHT_PURPLE,
			NamedTextColor.YELLOW,
			NamedTextColor.WHITE
	);
	private static final Map<Character, TextDecoration> DECORATIONS = Map.of(
			'k', TextDecoration.OBFUSCATED,
			'l', TextDecoration.BOLD,
			'm', TextDecoration.STRIKETHROUGH,
			'n', TextDecoration.UNDERLINED,
			'o', TextDecoration.ITALIC
	);
	private static final Map<Character, LegacyCode> SINGLE_CODES = singleCodes();

	private static final int SINGLE_LENGTH = 2;
	private static final int HEX_LENGTH = 8;
	private static final int SPREAD_HEX_LENGTH = 14;

	static boolean isMarker(char value) {
		return value == '&' || value == '§';
	}

	/**
	 * Reads the code whose marker is at an index.
	 *
	 * @param text  the text
	 * @param index the index of the marker
	 * @return the code and how many characters it takes, or {@code null} when the marker starts no code
	 */
	@Nullable
	static Read read(@NotNull String text, int index) {
		if (index + 1 >= text.length()) return null;

		char code = Character.toLowerCase(text.charAt(index + 1));
		LegacyCode single = SINGLE_CODES.get(code);
		if (single != null) return new Read(single, SINGLE_LENGTH);

		if (code == '#') return readHex(text, index);
		if (code == 'x') return readSpreadHex(text, index);

		return null;
	}

	@Nullable
	private static Read readHex(@NotNull String text, int index) {
		if (index + HEX_LENGTH > text.length()) return null;

		String hex = text.substring(index + 2, index + HEX_LENGTH);
		for (int offset = 0; offset < hex.length(); offset++) {
			if (!isHexDigit(hex.charAt(offset))) return null;
		}

		return hexColor(hex, HEX_LENGTH);
	}

	@Nullable
	private static Read readSpreadHex(@NotNull String text, int index) {
		if (index + SPREAD_HEX_LENGTH > text.length()) return null;

		StringBuilder hex = new StringBuilder(6);
		for (int offset = 2; offset < SPREAD_HEX_LENGTH; offset += 2) {
			char marker = text.charAt(index + offset);
			char digit = text.charAt(index + offset + 1);
			if (!isMarker(marker) || !isHexDigit(digit)) return null;

			hex.append(digit);
		}

		return hexColor(hex.toString(), SPREAD_HEX_LENGTH);
	}

	@NotNull
	private static Read hexColor(@NotNull String hex, int length) {
		TextColor color = TextColor.color(Integer.parseInt(hex, 16));

		return new Read(new LegacyCode.HexColor(color), length);
	}

	private static boolean isHexDigit(char value) {
		return (value >= '0' && value <= '9')
				|| (value >= 'a' && value <= 'f')
				|| (value >= 'A' && value <= 'F');
	}

	@NotNull
	private static Map<Character, LegacyCode> singleCodes() {
		Map<Character, LegacyCode> codes = new HashMap<>();
		for (int index = 0; index < COLORS.size(); index++)
			codes.put(COLOR_CODES.charAt(index), new LegacyCode.NamedColor(COLORS.get(index)));

		DECORATIONS.forEach((code, decoration) -> codes.put(code, new LegacyCode.Decoration(decoration)));
		codes.put('r', LegacyCode.RESET);

		return Map.copyOf(codes);
	}

	/**
	 * A code read from a text.
	 *
	 * @param code   the code
	 * @param length how many characters it takes, its marker included
	 */
	record Read(@NotNull LegacyCode code, int length) {
	}
}
