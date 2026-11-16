package me.whereareiam.keystone.platform.paper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.markup.DefaultMarkup;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.integration.miniplaceholders.MiniPlaceholdersIntegration;
import me.whereareiam.keystone.platform.MessengerBuilder;
import me.whereareiam.keystone.platform.bukkit.common.BukkitIntegrations;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPresence;
import me.whereareiam.keystone.platform.format.AdventureV5Format;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Keystone on Paper. Senders are Paper's own audiences, a player's client language is {@code Player#locale()},
 * and boss bars are hidden through Paper's asynchronous scheduler, so it also runs on Folia.
 * <pre>{@code
 * Messenger<CommandSender> messenger = PaperKeystone.messenger(plugin)
 *         .semantica(semantica)
 *         .build();
 * messenger.displays().add(plugin.getName(), plugin.getDataFolder().toPath().resolve("display.yml"));
 *
 * Markup chat = PaperKeystone.markup(TagPolicy.of(TagPolicy.Group.COLOR));
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaperKeystone {
	/**
	 * Starts a messenger to Paper's command senders.
	 *
	 * @param plugin the plugin using Keystone
	 * @return the builder
	 */
	public static @NotNull Builder<CommandSender> messenger(@NotNull Plugin plugin) {
		PaperPlatform platform = new PaperPlatform(Objects.requireNonNull(plugin, "plugin"));
		return new Builder<>(platform, platform::recipient);
	}

	/**
	 * Starts a messenger to the plugin's own sender type, such as its own player.
	 *
	 * @param plugin     the plugin using Keystone
	 * @param recipients returns the recipient of each of the plugin's senders
	 * @param <S>        the plugin's sender type
	 * @return the builder
	 */
	public static <S> @NotNull Builder<S> messenger(@NotNull Plugin plugin, @NotNull Function<? super S, ? extends Recipient> recipients) {
		return new Builder<>(new PaperPlatform(Objects.requireNonNull(plugin, "plugin")), recipients);
	}

	/**
	 * Returns the markup of text players write, with only the tags a policy allows.
	 *
	 * @param policy the tags players may use
	 * @return the markup, reading no legacy codes until {@link Markup#legacyCodes(java.util.function.Predicate)}
	 */
	public static @NotNull Markup markup(@NotNull TagPolicy policy) {
		return new DefaultMarkup(new AdventureV5Format(), Objects.requireNonNull(policy, "policy"));
	}

	/**
	 * The messenger's builder on Paper.
	 *
	 * @param <S> the plugin's sender type
	 */
	public static final class Builder<S> extends MessengerBuilder<S, Builder<S>> {
		private final PaperPlatform platform;

		private Builder(PaperPlatform platform, Function<? super S, ? extends Recipient> recipients) {
			super(recipients);
			this.platform = platform;
		}

		@Override
		protected @NotNull Platform platform() {
			return platform;
		}

		@Override
		protected @NotNull AdventureFormat format() {
			return new AdventureV5Format();
		}

		@Override
		protected @NotNull List<Integration> integrations() {
			List<Integration> integrations = new ArrayList<>(BukkitIntegrations.shared());
			integrations.add(new MiniPlaceholdersIntegration());

			return integrations;
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return BukkitPresence.enabled(platform.plugin(), plugin);
		}

		@Override
		protected @NotNull Builder<S> self() {
			return this;
		}
	}
}
