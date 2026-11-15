package me.whereareiam.keystone.integration.miniplaceholders;

import me.whereareiam.keystone.common.integration.Installation;
import me.whereareiam.keystone.common.integration.Integration;
import org.jetbrains.annotations.NotNull;

/**
 * MiniPlaceholders on Paper and Velocity, which share their Adventure with it: its global placeholders, such as
 * {@code <server_online>}, are tags of the literal parts of each text. Its placeholders of one audience need
 * MiniMessage's target, which Semantica's renderer does not pass yet, so they stay as written.
 */
public final class MiniPlaceholdersIntegration implements Integration {
	@Override
	public @NotNull String plugin() {
		return "MiniPlaceholders";
	}

	@Override
	public void install(@NotNull Installation installation) {
		installation.placeholders(new MiniPlaceholdersTags(io.github.miniplaceholders.api.MiniPlaceholders::globalPlaceholders));
	}
}
