package me.whereareiam.keystone.platform;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.common.integration.Integration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Which languages Keystone renders in. By default a recipient's languages are Intercept's choice when Intercept is
 * present, then the client's language, then the fallback, which is Semantica's default locale.
 * <pre>{@code
 * .languages(languages -> languages
 *         .fallback(Locale.ENGLISH)
 *         .resolver(recipient -> recipient.uniqueId().flatMap(storedLanguages::get)))
 * }</pre>
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class LanguageOptions {
	private final List<LanguageResolver> resolvers = new ArrayList<>();
	private final Set<Class<? extends Integration>> disabled;
	private @Nullable Locale fallback;

	/**
	 * Sets the language after every other, which is also the console's.
	 *
	 * @param fallback the language
	 * @return these options
	 */
	public @NotNull LanguageOptions fallback(@NotNull Locale fallback) {
		this.fallback = Objects.requireNonNull(fallback, "fallback");
		return this;
	}

	/**
	 * Adds a resolver, asked after those of the integrations and before the client's language.
	 *
	 * @param resolver the resolver
	 * @return these options
	 */
	public @NotNull LanguageOptions resolver(@NotNull LanguageResolver resolver) {
		resolvers.add(Objects.requireNonNull(resolver, "resolver"));
		return this;
	}

	/**
	 * Leaves out a shipped integration even when its plugin is present, such as Intercept's language.
	 *
	 * @param integration the integration's class
	 * @return these options
	 */
	public @NotNull LanguageOptions disable(@NotNull Class<? extends Integration> integration) {
		disabled.add(Objects.requireNonNull(integration, "integration"));
		return this;
	}

	@NotNull List<LanguageResolver> resolvers() {
		return resolvers;
	}

	@Nullable Locale fallback() {
		return fallback;
	}
}
