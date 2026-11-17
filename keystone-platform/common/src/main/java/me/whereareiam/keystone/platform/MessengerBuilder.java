package me.whereareiam.keystone.platform;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Messenger;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.DefaultMessenger;
import me.whereareiam.keystone.common.MessengerOptions;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Configures a messenger for one plugin; each platform has its own, such as {@code PaperKeystone.messenger(plugin)}.
 * Only Semantica is required.
 * <pre>{@code
 * Messenger<CommandSender> messenger = PaperKeystone.messenger(plugin)
 *         .semantica(semantica)
 *         .policy(TagPolicy.defaults().allow(TagPolicy.Group.CLICK, TagPolicy.Group.HOVER))
 *         .languages(languages -> languages.fallback(Locale.ENGLISH))
 *         .placeholders(placeholders -> placeholders.add(groupPlaceholders))
 *         .hold(recipient -> !loggedIn(recipient))
 *         .build();
 * }</pre>
 *
 * @param <S> the plugin's sender type
 * @param <B> the platform's builder
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MessengerBuilder<S, B extends MessengerBuilder<S, B>> {
	private final Function<? super S, ? extends Recipient> recipients;
	private final Set<Class<? extends Integration>> disabled = new HashSet<>();
	private final LanguageOptions languages = new LanguageOptions(disabled);
	private final PlaceholderOptions placeholders = new PlaceholderOptions(disabled);
	private @Nullable Semantica semantica;
	private TagPolicy policy = TagPolicy.defaults();
	private Predicate<Recipient> hold = recipient -> false;

	/**
	 * Sets the Semantica that resolves the texts; required.
	 *
	 * @param semantica the plugin's Semantica
	 * @return this builder
	 */
	public @NotNull B semantica(@NotNull Semantica semantica) {
		this.semantica = Objects.requireNonNull(semantica, "semantica");
		return self();
	}

	/**
	 * Sets the tags translated texts and texts written in configs may use; {@link TagPolicy#defaults()} when not set.
	 *
	 * @param policy the tag policy
	 * @return this builder
	 */
	public @NotNull B policy(@NotNull TagPolicy policy) {
		this.policy = Objects.requireNonNull(policy, "policy");
		return self();
	}

	/**
	 * Configures the languages: the fallback, the plugin's own resolvers and which integrations to leave out.
	 *
	 * @param configure configures the options
	 * @return this builder
	 */
	public @NotNull B languages(@NotNull Consumer<LanguageOptions> configure) {
		configure.accept(languages);
		return self();
	}

	/**
	 * Configures the placeholders: the plugin's own and which integrations to leave out.
	 *
	 * @param configure configures the options
	 * @return this builder
	 */
	public @NotNull B placeholders(@NotNull Consumer<PlaceholderOptions> configure) {
		configure.accept(placeholders);
		return self();
	}

	/**
	 * Holds the texts of players while a rule says so, such as before they log in, until
	 * {@link Messenger#release(Object)}. No texts are held when not set; the console never is.
	 *
	 * @param hold tells whether a player's texts are held now
	 * @return this builder
	 */
	public @NotNull B hold(@NotNull Predicate<Recipient> hold) {
		this.hold = Objects.requireNonNull(hold, "hold");
		return self();
	}

	/**
	 * Builds the messenger, installing the integrations whose plugins are enabled now.
	 *
	 * @return the messenger
	 * @throws IllegalStateException when Semantica is not set
	 */
	public @NotNull Messenger<S> build() {
		if (semantica == null) throw new IllegalStateException("A messenger needs Semantica: call semantica(...) first");

		MessengerOptions.MessengerOptionsBuilder options = MessengerOptions.builder()
				.semantica(semantica)
				.format(format())
				.policy(policy)
				.fallback(languages.fallback() != null ? languages.fallback() : semantica.getDefaultLocale())
				.hold(hold);
		install(options);
		languages.resolvers().forEach(options::resolver);
		placeholders.added().forEach(options::placeholder);

		return new DefaultMessenger<>(platform(), recipients, options.build());
	}

	private void install(MessengerOptions.MessengerOptionsBuilder options) {
		Installation installation = new Installation() {
			@Override
			public void placeholders(@NotNull Placeholders placeholders) {
				options.placeholder(placeholders);
			}

			@Override
			public void resolver(@NotNull LanguageResolver resolver) {
				options.resolver(resolver);
			}
		};

		for (Integration integration : integrations()) {
			if (disabled.contains(integration.getClass()) || !present(integration.plugin())) continue;
			integration.install(installation);
		}
	}

	/**
	 * Returns the platform the messenger runs on.
	 *
	 * @return the platform
	 */
	protected abstract @NotNull Platform platform();

	/**
	 * Returns Semantica's Adventure format of the platform.
	 *
	 * @return the format
	 */
	protected abstract @NotNull AdventureFormat format();

	/**
	 * Returns the integrations the platform offers.
	 *
	 * @return the integrations
	 */
	protected abstract @NotNull List<Integration> integrations();

	/**
	 * Tells whether a plugin is enabled.
	 *
	 * @param plugin the plugin's name
	 * @return whether it is enabled now
	 */
	protected abstract boolean present(@NotNull String plugin);

	/**
	 * Returns this builder.
	 *
	 * @return this builder
	 */
	protected abstract @NotNull B self();
}
