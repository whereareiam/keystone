package me.whereareiam.keystone.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.markup.DefaultMarkup;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.integration.intercept.InterceptIntegration;
import me.whereareiam.keystone.integration.miniplaceholders.MiniPlaceholdersIntegration;
import me.whereareiam.keystone.integration.papiproxybridge.PapiProxyBridgeIntegration;
import me.whereareiam.keystone.platform.MessengerBuilder;
import me.whereareiam.keystone.platform.format.AdventureV5Format;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Keystone on Velocity. Command sources are Velocity's own audiences and a player's client language is their
 * effective locale; the console's lines go to the plugin's logger.
 * <pre>{@code
 * Messenger<CommandSource> messenger = VelocityKeystone.messenger(server, this, logger)
 *         .semantica(semantica)
 *         .build();
 * messenger.displays().add("myplugin", dataDirectory.resolve("display.yml"));
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VelocityKeystone {
	/**
	 * Starts a messenger to Velocity's command sources.
	 *
	 * @param server the proxy
	 * @param plugin the plugin's main instance
	 * @param logger the plugin's logger
	 * @return the builder
	 */
	public static @NotNull Builder<CommandSource> messenger(@NotNull ProxyServer server, @NotNull Object plugin, @NotNull Logger logger) {
		return messenger(server, plugin, logger, VelocityRecipient::new);
	}

	/**
	 * Starts a messenger to the plugin's own sender type, such as its own player.
	 *
	 * @param server     the proxy
	 * @param plugin     the plugin's main instance
	 * @param logger     the plugin's logger
	 * @param recipients returns the recipient of each of the plugin's senders
	 * @param <S>        the plugin's sender type
	 * @return the builder
	 */
	public static <S> @NotNull Builder<S> messenger(
			@NotNull ProxyServer server,
			@NotNull Object plugin,
			@NotNull Logger logger,
			@NotNull Function<? super S, ? extends Recipient> recipients
	) {
		VelocityPlatform platform = new VelocityPlatform(
				Objects.requireNonNull(server, "server"),
				Objects.requireNonNull(plugin, "plugin"),
				Objects.requireNonNull(logger, "logger")
		);

		return new Builder<>(server, platform, recipients);
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
	 * The messenger's builder on Velocity.
	 *
	 * @param <S> the plugin's sender type
	 */
	public static final class Builder<S> extends MessengerBuilder<S, Builder<S>> {
		private final ProxyServer server;
		private final VelocityPlatform platform;

		private Builder(ProxyServer server, VelocityPlatform platform, Function<? super S, ? extends Recipient> recipients) {
			super(recipients);
			this.server = server;
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
			return List.of(new PapiProxyBridgeIntegration(), new MiniPlaceholdersIntegration(), new InterceptIntegration());
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return server.getPluginManager().getPlugins().stream()
					.map(container -> container.getDescription())
					.anyMatch(description -> description.getId().equalsIgnoreCase(plugin)
							|| description.getName().filter(plugin::equalsIgnoreCase).isPresent());
		}

		@Override
		protected @NotNull Builder<S> self() {
			return this;
		}
	}
}
