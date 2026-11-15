package me.whereareiam.keystone.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * What a platform's builder hands to Keystone.
 */
@Getter
@Builder
public final class KeystoneOptions {
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
	 * The display file, or {@code null} when the plugin keeps none.
	 */
	private final @Nullable Path display;
}
