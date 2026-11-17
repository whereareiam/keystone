package me.whereareiam.keystone.platform.bungeecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.markup.DefaultMarkup;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.integration.papiproxybridge.PapiProxyBridgeIntegration;
import me.whereareiam.keystone.platform.MessengerBuilder;
import me.whereareiam.keystone.platform.format.AdventureV4Format;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Keystone on BungeeCord, which has no Adventure of its own: texts reach senders through the plugin's
 * {@link BungeeAudiences}, which the plugin creates when it enables and closes when it disables. The plugin shades
 * Adventure and relocates it.
 * <pre>{@code
 * audiences = BungeeAudiences.create(this);
 * Messenger<CommandSender> messenger = BungeeKeystone.messenger(this, audiences)
 *         .semantica(semantica)
 *         .build();
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BungeeKeystone {
	/**
	 * Starts a messenger to BungeeCord's command senders.
	 *
	 * @param plugin    the plugin using Keystone
	 * @param audiences the plugin's audiences
	 * @return the builder
	 */
	public static @NotNull Builder<CommandSender> messenger(@NotNull Plugin plugin, @NotNull BungeeAudiences audiences) {
		BungeePlatform platform = platform(plugin, audiences);
		return new Builder<>(platform, platform::recipient);
	}

	/**
	 * Starts a messenger to the plugin's own sender type, such as its own player.
	 *
	 * @param plugin     the plugin using Keystone
	 * @param audiences  the plugin's audiences
	 * @param recipients returns the recipient of each of the plugin's senders
	 * @param <S>        the plugin's sender type
	 * @return the builder
	 */
	public static <S> @NotNull Builder<S> messenger(
			@NotNull Plugin plugin,
			@NotNull BungeeAudiences audiences,
			@NotNull Function<? super S, ? extends Recipient> recipients
	) {
		return new Builder<>(platform(plugin, audiences), Objects.requireNonNull(recipients, "recipients"));
	}

	/**
	 * Returns the markup of text players write, with only the tags a policy allows.
	 *
	 * @param policy the tags players may use
	 * @return the markup, reading no legacy codes until {@link Markup#legacyCodes(java.util.function.Predicate)}
	 */
	public static @NotNull Markup markup(@NotNull TagPolicy policy) {
		return new DefaultMarkup(new AdventureV4Format(), Objects.requireNonNull(policy, "policy"));
	}

	private static BungeePlatform platform(Plugin plugin, BungeeAudiences audiences) {
		return new BungeePlatform(Objects.requireNonNull(plugin, "plugin"), Objects.requireNonNull(audiences, "audiences"));
	}

	/**
	 * The messenger's builder on BungeeCord.
	 *
	 * @param <S> the plugin's sender type
	 */
	public static final class Builder<S> extends MessengerBuilder<S, Builder<S>> {
		private final BungeePlatform platform;

		private Builder(BungeePlatform platform, Function<? super S, ? extends Recipient> recipients) {
			super(recipients);
			this.platform = platform;
		}

		@Override
		protected @NotNull Platform platform() {
			return platform;
		}

		@Override
		protected @NotNull AdventureFormat format() {
			return new AdventureV4Format();
		}

		@Override
		protected @NotNull List<Integration> integrations() {
			return List.of(new PapiProxyBridgeIntegration());
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return platform.plugin().getProxy().getPluginManager().getPlugins().stream()
					.anyMatch(other -> other.getDescription().getName().equalsIgnoreCase(plugin));
		}

		@Override
		protected @NotNull Builder<S> self() {
			return this;
		}
	}
}
