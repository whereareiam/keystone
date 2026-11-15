package me.whereareiam.keystone;

import me.whereareiam.keystone.recipient.Recipient;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

/**
 * Chooses a recipient's language before the language their client reports.
 * <p>
 * Keystone renders each text in a chain of languages: the languages of its resolvers, in the order they were added
 * (Intercept's choice when Intercept is present), then the client's language, then the fallback. Semantica looks a
 * text up along that chain, so a text missing in the chosen language is taken from the next one.
 * <pre>{@code
 * PaperKeystone.builder(plugin)
 *         .languages(languages -> languages
 *                 .fallback(Locale.ENGLISH)
 *                 .resolver(recipient -> recipient.uniqueId().flatMap(storedLanguages::get)))
 * }</pre>
 */
@FunctionalInterface
public interface LanguageResolver {
	/**
	 * Returns the language a recipient chose.
	 *
	 * @param recipient the recipient
	 * @return the language, or empty when this resolver has none for them
	 */
	@NotNull Optional<Locale> resolve(@NotNull Recipient recipient);
}
