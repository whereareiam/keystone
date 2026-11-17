package me.whereareiam.keystone.platform.neoforge;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * NeoForge: a command source reaches its player through the adventure-platform audiences of its own server, the
 * console's lines go to the mod's logger, and later tasks run on the running server's thread once their delay has
 * passed. The platform lives as long as the mod; the server it works with is the one running at the time.
 */
@RequiredArgsConstructor
final class NeoForgePlatform implements Platform {
	private final Supplier<@Nullable MinecraftServer> server;
	private final Function<MinecraftServer, MinecraftServerAudiences> audiences;
	private final Logger logger;

	/**
	 * Returns the recipient of a command source. The console is the source whose sender is its server itself.
	 *
	 * @param source the command source
	 * @return its recipient
	 */
	@NotNull Recipient recipient(@NotNull CommandSourceStack source) {
		MinecraftServer owner = source.getServer();
		return new NeoForgeRecipient(source, audiences.apply(owner).audience(source), source.source == owner);
	}

	@Override
	public @NotNull Recipient console() {
		return recipient(running().createCommandSourceStack());
	}

	@Override
	public void log(@NotNull String line) {
		logger.info(line);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS, running()).execute(task);
	}

	private MinecraftServer running() {
		MinecraftServer running = server.get();
		if (running == null) throw new IllegalStateException("No server is running");

		return running;
	}
}
