package me.whereareiam.keystone.common.console;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Writes a component as text with ANSI escape codes: colours in 24 bits and the decorations a terminal shows (bold,
 * italic, underlined, strikethrough). Obfuscated text is written plainly.
 */
public final class AnsiSerializer {
	static final String RESET = "\u001B[0m";

	private static final Map<TextDecoration, Integer> DECORATIONS = new EnumMap<>(Map.of(
			TextDecoration.BOLD, 1,
			TextDecoration.ITALIC, 3,
			TextDecoration.UNDERLINED, 4,
			TextDecoration.STRIKETHROUGH, 9
	));

	/**
	 * Writes a component.
	 *
	 * @param component the component
	 * @return its text with ANSI escape codes, ending with a reset when it has any
	 */
	public @NotNull String serialize(@NotNull Component component) {
		Writer writer = new Writer();
		ComponentFlattener.basic().flatten(component, writer);

		return writer.finish();
	}

	private static final class Writer implements FlattenerListener {
		private final StringBuilder output = new StringBuilder();
		private final Deque<Shown> styles = new ArrayDeque<>();
		private boolean styled;

		private Writer() {
			styles.push(new Shown(null, EnumSet.noneOf(TextDecoration.class)));
		}

		@Override
		public void pushStyle(@NotNull Style style) {
			styles.push(styles.peek().with(style));
		}

		@Override
		public void component(@NotNull String text) {
			if (text.isEmpty()) return;

			Shown shown = styles.peek();
			if (shown.isPlain() && !styled) {
				output.append(text);
				return;
			}

			String codes = shown.codes();
			output.append(RESET).append(codes).append(text.replace("\n", RESET + "\n" + codes));
			styled = true;
		}

		@Override
		public void popStyle(@NotNull Style style) {
			styles.pop();
		}

		private String finish() {
			if (styled) output.append(RESET);
			return output.toString();
		}
	}

	private record Shown(@Nullable TextColor color, @NotNull Set<TextDecoration> decorations) {
		private Shown with(Style style) {
			TextColor next = style.color() != null ? style.color() : color;
			Set<TextDecoration> shown = decorations.isEmpty() ? EnumSet.noneOf(TextDecoration.class) : EnumSet.copyOf(decorations);
			for (TextDecoration decoration : DECORATIONS.keySet()) {
				TextDecoration.State state = style.decoration(decoration);
				if (state == TextDecoration.State.TRUE) shown.add(decoration);
				if (state == TextDecoration.State.FALSE) shown.remove(decoration);
			}

			return new Shown(next, shown);
		}

		private boolean isPlain() {
			return color == null && decorations.isEmpty();
		}

		private String codes() {
			StringBuilder codes = new StringBuilder();
			if (color != null)
				codes.append("\u001B[38;2;").append(color.red()).append(';').append(color.green()).append(';').append(color.blue()).append('m');

			for (TextDecoration decoration : decorations) codes.append("\u001B[").append(DECORATIONS.get(decoration)).append('m');

			return codes.toString();
		}
	}
}
