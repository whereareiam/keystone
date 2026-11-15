package me.whereareiam.keystone.platform;

import me.whereareiam.keystone.Placeholders;
import me.whereareiam.keystone.common.integration.Integration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Which placeholders Keystone fills in. By default every shipped integration whose plugin is present: PlaceholderAPI
 * on Bukkit and Paper, PAPIProxyBridge on proxies, MiniPlaceholders on Paper and Velocity.
 * <pre>{@code
 * .placeholders(placeholders -> placeholders
 *         .disable(MiniPlaceholdersIntegration.class)
 *         .add(groupPlaceholders))
 * }</pre>
 */
public final class PlaceholderOptions {
	private final List<Placeholders> added = new ArrayList<>();
	private final Set<Class<? extends Integration>> disabled;

	PlaceholderOptions(@NotNull Set<Class<? extends Integration>> disabled) {
		this.disabled = disabled;
	}

	/**
	 * Adds placeholders of the plugin's own, filled after those of the integrations.
	 *
	 * @param placeholders the placeholders
	 * @return these options
	 */
	public @NotNull PlaceholderOptions add(@NotNull Placeholders placeholders) {
		added.add(Objects.requireNonNull(placeholders, "placeholders"));
		return this;
	}

	/**
	 * Leaves out a shipped integration even when its plugin is present.
	 *
	 * @param integration the integration's class, such as {@code PlaceholderApiIntegration.class}
	 * @return these options
	 */
	public @NotNull PlaceholderOptions disable(@NotNull Class<? extends Integration> integration) {
		disabled.add(Objects.requireNonNull(integration, "integration"));
		return this;
	}

	@NotNull List<Placeholders> added() {
		return added;
	}
}
