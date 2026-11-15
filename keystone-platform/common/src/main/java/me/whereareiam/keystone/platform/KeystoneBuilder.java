package me.whereareiam.keystone.platform;

import me.whereareiam.keystone.Keystone;
import me.whereareiam.keystone.LanguageResolver;
import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.DefaultKeystone;
import me.whereareiam.keystone.common.KeystoneOptions;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.semantica.common.Semantica;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Configures Keystone for one plugin; each platform has its own, such as {@code PaperKeystone.builder(plugin)}.
 * Only Semantica is required.
 * <pre>{@code
 * Keystone<CommandSender> keystone = PaperKeystone.builder(plugin)
 *         .semantica(semantica)
 *         .policy(TagPolicy.defaults().allow(TagPolicy.Group.CLICK, TagPolicy.Group.HOVER))
 *         .languages(languages -> languages.fallback(Locale.ENGLISH))
 *         .placeholders(placeholders -> placeholders.add(groupPlaceholders))
 *         .display(dataFolder.resolve("display.yml"))
 *         .build();
 * }</pre>
 *
 * @param <S> the platform's command sender
 * @param <B> the platform's builder
 */
public abstract class KeystoneBuilder<S, B extends KeystoneBuilder<S, B>> {
	private final Set<Class<? extends Integration>> disabled = new HashSet<>();
	private final LanguageOptions languages = new LanguageOptions(disabled);
	private final PlaceholderOptions placeholders = new PlaceholderOptions(disabled);
	private @Nullable Semantica semantica;
	private TagPolicy policy = TagPolicy.defaults();
	private @Nullable Path display;

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
	 * Values are never read as markup, whatever the policy.
	 *
	 * @param policy the tags
	 * @return this builder
	 */
	public @NotNull B policy(@NotNull TagPolicy policy) {
		this.policy = Objects.requireNonNull(policy, "policy");
		return self();
	}

	/**
	 * Configures the languages texts are rendered in.
	 *
	 * @param configure changes the language options
	 * @return this builder
	 */
	public @NotNull B languages(@NotNull Consumer<LanguageOptions> configure) {
		configure.accept(languages);
		return self();
	}

	/**
	 * Configures the placeholders filled in for each recipient.
	 *
	 * @param configure changes the placeholder options
	 * @return this builder
	 */
	public @NotNull B placeholders(@NotNull Consumer<PlaceholderOptions> configure) {
		configure.accept(placeholders);
		return self();
	}

	/**
	 * Sets the display file, {@code display.yml}, which lists the translated messages shown as more than chat. A
	 * missing file has no entries; without one every message goes to the chat.
	 *
	 * @param display the file
	 * @return this builder
	 */
	public @NotNull B display(@NotNull Path display) {
		this.display = Objects.requireNonNull(display, "display");
		return self();
	}

	/**
	 * Creates Keystone and reads the display file.
	 *
	 * @return Keystone
	 * @throws IllegalStateException                                 when Semantica was not set
	 * @throws me.whereareiam.keystone.exception.DisplayFileException when the display file cannot be read
	 */
	public @NotNull Keystone<S> build() {
		if (semantica == null) throw new IllegalStateException("Keystone needs Semantica: call semantica(...) first");

		KeystoneOptions.KeystoneOptionsBuilder options = KeystoneOptions.builder()
				.semantica(semantica)
				.format(format())
				.policy(policy)
				.fallback(languages.fallback() != null ? languages.fallback() : semantica.getDefaultLocale())
				.display(display);

		install(options);
		languages.resolvers().forEach(options::resolver);
		placeholders.added().forEach(options::placeholder);

		return new DefaultKeystone<>(platform(), options.build());
	}

	private void install(KeystoneOptions.KeystoneOptionsBuilder options) {
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
	 * Returns the platform the built Keystone runs on.
	 *
	 * @return the platform
	 */
	protected abstract @NotNull Platform<S> platform();

	/**
	 * Returns Semantica's Adventure format for the platform's Adventure.
	 *
	 * @return the format
	 */
	protected abstract @NotNull AdventureFormat format();

	/**
	 * Returns the integrations shipped for the platform, used when their plugin is present.
	 *
	 * @return the integrations, in the order they are installed
	 */
	protected abstract @NotNull List<Integration> integrations();

	/**
	 * Tells whether a plugin is loaded on the platform.
	 *
	 * @param plugin the plugin's name, compared without regard to case
	 * @return {@code true} when it is loaded
	 */
	protected abstract boolean present(@NotNull String plugin);

	/**
	 * Returns this builder as the platform's type.
	 *
	 * @return this builder
	 */
	protected abstract @NotNull B self();
}
