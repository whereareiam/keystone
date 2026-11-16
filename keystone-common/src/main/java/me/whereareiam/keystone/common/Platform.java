package me.whereareiam.keystone.common;

import me.whereareiam.keystone.Recipient;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * What a messenger needs of a server or proxy: its console, the plugin's log and a way to run a task later. Each
 * platform module implements it.
 */
public interface Platform {
	/**
	 * Returns the console, which banners are rendered for.
	 *
	 * @return the console
	 */
	@NotNull Recipient console();

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
