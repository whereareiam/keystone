package me.whereareiam.keystone.platform.bukkit.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.integration.placeholderapi.PlaceholderApiIntegration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The integrations Bukkit and Paper share.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitIntegrations {
	/**
	 * Returns the shared integrations.
	 *
	 * @return PlaceholderAPI
	 */
	public static @NotNull List<Integration> shared() {
		return List.of(new PlaceholderApiIntegration());
	}
}
