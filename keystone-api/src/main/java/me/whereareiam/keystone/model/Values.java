package me.whereareiam.keystone.model;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The named values a text written in a config sees: its arguments, such as {@code {player}}, and the arguments of
 * the messages it references. It is immutable; {@link #and(String, Object)} returns new values.
 * <p>
 * A value is never read as markup: a player's name holding {@code <red>} is shown as typed. An Adventure component
 * is shown as it is.
 * <pre>{@code
 * recipient.send(announcement, Values.of("player", name).and("count", 3));
 * }</pre>
 */
@EqualsAndHashCode
public final class Values {
	private static final Values NONE = new Values(Map.of());

	private final Map<String, Object> values;

	private Values(Map<String, Object> values) {
		this.values = values;
	}

	/**
	 * Returns no values.
	 *
	 * @return the empty values
	 */
	public static @NotNull Values none() {
		return NONE;
	}

	/**
	 * Returns one value.
	 *
	 * @param name  the argument's name, without braces
	 * @param value the value: text, a number, a date or an Adventure component
	 * @return the values
	 */
	public static @NotNull Values of(@NotNull String name, @NotNull Object value) {
		return NONE.and(name, value);
	}

	/**
	 * Returns these values and one more, which replaces a value of the same name.
	 *
	 * @param name  the argument's name, without braces
	 * @param value the value: text, a number, a date or an Adventure component
	 * @return new values
	 */
	public @NotNull Values and(@NotNull String name, @NotNull Object value) {
		Map<String, Object> copy = new LinkedHashMap<>(values);
		copy.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value"));

		return new Values(Collections.unmodifiableMap(copy));
	}

	/**
	 * Returns the values by name, as Semantica takes them.
	 *
	 * @return an unmodifiable map
	 */
	public @NotNull Map<String, Object> asMap() {
		return values;
	}

	@Override
	public String toString() {
		return values.toString();
	}
}
