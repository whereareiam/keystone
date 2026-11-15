package me.whereareiam.keystone.platform.bungeecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.integration.papiproxybridge.PapiProxyBridgeIntegration;
import me.whereareiam.keystone.platform.KeystoneBuilder;
import me.whereareiam.keystone.platform.format.AdventureV4Format;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Keystone on BungeeCord, which has no Adventure of its own: texts reach senders through the plugin's
 * {@link BungeeAudiences}, which the plugin creates when it enables and closes when it disables. The plugin shades
 * Adventure and relocates it.
 * <pre>{@code
 * audiences = BungeeAudiences.create(this);
 * Keystone<CommandSender> keystone = BungeeKeystone.builder(this, audiences)
 *         .semantica(semantica)
 *         .build();
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BungeeKeystone {
	/**
	 * Starts configuring Keystone for a plugin.
	 *
	 * @param plugin    the plugin using Keystone
	 * @param audiences the plugin's audiences
	 * @return the builder
	 */
	public static @NotNull Builder builder(@NotNull Plugin plugin, @NotNull BungeeAudiences audiences) {
		return new Builder(Objects.requireNonNull(plugin, "plugin"), Objects.requireNonNull(audiences, "audiences"));
	}

	/**
	 * Configures Keystone on BungeeCord.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Builder extends KeystoneBuilder<CommandSender, Builder> {
		private final Plugin plugin;
		private final BungeeAudiences audiences;

		@Override
		protected @NotNull Platform<CommandSender> platform() {
			return new BungeePlatform(plugin, audiences);
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
			return this.plugin.getProxy().getPluginManager().getPlugins().stream()
					.anyMatch(other -> other.getDescription().getName().equalsIgnoreCase(plugin));
		}

		@Override
		protected @NotNull Builder self() {
			return this;
		}
	}
}
