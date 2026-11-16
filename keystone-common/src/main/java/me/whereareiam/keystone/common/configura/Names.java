package me.whereareiam.keystone.common.configura;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Reads the name of one of Adventure's enums as written in a config, such as {@code red} or {@code master}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Names {
	static <E extends Enum<E>> @NotNull E of(@NotNull String written, @NotNull Index<String, E> names) {
		E value = names.value(written.toLowerCase(Locale.ROOT));
		if (value == null) throw new IllegalArgumentException(written + " is none of " + names.keys());

		return value;
	}
}
