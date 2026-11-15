package me.whereareiam.keystone.common.fixture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.semantica.catalog.Layer;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.syntax.icu.IcuSyntax;
import me.whereareiam.semantica.testing.MemorySource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * A Semantica with English and German texts:
 * <ul>
 *   <li>{@code greeting}: {@code <gold>Hello, {player}!} / {@code <gold>Hallo, {player}!}</li>
 *   <li>{@code cooldown}: wait {@code {time}} seconds, English only</li>
 *   <li>{@code prefix}: {@code [S]}</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestSemantica {
	public static final Locale GERMAN = Locale.GERMAN;

	public static @NotNull Semantica create() {
		MemorySource source = new MemorySource("test")
				.put(Locale.ENGLISH, "greeting", "<gold>Hello, {player}!")
				.put(GERMAN, "greeting", "<gold>Hallo, {player}!")
				.put(Locale.ENGLISH, "cooldown", "Wait {time} seconds.")
				.put(Locale.ENGLISH, "prefix", "[S]")
				.put(Locale.ENGLISH, "restart", "Restart in {seconds} seconds");

		return Semantica.builder()
				.syntax(new IcuSyntax())
				.defaultLocale(Locale.ENGLISH)
				.layer(Layer.of(source))
				.watch(false)
				.build();
	}
}
