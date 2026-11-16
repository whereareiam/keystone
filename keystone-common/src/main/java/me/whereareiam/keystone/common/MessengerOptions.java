package me.whereareiam.keystone.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.format.adventure.TagPolicy;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * What a platform's builder hands to the messenger.
 */
@Getter
@Builder
public final class MessengerOptions {
	/**
	 * Resolves and checks the texts.
	 */
	@NonNull
	private final Semantica semantica;

	/**
	 * Semantica's Adventure format of the platform.
	 */
	@NonNull
	private final AdventureFormat format;

	/**
	 * The tags translated texts and config texts may use.
	 */
	@NonNull
	private final TagPolicy policy;

	/**
	 * The language after every other, of the console too.
	 */
	@NonNull
	private final Locale fallback;

	/**
	 * The resolvers asked before the client's language, in order.
	 */
	@Singular
	private final List<LanguageResolver> resolvers;

	/**
	 * The placeholders filled in for each recipient, in order.
	 */
	@Singular("placeholder")
	private final List<Placeholders> placeholders;

	/**
	 * Tells whether texts for a player are held; none are when not given.
	 */
	@NonNull
	@Builder.Default
	private final Predicate<Recipient> hold = recipient -> false;
}
