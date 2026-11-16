package me.whereareiam.keystone.common.render;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Recipient;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The languages a recipient's texts are rendered in: the language of each resolver, in order, then the client's
 * language, then the fallback. A language appears once, at its first place.
 */
@RequiredArgsConstructor
public final class LanguageChain {
	private final List<LanguageResolver> resolvers;
	private final Locale fallback;

	/**
	 * Returns the languages of a recipient, most preferred first.
	 *
	 * @param recipient the recipient
	 * @return the languages, ending with the fallback
	 */
	public @NotNull List<Locale> resolve(@NotNull Recipient recipient) {
		Set<Locale> chain = new LinkedHashSet<>();
		for (LanguageResolver resolver : resolvers) resolver.resolve(recipient).ifPresent(chain::add);

		recipient.clientLanguage().ifPresent(chain::add);
		chain.add(fallback);

		return List.copyOf(chain);
	}
}
