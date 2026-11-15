package me.whereareiam.keystone.recipient;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Finds the recipients of a platform.
 *
 * @param <S> the platform's command sender, such as Bukkit's {@code CommandSender} or Velocity's
 *            {@code CommandSource}
 */
public interface Recipients<S> {
	/**
	 * Returns the recipient of a command sender: a player, the {@link Console} or another sender.
	 *
	 * @param sender the sender
	 * @return its recipient
	 */
	@NotNull Recipient of(@NotNull S sender);

	/**
	 * Returns an online player.
	 *
	 * @param uniqueId the player's unique id
	 * @return the player's recipient, or empty when they are not online
	 */
	@NotNull Optional<Recipient> player(@NotNull UUID uniqueId);

	/**
	 * Returns every online player.
	 *
	 * @return the players online now
	 */
	@NotNull RecipientGroup players();

	/**
	 * Returns the online players with a permission.
	 *
	 * @param permission the permission
	 * @return the players online now who have it
	 */
	@NotNull RecipientGroup permission(@NotNull String permission);
}
