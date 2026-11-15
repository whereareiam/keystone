package me.whereareiam.keystone.platform.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.integration.miniplaceholders.MiniPlaceholdersIntegration;
import me.whereareiam.keystone.integration.papiproxybridge.PapiProxyBridgeIntegration;
import me.whereareiam.keystone.platform.KeystoneBuilder;
import me.whereareiam.keystone.platform.format.AdventureV5Format;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

/**
 * Keystone on Velocity. Command sources are Velocity's own audiences and a player's client language is their
 * effective locale; the console's lines go to the plugin's logger.
 * <pre>{@code
 * Keystone<CommandSource> keystone = VelocityKeystone.builder(server, this, logger)
 *         .semantica(semantica)
 *         .display(dataDirectory.resolve("display.yml"))
 *         .build();
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VelocityKeystone {
	/**
	 * Starts configuring Keystone for a plugin.
	 *
	 * @param server the proxy
	 * @param plugin the plugin's main instance, which owns scheduled tasks
	 * @param logger the plugin's logger
	 * @return the builder
	 */
	public static @NotNull Builder builder(@NotNull ProxyServer server, @NotNull Object plugin, @NotNull Logger logger) {
		return new Builder(
				Objects.requireNonNull(server, "server"),
				Objects.requireNonNull(plugin, "plugin"),
				Objects.requireNonNull(logger, "logger")
		);
	}

	/**
	 * Configures Keystone on Velocity.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Builder extends KeystoneBuilder<CommandSource, Builder> {
		private final ProxyServer server;
		private final Object plugin;
		private final Logger logger;

		@Override
		protected @NotNull Platform<CommandSource> platform() {
			return new VelocityPlatform(server, plugin, logger);
		}

		@Override
		protected @NotNull AdventureFormat format() {
			return new AdventureV5Format();
		}

		@Override
		protected @NotNull List<Integration> integrations() {
			return List.of(new PapiProxyBridgeIntegration(), new MiniPlaceholdersIntegration());
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return server.getPluginManager().getPlugins().stream()
					.map(container -> container.getDescription())
					.anyMatch(description -> description.getId().equalsIgnoreCase(plugin)
							|| description.getName().filter(plugin::equalsIgnoreCase).isPresent());
		}

		@Override
		protected @NotNull Builder self() {
			return this;
		}
	}
}
