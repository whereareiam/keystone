package me.whereareiam.keystone.platform.paper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.platform.Platform;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.integration.miniplaceholders.MiniPlaceholdersIntegration;
import me.whereareiam.keystone.platform.KeystoneBuilder;
import me.whereareiam.keystone.platform.bukkit.common.BukkitIntegrations;
import me.whereareiam.keystone.platform.bukkit.common.BukkitPresence;
import me.whereareiam.keystone.platform.format.AdventureV5Format;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Keystone on Paper. Senders are Paper's own audiences, a player's client language is {@code Player#locale()},
 * and boss bars are hidden through Paper's asynchronous scheduler, so it also runs on Folia.
 * <pre>{@code
 * Keystone<CommandSender> keystone = PaperKeystone.builder(plugin)
 *         .semantica(semantica)
 *         .display(plugin.getDataFolder().toPath().resolve("display.yml"))
 *         .build();
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaperKeystone {
	/**
	 * Starts configuring Keystone for a plugin.
	 *
	 * @param plugin the plugin using Keystone
	 * @return the builder
	 */
	public static @NotNull Builder builder(@NotNull Plugin plugin) {
		return new Builder(Objects.requireNonNull(plugin, "plugin"));
	}

	/**
	 * Configures Keystone on Paper.
	 */
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Builder extends KeystoneBuilder<CommandSender, Builder> {
		private final Plugin plugin;

		@Override
		protected @NotNull Platform<CommandSender> platform() {
			return new PaperPlatform(plugin);
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
			return BukkitPresence.enabled(this.plugin, plugin);
		}

		@Override
		protected @NotNull Builder self() {
			return this;
		}
	}
}
