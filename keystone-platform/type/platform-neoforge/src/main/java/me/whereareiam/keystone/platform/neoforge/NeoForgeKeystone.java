package me.whereareiam.keystone.platform.neoforge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.keystone.Markup;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import me.whereareiam.keystone.common.integration.Integration;
import me.whereareiam.keystone.common.markup.DefaultMarkup;
import me.whereareiam.keystone.common.render.AdventureFormat;
import me.whereareiam.keystone.platform.MessengerBuilder;
import me.whereareiam.keystone.platform.format.AdventureV4Format;
import me.whereareiam.semantica.format.adventure.TagPolicy;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Keystone on a NeoForge server, the dedicated one or the one inside a client. Texts reach command sources through
 * adventure-platform-neoforge, which the mod ships as a nested jar together with the Adventure this module brings;
 * a player's client language is the one their client reports, and the console's lines go to the mod's logger.
 * <p>
 * A messenger belongs to one server: the mod builds it when the server starts and drops it when the server stops,
 * since a client starts a new server for every world it opens.
 * <pre>{@code
 * @SubscribeEvent
 * void starting(ServerStartingEvent event) {
 *     messenger = NeoForgeKeystone.messenger(event.getServer(), LOGGER)
 *             .semantica(semantica)
 *             .build();
 * }
 *
 * messenger.send(context.getSource(), messages.reloaded());
 * messenger.send(player.createCommandSourceStack(), messages.welcome(player.getScoreboardName()));
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NeoForgeKeystone {
	/**
	 * Starts a messenger to the server's command sources: players, the console and other senders of commands. A
	 * player is reached through {@code player.createCommandSourceStack()}.
	 *
	 * @param server the server
	 * @param logger the mod's logger
	 * @return the builder
	 */
	public static @NotNull Builder<CommandSourceStack> messenger(@NotNull MinecraftServer server, @NotNull Logger logger) {
		NeoForgePlatform platform = platform(server, logger);
		return new Builder<>(platform, platform::recipient);
	}

	/**
	 * Starts a messenger to the mod's own sender type, such as its own player.
	 *
	 * @param server     the server
	 * @param logger     the mod's logger
	 * @param recipients returns the recipient of each of the mod's senders
	 * @param <S>        the mod's sender type
	 * @return the builder
	 */
	public static <S> @NotNull Builder<S> messenger(
			@NotNull MinecraftServer server,
			@NotNull Logger logger,
			@NotNull Function<? super S, ? extends Recipient> recipients
	) {
		return new Builder<>(platform(server, logger), Objects.requireNonNull(recipients, "recipients"));
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

	private static NeoForgePlatform platform(MinecraftServer server, Logger logger) {
		Objects.requireNonNull(server, "server");
		return new NeoForgePlatform(server, MinecraftServerAudiences.of(server), Objects.requireNonNull(logger, "logger"));
	}

	/**
	 * The messenger's builder on NeoForge.
	 *
	 * @param <S> the mod's sender type
	 */
	public static final class Builder<S> extends MessengerBuilder<S, Builder<S>> {
		private final NeoForgePlatform platform;

		Builder(NeoForgePlatform platform, Function<? super S, ? extends Recipient> recipients) {
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
			return List.of();
		}

		@Override
		protected boolean present(@NotNull String plugin) {
			return ModList.get().isLoaded(plugin);
		}

		@Override
		protected @NotNull Builder<S> self() {
			return this;
		}
	}
}
