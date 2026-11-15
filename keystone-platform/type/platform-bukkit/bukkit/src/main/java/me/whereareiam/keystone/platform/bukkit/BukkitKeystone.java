package me.whereareiam.keystone.platform.bukkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.platform.KeystoneBuilder;
import me.whereareiam.keystone.platform.bukkit.common.BukkitIntegrations;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPresence;
import me.whereareiam.keystone.platform.format.AdventureV4Format;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Keystone on Bukkit and Spigot, which have no Adventure of their own: texts reach senders through the plugin's
 * {@link BukkitAudiences}, which the plugin creates when it enables and closes when it disables. The plugin shades
 * Adventure and relocates it, so it never meets an Adventure the server ships.
 * <pre>{@code
 * audiences = BukkitAudiences.create(plugin);
 * Keystone<CommandSender> keystone = BukkitKeystone.builder(plugin, audiences)
 *         .semantica(semantica)
 *         .build();
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitKeystone {
	/**
	 * Starts configuring Keystone for a plugin.
	 *
	 * @param plugin    the plugin using Keystone
	 * @param audiences the plugin's audiences
	 * @return the builder
	 */
	public static @NotNull Builder builder(@NotNull Plugin plugin, @NotNull BukkitAudiences audiences) {
		return new Builder(Objects.requireNonNull(plugin, "plugin"), Objects.requireNonNull(audiences, "audiences"));
	}

	/**
	 * Configures Keystone on Bukkit.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Builder extends KeystoneBuilder<CommandSender, Builder> {
		private final Plugin plugin;
		private final BukkitAudiences audiences;

		@Override
		protected @NotNull Platform<CommandSender> platform() {
			return new BukkitAudiencePlatform(plugin, audiences);
		}

		@Override
		protected @NotNull AdventureFormat format() {
			return new AdventureV4Format();
		}

		@Override
		protected @NotNull List<Integration> integrations() {
			return BukkitIntegrations.shared();
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return BukkitPresence.enabled(this.plugin, plugin);
		}

		@Override
		protected @NotNull Builder self() {
			return this;
		}
	}
}
