package me.whereareiam.keystone.common.serializer.legacy;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.type.LegacyCode;
import me.whereareiam.keystone.type.LegacyInput;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

/**
 * One conversion of a text: walks it once, writing the codes the filter accepts as tags and everything else as
 * typed, and keeps the tags it opened so a colour or {@code &r} can close them.
 */
@RequiredArgsConstructor
final class LegacyConversion {
	private final String text;
	private final LegacyInput input;
	private final Predicate<? super LegacyCode> allowed;

	private final StringBuilder output = new StringBuilder();
	private final StringBuilder literal = new StringBuilder();
	private final Deque<String> openTags = new ArrayDeque<>();
	private int codeEnd = -1;
	private boolean converted;

	/**
	 * Writes text that is no converted code as the input asks: for plain text with every {@code <} and {@code \\}
	 * escaped, so no tag survives, and for MiniMessage as it is.
	 *
	 * @param input what the text is
	 * @param text  the text
	 * @return the text to write
	 */
	@NotNull
	static String literal(@NotNull LegacyInput input, @NotNull String text) {
		if (input == LegacyInput.MINIMESSAGE) return text;

		StringBuilder escaped = new StringBuilder(text.length());
		for (int index = 0; index < text.length(); index++) {
			char value = text.charAt(index);
			if (value == '<' || value == '\\') escaped.append('\\');

			escaped.append(value);
		}

		return escaped.toString();
	}

	/**
	 * Converts the text.
	 *
	 * @return the text as MiniMessage, or {@code null} when no code was converted
	 */
	@Nullable
	String run() {
		int index = 0;
		while (index < text.length())
			index = step(index);

		flushLiteral();
		closeTags();

		return converted ? output.toString() : null;
	}

	private int step(int index) {
		LegacyCodeReader.Read read = readAt(index);
		if (read == null) {
			literal.append(text.charAt(index));
			return index + 1;
		}

		int end = index + read.length();
		codeEnd = end;
		if (allowed.test(read.code())) apply(read.code());
		else literal.append(text, index, end);

		return end;
	}

	@Nullable
	private LegacyCodeReader.Read readAt(int index) {
		if (!LegacyCodeReader.isMarker(text.charAt(index)) || !startsCode(index)) return null;

		return LegacyCodeReader.read(text, index);
	}

	private boolean startsCode(int index) {
		if (index == 0 || index == codeEnd) return true;

		char previous = text.charAt(index - 1);

		return Character.isWhitespace(previous) || (input == LegacyInput.MINIMESSAGE && previous == '>');
	}

	private void apply(@NotNull LegacyCode code) {
		flushLiteral();
		converted = true;
		if (code instanceof LegacyCode.Decoration decoration) {
			openTag(TextDecoration.NAMES.key(decoration.decoration()));
			return;
		}

		closeTags();
		if (code instanceof LegacyCode.NamedColor named) openTag(NamedTextColor.NAMES.key(named.color()));
		if (code instanceof LegacyCode.HexColor hex) openTag(hex.color().asHexString());
	}

	private void openTag(@NotNull String name) {
		output.append('<').append(name).append('>');
		openTags.push("</" + name + ">");
	}

	private void closeTags() {
		while (!openTags.isEmpty())
			output.append(openTags.pop());
	}

	private void flushLiteral() {
		if (literal.isEmpty()) return;

		output.append(literal(input, literal.toString()));
		literal.setLength(0);
	}
}
