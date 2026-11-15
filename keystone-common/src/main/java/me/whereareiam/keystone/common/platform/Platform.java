package me.whereareiam.keystone.common.platform;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * What Keystone needs of a server or proxy: its senders, its console's log and a way to run a task later. Each
 * platform module implements it.
 *
 * @param <S> the platform's command sender
 */
public interface Platform<S> {
	/**
	 * Returns the sender behind a command sender of the platform.
	 *
	 * @param sender the platform's sender
	 * @return the sender
	 */
	@NotNull Sender sender(@NotNull S sender);

	/**
	 * Returns the console.
	 *
	 * @return the console's sender
	 */
	@NotNull Sender console();

	/**
	 * Returns the players online now.
	 *
	 * @return the players
	 */
	@NotNull Collection<Sender> players();

	/**
	 * Returns an online player.
	 *
	 * @param uniqueId the player's unique id
	 * @return the player, or empty when they are not online
	 */
	@NotNull Optional<Sender> player(@NotNull UUID uniqueId);

	/**
	 * Writes one line to the plugin's log, at the level of information.
	 *
	 * @param line the line, which may hold ANSI escape codes
	 */
	void log(@NotNull String line);

	/**
	 * Runs a task once after a delay, such as hiding a boss bar.
	 *
	 * @param delay how long to wait
	 * @param task  the task
	 */
	void later(@NotNull Duration delay, @NotNull Runnable task);
}
