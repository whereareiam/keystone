package me.whereareiam.keystone.platform.neoforge;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Recipient;
import me.whereareiam.keystone.common.Platform;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * NeoForge: command sources reach players through adventure-platform, the console's lines go to the mod's logger,
 * and later tasks run on the server's thread once their delay has passed.
 */
@RequiredArgsConstructor
final class NeoForgePlatform implements Platform {
	private final MinecraftServer server;
	private final MinecraftServerAudiences audiences;
	private final Logger logger;

	/**
	 * Returns the recipient of a command source. The console is the source whose sender is the server itself.
	 *
	 * @param source the command source
	 * @return its recipient
	 */
	@NotNull Recipient recipient(@NotNull CommandSourceStack source) {
		return new NeoForgeRecipient(source, audiences.audience(source), source.source == server);
	}

	@Override
	public @NotNull Recipient console() {
		return recipient(server.createCommandSourceStack());
	}

	@Override
	public void log(@NotNull String line) {
		logger.info(line);
	}

	@Override
	public void later(@NotNull Duration delay, @NotNull Runnable task) {
		CompletableFuture.delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS, server).execute(task);
	}
}
