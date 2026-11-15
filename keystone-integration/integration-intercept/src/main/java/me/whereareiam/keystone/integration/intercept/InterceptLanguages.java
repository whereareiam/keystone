package me.whereareiam.keystone.integration.intercept;

import lombok.RequiredArgsConstructor;
import me.whereareiam.intercept.language.LanguageService;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.recipient.Recipient;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The language a player chose in Intercept. Senders that are no player, and players who follow their client, have
 * none, and so has everyone while Intercept has not started.
 */
@RequiredArgsConstructor
final class InterceptLanguages implements LanguageResolver {
	private final Supplier<Optional<LanguageService>> languages;

	@Override
	public @NotNull Optional<Locale> resolve(@NotNull Recipient recipient) {
		return recipient.uniqueId().flatMap(uniqueId -> languages.get().flatMap(service -> service.chosen(uniqueId)));
	}
}
